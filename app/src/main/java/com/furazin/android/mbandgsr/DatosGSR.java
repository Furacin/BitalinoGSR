package com.furazin.android.mbandgsr;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.furazin.android.api.Communication;
import com.furazin.android.api.bitalino.BITalinoCommunication;
import com.furazin.android.api.bitalino.BITalinoCommunicationFactory;
import com.furazin.android.api.bitalino.BITalinoDescription;
import com.furazin.android.api.bitalino.BITalinoException;
import com.furazin.android.api.bitalino.BITalinoFrame;
import com.furazin.android.api.bitalino.BITalinoState;
import com.furazin.android.api.bitalino.bth.OnBITalinoDataAvailable;
import com.furazin.android.mbandgsr.FirebaseBD.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.microsoft.band.BandClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.furazin.android.api.Constants.ACTION_COMMAND_REPLY;
import static com.furazin.android.api.Constants.ACTION_DATA_AVAILABLE;
import static com.furazin.android.api.Constants.ACTION_DEVICE_READY;
import static com.furazin.android.api.Constants.ACTION_EVENT_AVAILABLE;
import static com.furazin.android.api.Constants.ACTION_STATE_CHANGED;
import static com.furazin.android.api.Constants.EXTRA_COMMAND_REPLY;
import static com.furazin.android.api.Constants.EXTRA_DATA;
import static com.furazin.android.api.Constants.EXTRA_STATE_CHANGED;
import static com.furazin.android.api.Constants.IDENTIFIER;
import static com.furazin.android.api.Constants.States;

/**
 * Created by manza on 15/05/2017.
 */

public class DatosGSR extends Activity implements OnBITalinoDataAvailable {

    // Variables de Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("users");

    private Handler handler;

    public final static String EXTRA_DEVICE = "com.furazin.android.mbandgsr.DatosGSR.EXTRA_DEVICE";
    public final static String FRAME = "com.furazin.android.mbandgsr.DeviceActivity.Frame";
    private BluetoothDevice bluetoothDevice;
    private BITalinoCommunication bitalino;
    private boolean isBITalino2 = false;
    private boolean isUpdateReceiverRegistered = false;

    Intent VideoData;

    private static final String UPLOAD_FAIL = "UPLOAD FAIL";
//    private String NOMBRE_EXPERIENCIA = Formulario.NOMBRE_EXPERIENCIA;
    private String EMAIL_USUARIO;
    private String NOMBRE_USUARIO;

    private static final String UPLOAD_COMPLETE = "UPLOAD SUCCES";
    private StorageReference mStorageRef;

    static final int REQUEST_VIDEO_CAPTURE = 1;
    final static int RQS_RECORDING = 1;

    private BandClient client = null;
    private Button btnStart, btnStop, btnBluetooth, btnConectarBitalino, btnVolverMenu;
    private TextView txtGSR, txtTemperatura, txtFC;
    private TextView nameTextView;
    private TextView addressTextView;
    private TextView resultsTextView;
    private TextView txtSubidaVideo;
    private TextView txtlblSubidaVideo;
    private TextView txtVideoSubidoExito;
    private final String TAG = this.getClass().getSimpleName();
    VideoView video_record;
    private String videoPath = "";
    private String audioPath = "";

    int contador_gsr, contador_temp, contador_fc;

    // Variable para recordar las credenciales del usuario
    private SharedPreferences sharedPref;


    GraphView graphGSR; // Elemento de la gráfica
    GraphView graphTemperatura; // Elemento de la gráfica
    GraphView graphFC; // Elemento de la gráfica

    ArrayList<DataPoint> gsrValues; // Array con los distintos valores de la GSR
    ArrayList<DataPoint> temperaturaValues; // Array con los distintos valores de la GSR
    ArrayList<DataPoint> fcValues; // Array con los distintos valores de la GSR

    // Cronómetro
    Chronometer crono;

    AnimationDrawable microAnimation;
    ImageView microImage;

    Timer timer = new Timer();

    private ProgressBar progressBar;

    private String tipoPrueba;
    private Boolean finPrueba;

