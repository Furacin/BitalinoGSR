package com.furazin.android.mbandgsr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.furazin.android.mbandgsr.Dialog.DateDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by manza on 11/05/2017.
 */

public class Formulario extends AppCompatActivity{

    public static EditText txtDate;
    EditText nombre, apellidos, fecha_nacimiento, descripcion;
    RadioButton sexo_masculino, sexo_femenino;

    // Variable para recordar las credenciales del usuario
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        // Instanciamos una referencia al Contexto
        Context context = this.getApplicationContext();
        //Instanciamos el objeto SharedPrefere  nces y creamos un fichero Privado bajo el
        //nombre definido con la clave preference_file_key en el fichero string.xml
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        txtDate = (EditText)findViewById(R.id.editTextFecha);
        txtDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    DateDialog dialog = new DateDialog();// = new DateDialog(view);
                    android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");
                }
            }
        });

        ImageButton dateIcon = (ImageButton) findViewById(R.id.dateButton);
        dateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateDialog dialog = new DateDialog(); //= new DateDialog(view);
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
            }
        });

        nombre = (EditText) findViewById(R.id.editTextNombre);
        apellidos = (EditText) findViewById(R.id.editTextApellidos);
        fecha_nacimiento = (EditText) findViewById(R.id.editTextFecha);
        descripcion = (EditText) findViewById(R.id.text_descripcion);

        sexo_femenino = (RadioButton) findViewById(R.id.radioButton1) ;
        sexo_masculino = (RadioButton) findViewById(R.id.radioButton2) ;

        Button ButtonInicio = (Button) findViewById(R.id.InicioPrueba_Button);
        ButtonInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEmpty(nombre) && !isEmpty(apellidos) && !isEmpty(fecha_nacimiento) && !isEmpty(descripcion)) {
                    PushFirebase();
                    Intent i = new Intent(getApplicationContext(), DatosGSR.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(getApplicationContext(), "No puede estar ningún campo del formulario vacío", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public void PushFirebase() {

        // Obtenemos email del usuario que se ha logueado
        final String email = sharedPref.getString((getString(R.string.email_key)), "");

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
//        myRef.push().setValue(email);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String db_email = snapshot.getValue(String.class);
                    Usuario user = snapshot.getValue(Usuario.class);
                    if (user.getEmail().equals(email)) {
                        System.out.println( "HOLAaa" + user.getEmail());
                    }
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        String key = myRef.child("users").push().getKey();
//        DatabaseReference myRef = database.getReference(email);
//        DatabaseReference myRef = database.getReference("users");
//        myRef.push();

//        myRef.setValue(email);
//        myRef.child("nombre").setValue(nombre);
//        myRef.child("apellidos").setValue(apellidos);
//        myRef.child("fecha_nacimiento").setValue(fecha_nacimiento);
//        myRef.child("descripcion").setValue(descripcion);
    }

}
