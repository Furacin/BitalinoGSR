package com.furazin.android.mbandgsr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BandClient client = null;
    private Button btnStart;
    private TextView txtStatus;

    int contador;

    GraphView graph; // Elemento de la gráfica
    ArrayList<DataPoint> gsrValues; // Array con los distintos valores de la GSR

    // DrawerLayout
    private DrawerLayout mDrawerLayout;

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
        setContentView(R.layout.activity_main);

        // ------------------------------------------------------------------------------------
        // INTERFAZ
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // ------------------------------------------------------------------------------------

        Button NuevoForm = (Button) findViewById(R.id.FormularioButton);
        NuevoForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Formulario.class);
                startActivity(i);
            }
        });

        txtStatus = (TextView) findViewById(R.id.txtStatus);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtStatus.setText("");
                new GsrSubscriptionTask().execute();
            }
        });
//        // Gráfica
        graph = (GraphView) findViewById(R.id.graph);
        gsrValues = new ArrayList<>();
        contador = 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
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