    File audiofile = null;
    MediaRecorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);

        nameTextView = findViewById(R.id.device_name_text_view);
        addressTextView = findViewById(R.id.mac_address_text_view);
        txtSubidaVideo = findViewById(R.id.txtSubidaVideo);
        txtlblSubidaVideo = findViewById(R.id.textoLblSubirVideo);
        txtVideoSubidoExito = findViewById(R.id.txtVideoSubidoExito);
        microImage = findViewById(R.id.image_mic);
        microImage.setBackgroundResource(R.drawable.mic_animation);
        microAnimation = (AnimationDrawable) microImage.getBackground();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        finPrueba = false;

        // Selección multimedia
        getTipoPrueba();

        // Comprobamos que está emparejado un dispositvo bluetooth Bitalino y obtenemos sus datos
        if(getIntent().hasExtra(EXTRA_DEVICE)){
            this.NOMBRE_USUARIO = InfoSujeto.id_usuario;
            // Obtenemos email del usuario que se ha logueado
            this.EMAIL_USUARIO = MainActivity.EMAIL_USUARIO;

            bluetoothDevice = getIntent().getParcelableExtra(EXTRA_DEVICE);
            iniciarUIBluetooth();
            setUIBluetooth();
        }

        handler = new Handler(getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                BITalinoFrame frame = bundle.getParcelable(FRAME);

                Log.d(TAG, frame.toString());

                if(frame != null){ //BITalino
                    resultsTextView.setText(frame.toString());
                }
            }
        };

        // Variable de Firebase para gestionar el almacenamiento de archivos
        mStorageRef = FirebaseStorage.getInstance().getReference();

        txtGSR = (TextView) findViewById(R.id.txtGSR);
//        txtTemperatura = (TextView) findViewById(R.id.txtTemperatura);
        txtFC = (TextView) findViewById(R.id.txtFC);


        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setEnabled(false);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnBluetooth = (Button) findViewById(R.id.btnEmparejarBluetooth);
        btnConectarBitalino = (Button) findViewById(R.id.btnConectarBitalino);
        btnVolverMenu = (Button) findViewById(R.id.btnVolverMenu);

        resultsTextView = (TextView) findViewById(R.id.results_text_view);

        // Cronómetro
//        crono = (Chronometer) findViewById(R.id.chronometer3);

//        // Gráfica
        graphGSR = (GraphView) findViewById(R.id.graph_GSR);
//        graphTemperatura = (GraphView) findViewById(R.id.graph_Temperatura);
        graphFC = (GraphView) findViewById(R.id.graph_FC);
        gsrValues = new ArrayList<>();
        temperaturaValues = new ArrayList<>();
        fcValues = new ArrayList<>();
        contador_gsr = 0;
        contador_temp = 0;
        contador_fc = 0;

        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ScanActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnConectarBitalino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //txtGSR.setText("");
                //txtTemperatura.setText("");
                //txtFC.setText("");

                 //Permitir monitorizar la Frecuencia Cardiaca
//                new HeartRateConsentTask().execute(reference);
//
//                crono.setVisibility(View.VISIBLE);
//                crono.setBase(SystemClock.elapsedRealtime());
//                crono.start();
//
//                new SubscriptionTask().execute();
//                 Inicializamos la generación de aleatorios para las gráficas
                timer.schedule(new RandomValues(), 0, 2000);
