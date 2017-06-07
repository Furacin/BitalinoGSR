package com.furazin.android.mbandgsr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandBarometerEvent;
import com.microsoft.band.sensors.BandBarometerEventListener;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by manza on 15/05/2017.
 */

public class DatosGSR extends AppCompatActivity {

    private static final String UPLOAD_COMPLETE = "UPLOAD";
    private StorageReference mStorageRef;

    private int ACTIVITY_START_CAMERA_APP =0;
    static final int REQUEST_VIDEO_CAPTURE = 1;

    private BandClient client = null;
    private Button btnStart;
    private TextView txtGSR, txtTemperatura, txtFC;
    VideoView video_record;
    private String videoPath = "";

    int contador_gsr, contador_temp, contador_fc;

    // Variable para recordar las credenciales del usuario
    private SharedPreferences sharedPref;


    GraphView graphGSR; // Elemento de la gráfica
    GraphView graphTemperatura; // Elemento de la gráfica
    GraphView graphFC; // Elemento de la gráfica

    ArrayList<DataPoint> gsrValues; // Array con los distintos valores de la GSR
    ArrayList<DataPoint> temperaturaValues; // Array con los distintos valores de la GSR
    ArrayList<DataPoint> fcValues; // Array con los distintos valores de la GSR

    private BandGsrEventListener mGsrEventListener = new BandGsrEventListener() {
        @Override
        public void onBandGsrChanged(final BandGsrEvent event) {
            if (event != null) {
                appendGSRToUI(String.format("GSR = %d kOhms\n", event.getResistance()));
                graphicGSR(event.getResistance());
            }
        }
    };

