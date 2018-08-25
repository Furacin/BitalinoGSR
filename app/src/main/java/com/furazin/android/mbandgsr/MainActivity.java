package com.furazin.android.mbandgsr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.furazin.android.mbandgsr.FirebaseBD.Usuario;
import com.furazin.android.mbandgsr.RecyclerExperiencias.RecyclerViewAdapterListaExperiencias;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

//    private LinearLayout layout;
    // Variable para recordar las credenciales del usuario
    private SharedPreferences sharedPref;
    public static String EMAIL_USUARIO;

    public static String user_key;

    // DrawerLayout
    private DrawerLayout mDrawerLayout;

//    private List<String> experiencias;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapterListaExperiencias recyclerViewAdapter;

    private ArrayList<ArrayList<String>> experiencias;

    public static Activity activity;

    private BottomSheetDialog mBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instancimos el activity
        activity = this;

        // Instanciamos una referencia al Contexto
        Context context = this.getApplicationContext();
        //Instanciamos el objeto SharedPreferences y creamos un fichero Privado bajo el
        //nombre definido con la clave preference_file_key en el fichero string.xml
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        // Obtenemos email del usuario que se ha logueado
        EMAIL_USUARIO = sharedPref.getString((getString(R.string.email_key)), "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // BottomSheet
        View bottomSheet = findViewById(R.id.framelayout_bottom_sheet);

        final FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),NuevaExperiencia.class);
                startActivity(i);
                finish();
            }
        });

        experiencias = new ArrayList<ArrayList<String>>();
        recyclerView = (RecyclerView)findViewById(R.id.experiencias_list);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy<0 && !fab.isShown())
                    fab.show();
                else if(dy>0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        getListaUsuarios();

        final View bottomSheetLayout = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
        TextView t1 = (TextView) bottomSheetLayout.findViewById(R.id.tv_detail);
        TextView t2 = (TextView) bottomSheetLayout.findViewById(R.id.tv_detail_2);
        t1.setText(R.string.t1_detail);
        t2.setText(R.string.t2_detail);
        (bottomSheetLayout.findViewById(R.id.button_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
            }
        });
        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(bottomSheetLayout);
    }

    public void getListaUsuarios() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                experiencias.remove(experiencias);
                experiencias.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Usuario user = snapshot.getValue(Usuario.class);
                    if (user.getEmail().equals(EMAIL_USUARIO)) {
                        // Obtenemos la key del usuario logueado
                        user_key = snapshot.getKey();

                            for(DataSnapshot singleSnapshot : dataSnapshot.child(user_key).child("Experiencias").getChildren()) {
                                if (singleSnapshot.child("pruebaTerminada").exists()) {
                                    String experienciaTitle = singleSnapshot.getKey();
                                    String fechaRealicacion = singleSnapshot.child("fechaRealizacion").getValue().toString();
                                    String terminada = singleSnapshot.child("pruebaTerminada").getValue().toString();
                                    //                                    System.out.println(experienciaTitle);
                                    //                                    experiencias.add(experienciaTitle);
                                    ArrayList<String> datosExperiencia = new ArrayList<String>();
                                    datosExperiencia.add(experienciaTitle);  // posicion 0
                                    datosExperiencia.add(fechaRealicacion);  // posicion 1
                                    datosExperiencia.add(terminada);         // posicion 2
                                    experiencias.add(datosExperiencia);
                                    recyclerViewAdapter = new RecyclerViewAdapterListaExperiencias(MainActivity.this, experiencias);
                                    recyclerView.setAdapter(recyclerViewAdapter);
                                }
                            }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // the inflation code goes here
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
            case R.id.action_info:
                mBottomSheetDialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