//
                if (addressTextView.getText().toString().equals("00:00:00:00:00:00")) {
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Antes de conectar debe de estar emparejado por Bluetooth con Bitalino", Toast.LENGTH_SHORT);
                    toast1.show();
                }
                else {
                    try {
                        bitalino.connect(bluetoothDevice.getAddress());
                    } catch (BITalinoException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addressTextView.getText().toString().equals("00:00:00:00:00:00") || resultsTextView.getText().toString().equals("...")) {
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Conectar previamente con Bitalino", Toast.LENGTH_SHORT);
                    toast1.show();
                }
                else {
                    btnStart.setEnabled(false);
                    btnStop.setVisibility(View.VISIBLE);
                    limpiarGraphicGSR();
                    try {
                        bitalino.start(new int[]{0,1,2,3,4,5}, 1);
                    } catch (BITalinoException e) {
                        e.printStackTrace();
                    }
                    switch(tipoPrueba) {
                        case "Sólo Vídeo":
                        case "Audio y Vídeo":
                            GrabarVideo();
                            break;
                        case "Ninguno":
                            break;
                        case "Sólo Audio":
                            try {
//                                crono.setVisibility(View.VISIBLE);
//                                crono.setBase(SystemClock.elapsedRealtime());
//                                crono.start();
                                microAnimation.start();
                                GrabarAudio();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    bitalino.stop();
                } catch (BITalinoException e) {
                    e.printStackTrace();
                }
                timer.cancel();
                //crono.stop();
                btnStop.setEnabled(false);
                graphicGSR();
//                graphicTemperatura();
                graphicFC();
                switch(tipoPrueba) {
                    case "Audio y Vídeo":
                        txtlblSubidaVideo.setText("Subiendo audio y vídeo... ");
                        SubirArchivoFirebase(videoPath);
                        break;
                    case "Sólo Vídeo":
                        txtlblSubidaVideo.setText("Subiendo vídeo... ");
                        SubirArchivoFirebase(videoPath);
                        break;
                    case "Sólo Audio":
//                        crono.stop();
                        microAnimation.stop();
                        recorder.stop();
                        recorder.release();
                        addRecordingToMediaLibrary();
                        txtlblSubidaVideo.setText("Subiendo audio... ");
                        SubirArchivoFirebase(audioPath);
                        break;
                    case "Ninguno":
                        break;
                }
                WriteDatosGraficaFirebase(gsrValues, temperaturaValues, fcValues);
            }
        });

        btnVolverMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(DatosGSR.this,MainActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(updateReceiver, makeUpdateIntentFilter());
        isUpdateReceiverRegistered = true;

        txtGSR.setText("");
//        txtTemperatura.setText("");
        txtFC.setText("");
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * BITALINO
     */

    @Override
    public void onBITalinoDataAvailable(BITalinoFrame bitalinoFrame) {
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putParcelable(FRAME, bitalinoFrame);
        message.setData(bundle);
        handler.sendMessage(message);

        double gsr = getConvertedGSR(bitalinoFrame.getAnalog(2));
        if (String.valueOf(gsr) != "Infinity" && gsr > 10000) {
            System.out.println("GSR ANALOG ---> " + gsr);
            nuevoDatoGSR(gsr);
        }
    }

    private void iniciarUIBluetooth() {
        nameTextView = findViewById(R.id.device_name_text_view);
        addressTextView = findViewById(R.id.mac_address_text_view);
    }

    private void setUIBluetooth() {
        if (bluetoothDevice.getName() == null) {
            nameTextView.setText("BITalino");
        } else {
            nameTextView.setText(bluetoothDevice.getName());
            addressTextView.setText(bluetoothDevice.getAddress());
        }

        Communication communication = Communication.getById(bluetoothDevice.getType());
        Log.d(TAG, "Communication: " + communication.name());
        if(communication.equals(Communication.DUAL)){
            communication = Communication.BLE;
        }

        bitalino = new BITalinoCommunicationFactory().getCommunication(communication,this, this);
    }

    private final BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(ACTION_STATE_CHANGED.equals(action)){
                String identifier = intent.getStringExtra(IDENTIFIER);
                States state = States.getStates(intent.getIntExtra(EXTRA_STATE_CHANGED, 0));

                //stateTextView.setText(state.name());

                switch (state){
                    case NO_CONNECTION:
                        break;
                    case LISTEN:
                        break;
                    case CONNECTING:
                        break;
                    case CONNECTED:
                        if (btnStop.getVisibility() != View.VISIBLE) {
                            btnStart.setEnabled(true);
                        }
                        btnBluetooth.setEnabled(false);
                        btnConectarBitalino.setEnabled(false);
                        break;
                    case ACQUISITION_TRYING:
                        break;
                    case ACQUISITION_OK:
                        break;
                    case ACQUISITION_STOPPING:
                        break;
                    case DISCONNECTED:
                        break;
                    case ENDED:
                        break;

                }
            }
            else if(ACTION_DATA_AVAILABLE.equals(action)){
                if(intent.hasExtra(EXTRA_DATA)){
                    Parcelable parcelable = intent.getParcelableExtra(EXTRA_DATA);
                    if(parcelable.getClass().equals(BITalinoFrame.class)){ //BITalino
                        BITalinoFrame frame = (BITalinoFrame) parcelable;
                        resultsTextView.setText(frame.toString());
                    }
                }
            }
            else if(ACTION_COMMAND_REPLY.equals(action)){
                String identifier = intent.getStringExtra(IDENTIFIER);

                if(intent.hasExtra(EXTRA_COMMAND_REPLY) && (intent.getParcelableExtra(EXTRA_COMMAND_REPLY) != null)){
                    Parcelable parcelable = intent.getParcelableExtra(EXTRA_COMMAND_REPLY);
                    if(parcelable.getClass().equals(BITalinoState.class)){ //BITalino
                        Log.d(TAG, ((BITalinoState)parcelable).toString());
                        resultsTextView.setText(parcelable.toString());
                    }
                    else if(parcelable.getClass().equals(BITalinoDescription.class)){ //BITalino
                        isBITalino2 = ((BITalinoDescription)parcelable).isBITalino2();
                        resultsTextView.setText("isBITalino2: " + isBITalino2 + "; FwVersion: " + String.valueOf(((BITalinoDescription)parcelable).getFwVersion()));
                    }
                }
            }
        }
    };

    private IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_STATE_CHANGED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        intentFilter.addAction(ACTION_EVENT_AVAILABLE);
        intentFilter.addAction(ACTION_DEVICE_READY);
        intentFilter.addAction(ACTION_COMMAND_REPLY);
        return intentFilter;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void onPause() {
        super.onPause();
//        if (client != null) {
//            try {
//                client.getSensorManager().unregisterGsrEventListener(mGsrEventListener);
//                client.getSensorManager().unregisterBarometerEventListener(mBarometerEventListener);
//                client.getSensorManager().unregisterHeartRateEventListener(mHeartRateEventListener);
//            } catch (BandIOException e) {
//                appendGSRToUI(e.getMessage());
//            }
//        }
    }

    @Override
    protected void onDestroy() {
//        if (client != null) {
//            try {
//                client.disconnect().await();
//            } catch (InterruptedException e) {
//                // Do nothing as this is happening during destroy
//            } catch (BandException e) {
//                // Do nothing as this is happening during destroys
//            }
//        }
        super.onDestroy();
        if(isUpdateReceiverRegistered) {
            unregisterReceiver(updateReceiver);
            isUpdateReceiverRegistered = false;
        }

        if(bitalino != null){
            bitalino.closeReceivers();
            try {
                bitalino.disconnect();
            } catch (BITalinoException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (NOMBRE_USUARIO != null) {
            if (!tipoPrueba.equals("Ninguno") ) {
                if (finPrueba) {
                    finish();
                    Intent i = new Intent(DatosGSR.this,MainActivity.class);
                    startActivity(i);
                }
            }
            else {
                if (finPrueba) {
                    finish();
                    Intent i = new Intent(DatosGSR.this, MainActivity.class);
                    startActivity(i);
                }
            }
        }
        else {
            finish();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Con esto obtenemos el path del vídeo que acabamos de grabar de la experiencia realizada

        switch (tipoPrueba) {
            case "Sólo Vídeo":
            case "Audio y Vídeo":
                if(resultCode==RESULT_OK)
                {
                    Uri vid = data.getData();
                    videoPath = getRealPathFromURI(vid);
                }
                break;
            case "Sólo Audio":
//                    Uri audio = data.getData();
//                    audioPath = getRealPathFromURI(audio);
                break;
        }

    }

    /*
    * Método para obtener el tipo de prueba que se quiere hacer
     */
    public void getTipoPrueba() {
        this.tipoPrueba = InfoSujeto.tipoPrueba;
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Usuario user = snapshot.getValue(Usuario.class);
//                    if (user.getEmail().equals(EMAIL_USUARIO)) {
//                        tipoPrueba = snapshot.child("Experiencias").child(UsuariosExperiencia.NOMBRE_EXPERIENCIA).child(NOMBRE_USUARIO).child("opcion_multimedia").getValue().toString();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    /*
    /  Método para iniciar la grabación de vídeo por el usuario
     */
    public void GrabarVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    /*
  /  Método para iniciar la grabación de audio por el usuario
   */
    public void GrabarAudio() throws IOException {
//        Intent intent =
//                new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
//        startActivityForResult(intent, RQS_RECORDING);
        File sampleDir = Environment.getExternalStorageDirectory();
        try {
            audiofile = File.createTempFile("sound", ".mp3", sampleDir);
        } catch (IOException e) {
            Log.e(TAG, "sdcard access error");
            return;
        }
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        recorder.prepare();
        recorder.start();
    }

    /*
    / Método para subir un archivo a Firebase
     */
    public void SubirArchivoFirebase(String path) {

        progressBar.setVisibility(View.VISIBLE);
        txtlblSubidaVideo.setVisibility(View.VISIBLE);
        Uri file = Uri.fromFile(new File(path));
        StorageReference archivoRef = null;

        if (tipoPrueba.equals("Sólo Vídeo") || tipoPrueba.equals("Audio y Vídeo")) {
            archivoRef= mStorageRef.child(EMAIL_USUARIO + "/Vídeos/" + UsuariosExperiencia.NOMBRE_EXPERIENCIA + "/" + this.NOMBRE_USUARIO + "/video.3gp");
        }
        else {
            if (tipoPrueba.equals("Sólo Audio")) {
                archivoRef= mStorageRef.child(EMAIL_USUARIO + "/Audios/" + UsuariosExperiencia.NOMBRE_EXPERIENCIA + "/" + this.NOMBRE_USUARIO + "/audio.mp3");
            }
        }

        archivoRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
//                        Uri downloadUrl = taskSnapshot.getDownloadUrl()
                        Log.d(UPLOAD_COMPLETE,"Archivo subido con éxito");
                        txtVideoSubidoExito.setVisibility(View.VISIBLE);
                        txtVideoSubidoExito.setText("¡Archivo subido con éxito!");
                        finPrueba = true;
                        btnVolverMenu.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Log.d(UPLOAD_FAIL,"Fallo al subir");
                        txtVideoSubidoExito.setVisibility(View.VISIBLE);
                        txtVideoSubidoExito.setText("Error al realizar la subida del archivo");
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, String.format("onProgress: %5.2f MB transferred",
                        taskSnapshot.getBytesTransferred()/1024.0/1024.0));

                double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                progressBar.setProgress((int)progress);
                txtSubidaVideo.setText(String.format("%5.2f MB",
                        taskSnapshot.getBytesTransferred()/1024.0/1024.0));
            }
        });
    }

    /*
    / Método para obtener el path de un vídeo
     */
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /*
    * Función que transforma los valores de la GSR obtenidos directamente de Bitalino en microSiemens
    */
    private double getConvertedGSR(int gsr) {
        double VCC = 3.3;
        double gsrConverted_micro = ((gsr/Math.pow(2,10))*VCC)/0.132;
        gsrConverted_micro = Math.pow(gsrConverted_micro*Math.pow(10,-6),-1);

        return gsrConverted_micro;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    / Método para mostrar los datos de la GSR en tiempo real
     */
//    private class SubscriptionTask extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                if (getConnectedBandClient()) {
//                    int hardwareVersion = Integer.parseInt(client.getHardwareVersion().await());
//                    if (hardwareVersion >= 20) {
//                        appendGSRToUI("Microsoft Band conectada.\n");
//                        appendTemperaturaToUI("Microsoft Band conectada.\n");
//                        appendFCToUI("Microsoft Band conectada.\n");
//                            client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
//                            client.getSensorManager().registerGsrEventListener(mGsrEventListener);
//                            client.getSensorManager().registerBarometerEventListener(mBarometerEventListener);
//
//                    } else {
//                        appendGSRToUI("The Gsr sensor is not supported with your Band version. Microsoft Band 2 is required.\n");
//                    }
//                } else {
//                    appendGSRToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
//                }
//            } catch (BandException e) {
//                String exceptionMessage="";
//                switch (e.getErrorType()) {
//                    case UNSUPPORTED_SDK_VERSION_ERROR:
//                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
//                        break;
//                    case SERVICE_ERROR:
//                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
//                        break;
//                    default:
//                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
//                        break;
//                }
//                appendGSRToUI(exceptionMessage);
//
//            } catch (Exception e) {
//                appendGSRToUI(e.getMessage());
//            }

//            return null;
//        }
//    }

    private class RandomValues extends TimerTask {
        public void run() {
            Random rnd = new Random();
            int valor_random = (int)(rnd.nextDouble() * 350000 + 0);
//            appendGSRToUI(String.format("GSR = %d kOhms\n", valor_random));
//            nuevoDatoGSR(valor_random);

            Random rnd2 = new Random();
            valor_random = (int)(rnd2.nextDouble() * 36 + 35);
            //appendTemperaturaToUI(String.format("Temperatura = %d degrees Celsius", valor_random));
            nuevoDatoTemperatura(valor_random);

            Random rnd3 = new Random();
            valor_random = (int)(rnd3.nextDouble() * 110 + 60);
            //appendFCToUI(String.format("Frecuencia cardiaca = %d beats per minute\n", valor_random));
            nuevoDatoFC(valor_random);
        }
    }


    /*
    / Datos de la interfaz con los valores de la pulsera
     */
//    private void appendGSRToUI(final String string) {
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                txtGSR.setText(string);
//            }
//        });
//    }
//
//    private void appendTemperaturaToUI(final String string) {
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                txtTemperatura.setText(string);
//            }
//        });
//    }
//
//    private void appendFCToUI(final String string) {
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                txtFC.setText(string);
//            }
//        });
//    }

//    private boolean getConnectedBandClient() throws InterruptedException, BandException {
//        if (client == null) {
//            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
//            if (devices.length == 0) {
//                appendGSRToUI("Band isn't paired with your phone.\n");
//                appendTemperaturaToUI("Band isn't paired with your phone.\n");
//                appendFCToUI("Band isn't 'paired with your phone.\n");
//                return false;
//            }
//            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
//        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
//            return true;
//        }
//
//        appendGSRToUI("Conectando con Microsoft Band...\n");
//        appendTemperaturaToUI("Conectando con Microsoft Band...\n");
//        appendFCToUI("Conectando con Microsoft Band...\n");
//        return ConnectionState.CONNECTED == client.connect().await();
//    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    / Gráfica con los valores de los sensores
     */

    public void nuevoDatoGSR(double res) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gsrValues.add(new DataPoint(contador_gsr,res));
        contador_gsr ++;
    }

    public void nuevoDatoTemperatura(double res) {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        temperaturaValues.add(new DataPoint(contador_temp,res));
        contador_temp ++;
    }

    public void nuevoDatoFC(int res) {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fcValues.add(new DataPoint(contador_fc,res));
        contador_fc ++;
    }

    public void limpiarGraphicGSR() {
        gsrValues.clear();
        graphGSR.removeAllSeries();
        contador_gsr = 0;
    }

    public void graphicGSR() {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        DataPoint[] points = new DataPoint[100000];

        for (int i=0; i<gsrValues.size(); i++) {
            double x = gsrValues.get(i).getX();
            double y = gsrValues.get(i).getY();
            points[i] = new DataPoint(x,y);
            series.appendData(new DataPoint(x,y),true,50000);
        }

        graphGSR.addSeries(series);
    }

    public void graphicTemperatura() {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        series.setColor(Color.MAGENTA);
        DataPoint[] points = new DataPoint[100000];

        for (int i=0; i<temperaturaValues.size(); i++) {
            int x = (int)temperaturaValues.get(i).getX();
            int y = (int)temperaturaValues.get(i).getY();
            points[i] = new DataPoint(x,y);
            series.appendData(new DataPoint(x,y),true,50000);
        }

        graphTemperatura.addSeries(series);
    }

    public void graphicFC() {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        series.setColor(Color.RED);
        DataPoint[] points = new DataPoint[100000];

        for (int i=0; i<fcValues.size(); i++) {
            int x = (int)fcValues.get(i).getX();
            int y = (int)fcValues.get(i).getY();
            points[i] = new DataPoint(x,y);
            series.appendData(new DataPoint(x,y),true,50000);
        }

        graphFC.addSeries(series);
    }

    public void WriteDatosGraficaFirebase(ArrayList<DataPoint> datos_gsr, ArrayList<DataPoint> datos_temperatura, ArrayList<DataPoint> datos_fc) {

        final ArrayList<String> valores_gsr = new ArrayList<>();

        for (int i=0; i<datos_gsr.size(); i++) {
            //int x = (int)datos_gsr.get(i).getX();
            int y = (int)datos_gsr.get(i).getY();

            valores_gsr.add(String.valueOf(y));
        }

        //if (datos_temperatura.size() != 0) {
//        final ArrayList<String> valores_temperatura = new ArrayList<>();
//
//            for (int i = 0; i < datos_temperatura.size(); i++) {
//                //int x = (int) datos_temperatura.get(i).getX();
//                int y = (int) datos_temperatura.get(i).getY();
//
//                valores_temperatura.add((String.valueOf(y)));
//            }
        //}

        //if (datos_fc.size()!=0 ) {
        final ArrayList<String> valores_fc = new ArrayList<>();

            for (int i = 0; i < datos_fc.size(); i++) {
                //int x = (int) datos_fc.get(i).getX();
                int y = (int) datos_fc.get(i).getY();

                valores_fc.add((String.valueOf(y)));
            }
        //}

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Usuario user = snapshot.getValue(Usuario.class);
                    if (user.getEmail().equals(EMAIL_USUARIO)) {
                        // Obtenemos la key del usuario logueado
                        final String key = snapshot.getKey();

                        myRef.child(key).child("Experiencias").child(UsuariosExperiencia.NOMBRE_EXPERIENCIA).child(NOMBRE_USUARIO).child("Datos Graficas").child("GSR").setValue(valores_gsr);
//                        myRef.child(key).child("Experiencias").child(UsuariosExperiencia.NOMBRE_EXPERIENCIA).child(NOMBRE_USUARIO).child("Datos Graficas").child("Temperatura").setValue(valores_temperatura);
                        myRef.child(key).child("Experiencias").child(UsuariosExperiencia.NOMBRE_EXPERIENCIA).child(NOMBRE_USUARIO).child("Datos Graficas").child("FC").setValue(valores_fc);

                        // Marcamos experiencia como terminada
                        myRef.child(key).child("Experiencias").child(UsuariosExperiencia.NOMBRE_EXPERIENCIA).child(NOMBRE_USUARIO).child("terminada").setValue("si");

                        // Recorremos cada prueba de la experiencias y comprobamos si tiene todas las pruebas terminadas
                        boolean terminadaAux = true;
                        for (DataSnapshot dataSnapShotExperiencias : snapshot.child("Experiencias").child(UsuariosExperiencia.NOMBRE_EXPERIENCIA).getChildren()) {
                            if (dataSnapShotExperiencias.hasChildren()) {
                                if (dataSnapShotExperiencias.child("terminada").getValue().toString().equals("no") && !dataSnapShotExperiencias.getKey().equals(NOMBRE_USUARIO)){
                                    terminadaAux = false;
                                }
                            }
                        }
                        // Si todas las pruebas están terminadas, seteamos la variable en Firebase
                        if (terminadaAux) {
                            myRef.child(key).child("Experiencias").child(UsuariosExperiencia.NOMBRE_EXPERIENCIA).child("pruebaTerminada").setValue("si");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

            if (tipoPrueba.equals("Ninguno")) {
                btnVolverMenu.setVisibility(View.VISIBLE);
                finPrueba = true;
            }
    }

    protected void addRecordingToMediaLibrary() {
        ContentValues values = new ContentValues(4);
        long current = System.currentTimeMillis();
        values.put(MediaStore.Audio.Media.TITLE, "audio" + audiofile.getName());
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
        values.put(MediaStore.Audio.Media.DATA, audiofile.getAbsolutePath());
        ContentResolver contentResolver = getContentResolver();

        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
//        Toast.makeText(this, "Added File " + newUri, Toast.LENGTH_LONG).show();

        audioPath = getRealPathFromURI(newUri);
    }

}
