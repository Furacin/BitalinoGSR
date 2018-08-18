package com.furazin.android.mbandgsr;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.furazin.android.mbandgsr.FirebaseBD.Experiencia;
import com.furazin.android.mbandgsr.FirebaseBD.Usuario;
import com.furazin.android.mbandgsr.RecyclerExperiencias.RecyclerViewAdapterUsuariosExperiencia;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.furazin.android.mbandgsr.MainActivity.EMAIL_USUARIO;

/**
 * Created by manza on 15/06/2017.
 */

public class NuevaExperiencia extends AppCompatActivity {

    public static String NOMBRE_EXPERIENCIA;
    public static String NOMBRE_USUARIO;

    Button btn_adduser;
    TextView titulo;
    View linea;
    Button btn_newuser, btn_nuevaexperiencia;
    EditText edit_nombre_experiencia;
    Button btn_crearExperiencia;

    Dialog dialog;

    // Variable para recordar las credenciales del usuario
    private SharedPreferences sharedPref;

    private ArrayList<String> lista_sujetos;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapterUsuariosExperiencia recyclerViewAdapter;

    final String[] user_key = new String[1];

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("users");

    private ValueEventListener referenceListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_experiencia);

        // Instanciamos una referencia al Contexto
        Context context = this.getApplicationContext();
        //Instanciamos el objeto SharedPreferences y creamos un fichero Privado bajo el
        //nombre definido con la clave preference_file_key en el fichero string.xml
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // RecyclerView para mostrar la lista de suejetos que el investigador va añadiendo a la experiencia
        lista_sujetos = new ArrayList<String>();
        recyclerView = (RecyclerView)findViewById(R.id.sujetos_list);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        titulo = (TextView) findViewById(R.id.textview_titulo);
        linea = findViewById(R.id.line1);
        btn_newuser = (Button) findViewById(R.id.btn_newuser);
        btn_nuevaexperiencia = (Button) findViewById(R.id.btn_nombre_experiencia);
        btn_crearExperiencia = (Button) findViewById(R.id.btn_crearexp);
        edit_nombre_experiencia = (EditText) findViewById(R.id.edittext_experiencia);

        // Cuando el investigador introduce un nombre para la experiencia, se muestra el botón para añadir sujetos de prueba
        btn_nuevaexperiencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titulo.setVisibility(View.VISIBLE);
                linea.setVisibility(View.VISIBLE);
                btn_newuser.setVisibility(View.VISIBLE);
                btn_crearExperiencia.setVisibility(View.VISIBLE);

                if (!isEmpty(edit_nombre_experiencia)) {
                    NOMBRE_EXPERIENCIA = edit_nombre_experiencia.getText().toString();
                    edit_nombre_experiencia.setKeyListener(null);
                    btn_nuevaexperiencia.setEnabled(false);
                }
                else
                    Toast.makeText(getApplicationContext(), "Se debe de introducir un nombre.", Toast.LENGTH_SHORT).show();


            }
        });

        titulo.setVisibility(View.GONE);
        linea.setVisibility(View.GONE);
        btn_newuser.setVisibility(View.GONE);

        btn_adduser = (Button) findViewById(R.id.btn_newuser);
        btn_adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(NuevaExperiencia.this);
                dialog.setContentView(R.layout.activity_formulario);
                dialog.setTitle("Datos de la experiencia");

                TextView textViewTitle = (TextView) dialog.findViewById(R.id.formulario_titulo);
                textViewTitle.setText(edit_nombre_experiencia.getText().toString());

                final EditText edit_nombre = (EditText) dialog.findViewById(R.id.editTextNombre);
                final EditText edit_apellidos = (EditText) dialog.findViewById(R.id.editTextApellidos);
                final EditText edit_fecha_nacimiento = (EditText) dialog.findViewById(R.id.editTextFecha);
                final EditText edit_descripcion = (EditText) dialog.findViewById(R.id.text_descripcion);

                final RadioGroup radio_sexo = (RadioGroup) dialog.findViewById(R.id.radioGenero);
                final RadioGroup radio_opcion_multimedia = (RadioGroup) dialog.findViewById(R.id.radioMultimedia);

                // Botón del dialog de creación de usuario
                Button btn_guardar_datos = (Button) dialog.findViewById(R.id.InicioPrueba_Button);
                btn_guardar_datos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nombre = edit_nombre.getText().toString();
                        NOMBRE_USUARIO = nombre;
                        String apellidos = edit_apellidos.getText().toString();
                        String fecha_nacimiento = edit_fecha_nacimiento.getText().toString();
                        String sexo = ((RadioButton)dialog.findViewById(radio_sexo.getCheckedRadioButtonId())).getText().toString();

                        String opcion_multimedia = ((RadioButton)dialog.findViewById(radio_opcion_multimedia.getCheckedRadioButtonId())).getText().toString();

                        String descripcion = edit_descripcion.getText().toString();
                        Experiencia experiencia = new Experiencia(nombre,apellidos,fecha_nacimiento, sexo,opcion_multimedia,descripcion);

                        WriteFirebase(experiencia);

                        dialog.dismiss();


                            ObtenerKey();
