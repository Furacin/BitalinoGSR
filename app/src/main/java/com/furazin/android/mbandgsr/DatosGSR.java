package com.furazin.android.mbandgsr;

import android.content.Intent;
import android.database.Cursor;
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
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;

import java.io.File;
import java.util.ArrayList;

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
    private TextView txtStatus;
    VideoView video_record;
    private String videoPath = "";

    int contador;

    GraphView graph; // Elemento de la gráfica
    ArrayList<DataPoint> gsrValues; // Array con los distintos valores de la GSR

        private BandGsrEventListener mGsrEventListener = new BandGsrEventListener() {
        @Override
        public void onBandGsrChanged(final BandGsrEvent event) {
            if (event != null) {
                appendToUI(String.format("Resistencia = %d kOhms\n", event.getResistance()));
                graphic(event.getResistance());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);

        // Variable de Firebase para gestionar el almacenamiento de archivos
        mStorageRef = FirebaseStorage.getInstance().getReference();

        video_record = (VideoView) findViewById(R.id.videoview);

        txtStatus = (TextView) findViewById(R.id.txtStatus);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtStatus.setText("");

                new GsrSubscriptionTask().execute();
                GrabarVideo();
            }
        });
//        // Gráfica
        graph = (GraphView) findViewById(R.id.graph);
        gsrValues = new ArrayList<>();
        contador = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtStatus.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (client != null) {
            try {
                client.getSensorManager().unregisterGsrEventListener(mGsrEventListener);
            } catch (BandIOException e) {
                appendToUI(e.getMessage());
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

    public void SubirArchivoFirebase(String path) {
        Uri file = Uri.fromFile(new File(path));
        StorageReference archivoRef = mStorageRef.child(path);

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

//    private File getLatestFilefromDir(String dirPath){
//        File dir = new File(dirPath);
//        File[] files = dir.listFiles();
//        if (files == null || files.length == 0) {
//            return null;
//        }
//
//        File lastModifiedFile = files[0];
//        for (int i = 1; i < files.length; i++) {
//            if (lastModifiedFile.getName().contains("VID_")) { // Si es un vídeo
//                if (lastModifiedFile.lastModified() < files[i].lastModified()) {
//                    lastModifiedFile = files[i];
//                }
//
//            }
//        }
//        return lastModifiedFile;


//    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private class GsrSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    int hardwareVersion = Integer.parseInt(client.getHardwareVersion().await());
                    if (hardwareVersion >= 20) {
                        appendToUI("Microsoft Band conectada.\n");
                        client.getSensorManager().registerGsrEventListener(mGsrEventListener);
                    } else {
                        appendToUI("The Gsr sensor is not supported with your Band version. Microsoft Band 2 is required.\n");
                    }
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
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
                appendToUI(exceptionMessage);

            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }
    }

    private void appendToUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtStatus.setText(string);
            }
        });
    }

    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                appendToUI("Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        appendToUI("Conectando con Microsoft Band...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }


    public void graphic(int res) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        gsrValues.add(new DataPoint(contador,res));
        DataPoint[] points = new DataPoint[100000];


        for (int i=0; i<gsrValues.size(); i++) {
            int x = (int)gsrValues.get(i).getX();
            int y = (int)gsrValues.get(i).getY();
            //points[i] = new DataPoint(x,y);
            series.appendData(new DataPoint(x,y),true,50000);
        }

        contador ++;

        graph.addSeries(series);
    }
}