    private BandBarometerEventListener mBarometerEventListener = new BandBarometerEventListener() {
        @Override
        public void onBandBarometerChanged(final BandBarometerEvent event) {
            if (event != null) {
                appendTemperaturaToUI(String.format("Temperatura = %.2f degrees Celsius", event.getTemperature()));
                graphicTemperatura(event.getTemperature());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            if (event != null) {
                appendFCToUI(String.format("Frecuencia cardiaca = %d beats per minute\n", event.getHeartRate()));
                graphicFC(event.getHeartRate());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);

        // Instanciamos una referencia al Contexto
        Context context = this.getApplicationContext();
        //Instanciamos el objeto SharedPrefere  nces y creamos un fichero Privado bajo el
        //nombre definido con la clave preference_file_key en el fichero string.xml
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Variable de Firebase para gestionar el almacenamiento de archivos
        mStorageRef = FirebaseStorage.getInstance().getReference();

        txtGSR = (TextView) findViewById(R.id.txtGSR);
        txtTemperatura = (TextView) findViewById(R.id.txtTemperatura);
        txtFC = (TextView) findViewById(R.id.txtFC);

        final WeakReference<Activity> reference = new WeakReference<Activity>(this);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtGSR.setText("");
                txtTemperatura.setText("");
                txtFC.setText("");

                // Permitir monitorizar la Frecuencia Cardiaca
                new HeartRateConsentTask().execute(reference);

                new SubscriptionTask().execute();
                GrabarVideo();
            }
        });

//        // Gráfica
        graphGSR = (GraphView) findViewById(R.id.graph_GSR);
        graphTemperatura = (GraphView) findViewById(R.id.graph_Temperatura);
        graphFC = (GraphView) findViewById(R.id.graph_FC);
        gsrValues = new ArrayList<>();
        temperaturaValues = new ArrayList<>();
        fcValues = new ArrayList<>();
        contador_gsr = 0;
        contador_temp = 0;
        contador_fc = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtGSR.setText("");
        txtTemperatura.setText("");
        txtFC.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (client != null) {
            try {
                client.getSensorManager().unregisterGsrEventListener(mGsrEventListener);
                client.getSensorManager().unregisterBarometerEventListener(mBarometerEventListener);
                client.getSensorManager().unregisterHeartRateEventListener(mHeartRateEventListener);
            } catch (BandIOException e) {
                appendGSRToUI(e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (client != null) {
            try {
                client.disconnect().await();
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            }
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Con esto obtenemos el path del vídeo que acabamos de grabar de la experiencia realizada
        if(resultCode==RESULT_OK)
        {
            Uri vid = data.getData();
            videoPath = getRealPathFromURI(vid);
            SubirArchivoFirebase(videoPath);
        }

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
    / Método para subir un archivo a Firebase
     */
    public void SubirArchivoFirebase(String path) {
        Uri file = Uri.fromFile(new File(path));
        // Obtenemos email del usuario que se ha logueado
        final String email = sharedPref.getString((getString(R.string.email_key)), "");
        StorageReference archivoRef = mStorageRef.child(email + "/Vídeos/" +getFechaYHora());


        archivoRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
//                        Uri downloadUrl = taskSnapshot.getDownloadUrl()
                        Log.d(UPLOAD_COMPLETE,"Archivo subido con éxito");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
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

    String getFechaYHora() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyyMMdd");
        String currentDate = mdformat.format(calendar.getTime());

        SimpleDateFormat format = new SimpleDateFormat("HHmm", Locale.US);
        String hour = format.format(new Date());

        currentDate+=hour;

        return currentDate;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    / Método para mostrar los datos de la GSR en tiempo real
     */
    private class   SubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    int hardwareVersion = Integer.parseInt(client.getHardwareVersion().await());
                    if (hardwareVersion >= 20) {
                        appendGSRToUI("Microsoft Band conectada.\n");
                        appendTemperaturaToUI("Microsoft Band conectada.\n");
                        appendFCToUI("Microsoft Band conectada.\n");
                            client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
                            client.getSensorManager().registerGsrEventListener(mGsrEventListener);
                            client.getSensorManager().registerBarometerEventListener(mBarometerEventListener);

                    } else {
                        appendGSRToUI("The Gsr sensor is not supported with your Band version. Microsoft Band 2 is required.\n");
                    }
                } else {
                    appendGSRToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                appendGSRToUI(exceptionMessage);

            } catch (Exception e) {
                appendGSRToUI(e.getMessage());
            }
            return null;
        }
    }

    private class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void> {
        @Override
        protected Void doInBackground(WeakReference<Activity>... params) {
            try {
                if (getConnectedBandClient()) {

                    if (params[0].get() != null) {
                        client.getSensorManager().requestHeartRateConsent(params[0].get(), new HeartRateConsentListener() {
                            @Override
                            public void userAccepted(boolean consentGiven) {
                            }
                        });
                    }
                } else {
                }
            } catch (BandException e) {

            } catch (Exception e) {
            }
            return null;
        }
    }

    /*
    / Datos de la interfaz con los valores de la pulsera
     */
    private void appendGSRToUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtGSR.setText(string);
            }
        });
    }

    private void appendTemperaturaToUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtTemperatura.setText(string);
            }
        });
    }

    private void appendFCToUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtFC.setText(string);
            }
        });
    }

    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                appendGSRToUI("Band isn't paired with your phone.\n");
                appendTemperaturaToUI("Band isn't paired with your phone.\n");
                appendFCToUI("Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        appendGSRToUI("Conectando con Microsoft Band...\n");
        appendTemperaturaToUI("Conectando con Microsoft Band...\n");
        appendFCToUI("Conectando con Microsoft Band...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    / Gráfica con los valores de la GSR
     */
    public void graphicGSR(int res) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        gsrValues.add(new DataPoint(contador_gsr,res));
        DataPoint[] points = new DataPoint[100000];


        for (int i=0; i<gsrValues.size(); i++) {
            int x = (int)gsrValues.get(i).getX();
            int y = (int)gsrValues.get(i).getY();
            //points[i] = new DataPoint(x,y);
            series.appendData(new DataPoint(x,y),true,50000);
        }

        contador_gsr ++;

        graphGSR.addSeries(series);
    }

    public void graphicTemperatura(double res) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        series.setColor(Color.MAGENTA);
        temperaturaValues.add(new DataPoint(contador_temp,res));
        DataPoint[] points = new DataPoint[100000];


        for (int i=0; i<temperaturaValues.size(); i++) {
            int x = (int)temperaturaValues.get(i).getX();
            int y = (int)temperaturaValues.get(i).getY();
            //points[i] = new DataPoint(x,y);
            series.appendData(new DataPoint(x,y),true,50000);
        }

        contador_temp ++;

        graphTemperatura.addSeries(series);
    }

    public void graphicFC(int res) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        series.setColor(Color.RED);
        fcValues.add(new DataPoint(contador_fc,res));
        DataPoint[] points = new DataPoint[100000];


        for (int i=0; i<fcValues.size(); i++) {
            int x = (int)fcValues.get(i).getX();
            int y = (int)fcValues.get(i).getY();
            //points[i] = new DataPoint(x,y);
            series.appendData(new DataPoint(x,y),true,50000);
        }

        contador_fc ++;

        graphFC.addSeries(series);
    }
}