//                        AñadirSujetosRecyclerView();
                    }
                });

                dialog.show();

            }
        });

        // Como ya hemos creado y guardado los usuarios con el RecyclerViewHolder, cerramos.
        btn_crearExperiencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(NuevaExperiencia.this,MainActivity.class);
                startActivity(i);
            }
        });

    }

    public void ObtenerKey() {

        referenceListener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                lista_sujetos.remove(lista_sujetos);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Usuario user = snapshot.getValue(Usuario.class);
                    if (user.getEmail().equals(EMAIL_USUARIO)) {
                        // Obtenemos la key del usuario logueado
                        user_key[0] = snapshot.getKey();
                        AñadirSujetosRecyclerView();
                    }
                }

//                DatabaseReference ref = myRef.child(user_key[0]).child("Experiencias").child(edit_nombre_experiencia.getText().toString());
//                ValueEventListener eventListener = new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                            String sujeto = ds.child(edit_nombre_experiencia.getText().toString()).getValue(String.class);
//                            lista_sujetos.add(sujeto);
//                        }
//                        for (String sujeto : lista_sujetos) {
//                            TextView usersTextView = usersTextView = (TextView) findViewById(R.id.users);
//                            usersTextView.setText(usersTextView.getText().toString() + sujeto + " , ");
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                };
//                ref.addListenerForSingleValueEvent(eventListener);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void  AñadirSujetosRecyclerView() {

        if (user_key[0] != null) {
            referenceListener = myRef.child(user_key[0]).child("Experiencias").child(edit_nombre_experiencia.getText().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    lista_sujetos.clear();
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        String experienciaTitle = singleSnapshot.getKey();
                        if (!experienciaTitle.equals("fechaRealizacion") && !experienciaTitle.equals("pruebaTerminada"))
                        lista_sujetos.add(experienciaTitle);
                    }
                    //                        System.out.println(lista_sujetos.size());
                    //                        lista_sujetos = EliminarRepetidos(lista_sujetos);

                    recyclerViewAdapter = new RecyclerViewAdapterUsuariosExperiencia(NuevaExperiencia.this, lista_sujetos);
                    recyclerView.setAdapter(recyclerViewAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public void WriteFirebase(final Experiencia experiencia) {

        // Obtenemos email del usuario que se ha logueado
        final String email = sharedPref.getString((getString(R.string.email_key)), "");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Usuario user = snapshot.getValue(Usuario.class);
                    if (user.getEmail().equals(email)) {
                        // Obtenemos la key del usuario logueado
                        String key = snapshot.getKey();
                        // Creamos una experiencia con los datos del formulario para ser almacenada en la base de datos en firabase
//                        Experiencia experiencia = ExperienciaFormulario();
                        // Añadimos la informacion del formulario, y en la bd se creara una entrada con la fecha y hora actuales
//                        NOMBRE_EXPERIENCIA = getFechaYHora();
//                        long i =  dataSnapshot.getChildrenCount();
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
                        String strDate = mdformat.format(calendar.getTime());
                        myRef.child(key).child("Experiencias").child(NOMBRE_EXPERIENCIA).child("fechaRealizacion").setValue(strDate);
                        myRef.child(key).child("Experiencias").child(NOMBRE_EXPERIENCIA).child("pruebaTerminada").setValue("no");
                        myRef.child(key).child("Experiencias").child(NOMBRE_EXPERIENCIA).child(experiencia.getNombre()).setValue(experiencia);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(NuevaExperiencia.this,MainActivity.class);
        startActivity(i);
    }


}
