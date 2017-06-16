package com.furazin.android.mbandgsr;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.furazin.android.mbandgsr.FirebaseBD.Experiencia;
import com.furazin.android.mbandgsr.FirebaseBD.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by manza on 15/06/2017.
 */

public class NuevaExperiencia extends AppCompatActivity {

    public static String NOMBRE_EXPERIENCIA = "";

    Button btn_adduser;
    TextView titulo;
    View linea;
    Button btn_newuser, btn_nuevaexperiencia;
    EditText edit_nombre_experiencia;

    // Variable para recordar las credenciales del usuario
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_experiencia);

        // Instanciamos una referencia al Contexto
        Context context = this.getApplicationContext();
        //Instanciamos el objeto SharedPrefere  nces y creamos un fichero Privado bajo el
        //nombre definido con la clave preference_file_key en el fichero string.xml
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        titulo = (TextView) findViewById(R.id.textview_titulo);
        linea = findViewById(R.id.line1);
        btn_newuser = (Button) findViewById(R.id.btn_newuser);
        btn_nuevaexperiencia = (Button) findViewById(R.id.btn_nombre_experiencia);
        edit_nombre_experiencia = (EditText) findViewById(R.id.edittext_experiencia);

        btn_nuevaexperiencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titulo.setVisibility(View.VISIBLE);
                linea.setVisibility(View.VISIBLE);
                btn_newuser.setVisibility(View.VISIBLE);

                if (!isEmpty(edit_nombre_experiencia)) {
                    NOMBRE_EXPERIENCIA = edit_nombre_experiencia.getText().toString();
//                    WriteFirebase(NOMBRE_EXPERIENCIA);
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
                final Dialog dialog = new Dialog(NuevaExperiencia.this);
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

                Button btn_guardar_datos = (Button) dialog.findViewById(R.id.InicioPrueba_Button);
                btn_guardar_datos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nombre = edit_nombre.getText().toString();
                        String apellidos = edit_apellidos.getText().toString();
                        String fecha_nacimiento = edit_fecha_nacimiento.getText().toString();
                        String sexo = ((RadioButton)dialog.findViewById(radio_sexo.getCheckedRadioButtonId())).getText().toString();

                        String opcion_multimedia = ((RadioButton)dialog.findViewById(radio_opcion_multimedia.getCheckedRadioButtonId())).getText().toString();

                        String descripcion = edit_descripcion.getText().toString();
                        Experiencia experiencia = new Experiencia(nombre,apellidos,fecha_nacimiento, sexo,opcion_multimedia,descripcion);
                        WriteFirebase(experiencia);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

//    public void WriteFirebase(final String nombre) {
//
//        // Obtenemos email del usuario que se ha logueado
//        final String email = sharedPref.getString((getString(R.string.email_key)), "");
//
//        // Write a message to the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        final DatabaseReference myRef = database.getReference("users");
//
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Usuario user = snapshot.getValue(Usuario.class);
//                    if (user.getEmail().equals(email)) {
//                        // Obtenemos la key del usuario logueado
//                        String key = snapshot.getKey();
//                        // Creamos una experiencia con los datos del formulario para ser almacenada en la base de datos en firabase
////                        Experiencia experiencia = ExperienciaFormulario();
//                        // Añadimos la informacion del formulario, y en la bd se creara una entrada con la fecha y hora actuales
////                        NOMBRE_EXPERIENCIA = getFechaYHora();
//                        myRef.child(key).child("Experiencias").child(NOMBRE_EXPERIENCIA).child("terminada").setValue("no");
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }

    public void WriteFirebase(final Experiencia experiencia) {

        // Obtenemos email del usuario que se ha logueado
        final String email = sharedPref.getString((getString(R.string.email_key)), "");

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users");

        myRef.addValueEventListener(new ValueEventListener() {
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
                        myRef.child(key).child("Experiencias").child(NOMBRE_EXPERIENCIA).child(experiencia.getNombre()).setValue(experiencia);
//                        myRef.child(key).child("Experiencias").child(NOMBRE_EXPERIENCIA).child("terminada").setValue("no");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

//    String getFechaYHora() {
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat mdformat = new SimpleDateFormat("yyyyMMdd");
//        String currentDate = mdformat.format(calendar.getTime());
//
//        SimpleDateFormat format = new SimpleDateFormat("HHmm", Locale.US);
//        String hour = format.format(new Date());
//
//        currentDate+=hour;
//
//        return currentDate;
//    }
}
