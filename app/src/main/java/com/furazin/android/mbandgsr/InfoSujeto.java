package com.furazin.android.mbandgsr;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.furazin.android.mbandgsr.FirebaseBD.Experiencia;
import com.furazin.android.mbandgsr.FirebaseBD.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by manza on 12/06/2017.
 */

public class InfoSujeto extends AppCompatActivity {

    public static String id_usuario;
    String EMAIL_USUARIO = MainActivity.EMAIL_USUARIO;
    TextView fecha, nombre, apellidos, fecha_nacimiento, descripcion, txtTerminada, txtTipoPrueba;
    ImageView sexo;
    Button btnStartExperiencia, btnEditarSujeto;
    Button btn_guardar_datos;

    EditText edit_nombre;
    EditText edit_apellidos;
    EditText edit_fecha_nacimiento;
    EditText edit_descripcion;
    RadioGroup radio_sexo;
    RadioGroup radio_opcion_multimedia;

    // Variable para almacenar el tipo de prueba
    static public String tipoPrueba = "";
    static public String sexo_sujeto = "";

    Dialog dialog;
    private ValueEventListener referenceListener;
    public String user_key;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("users");

    private SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_sujeto);

        // Instanciamos una referencia al Contexto
        Context context = this.getApplicationContext();
        //Instanciamos el objeto SharedPreferences y creamos un fichero Privado bajo el
        //nombre definido con la clave preference_file_key en el fichero string.xml
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        fecha = (TextView) findViewById(R.id.fechaRealizacion);
        nombre = (TextView) findViewById(R.id.nombre);
        apellidos = (TextView) findViewById(R.id.apellidos);
        sexo = (ImageView) findViewById(R.id.sexo);
        fecha_nacimiento = (TextView) findViewById(R.id.fecha_nacimiento);
        descripcion = (TextView) findViewById(R.id.descripcion);
        btnStartExperiencia = (Button) findViewById(R.id.btnStartExperiencia);
        txtTerminada = (TextView) findViewById(R.id.experienciaTerminada);
        txtTipoPrueba = (TextView) findViewById(R.id.tipoPruebaInfoExperiencia);
        btnEditarSujeto = (Button) findViewById(R.id.btnEditarSujeto);


        id_usuario = getIntent().getExtras().getString("id_usuario");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users");

        dialog = new Dialog(InfoSujeto.this);
        dialog.setContentView(R.layout.activity_formulario);
        dialog.setTitle("");

        TextView textViewTitle = (TextView) dialog.findViewById(R.id.formulario_titulo);
        textViewTitle.setText("Editar información de sujeto");

        btn_guardar_datos = (Button) dialog.findViewById(R.id.InicioPrueba_Button);
        edit_nombre = (EditText) dialog.findViewById(R.id.editTextNombre);
        edit_apellidos = (EditText) dialog.findViewById(R.id.editTextApellidos);
        edit_fecha_nacimiento = (EditText) dialog.findViewById(R.id.editTextFecha);
        edit_descripcion = (EditText) dialog.findViewById(R.id.text_descripcion);
        radio_sexo = (RadioGroup) dialog.findViewById(R.id.radioGenero);
        radio_opcion_multimedia = (RadioGroup) dialog.findViewById(R.id.radioMultimedia);

        // Esconder Opciones Multimedia del Dialog
        radio_opcion_multimedia.setVisibility(View.INVISIBLE);
        TextView lblMultimedia = (TextView)dialog.findViewById(R.id.TextViewMultimedia);
        lblMultimedia.setVisibility(View.INVISIBLE);
        View line2 = dialog.findViewById(R.id.line2);
        line2.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.line1);
        params.setMargins(170,30,0,0);
        btn_guardar_datos.setLayoutParams(params);

        btnEditarSujeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Botón del dialog de creación de usuario
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

                        ObtenerKey();

                        WriteFirebase(experiencia);

                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        btnStartExperiencia.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    btnStartExperiencia.setAlpha(0.3f);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    btnStartExperiencia.setAlpha(1f);
                }
                return false;
            }
        });

        btnStartExperiencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.activity.finish();
                Intent i = new Intent(getApplicationContext(), DatosGSR.class);
                i.putExtra("id_usuario",id_usuario);
                startActivity(i);
                finish();
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Usuario user = snapshot.getValue(Usuario.class);
                    final String user_key;
                    if (user.getEmail().equals(EMAIL_USUARIO)) {
                        // Obtenemos la key del usuario logueado
                        user_key = snapshot.getKey();
                        myRef.child(user_key).child("Experiencias").child(SujetosExperiencia.NOMBRE_EXPERIENCIA).child(id_usuario).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Experiencia e = dataSnapshot.getValue(Experiencia.class);
//                                System.out.println(e.getNombre());
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar c = Calendar.getInstance();
                                String date = sdf.format(c.getTime());
//                                txtTituloExperiencia.setText(SujetosExperiencia.NOMBRE_EXPERIENCIA);
                                fecha.setText("Fecha de realización de la prueba: " + date);
                                nombre.setText(e.getNombre());
                                apellidos.setText( e.getApellidos());
                                if (e.getSexo().equals("Femenino")) {
                                    sexo.setImageResource(R.drawable.mujer);
                                    sexo_sujeto = "mujer";
                                }
                                else {
                                    sexo.setImageResource(R.drawable.hombre);
                                    sexo_sujeto = "hombre";
                                }
                                fecha_nacimiento.setText(e.getFecha_nacimiento());
                                descripcion.setText(e.getDescripcion());
                                txtTipoPrueba.setText(e.getOpcion_multimedia());
                                tipoPrueba = e.getOpcion_multimedia();

                                if (e.getTerminada().equals("si")) {
                                    txtTerminada.setVisibility(View.VISIBLE);
//                                    btnStartExperiencia.setEnabled(false);
                                    btnStartExperiencia.setAlpha(.3f);
                                    btnStartExperiencia.setClickable(false);
                                }
                                setCamposDialog();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                        user_key = snapshot.getKey();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                        myRef.child(key).child("Experiencias").child(SujetosExperiencia.NOMBRE_EXPERIENCIA).child(String.valueOf(id_usuario)).child("apellidos").setValue(experiencia.getApellidos());
                        myRef.child(key).child("Experiencias").child(SujetosExperiencia.NOMBRE_EXPERIENCIA).child(String.valueOf(id_usuario)).child("descripcion").setValue(experiencia.getDescripcion());
                        myRef.child(key).child("Experiencias").child(SujetosExperiencia.NOMBRE_EXPERIENCIA).child(String.valueOf(id_usuario)).child("fecha_nacimiento").setValue(experiencia.getFecha_nacimiento());
                        myRef.child(key).child("Experiencias").child(SujetosExperiencia.NOMBRE_EXPERIENCIA).child(String.valueOf(id_usuario)).child("nombre").setValue(experiencia.getNombre());
                        myRef.child(key).child("Experiencias").child(SujetosExperiencia.NOMBRE_EXPERIENCIA).child(String.valueOf(id_usuario)).child("sexo").setValue(experiencia.getSexo());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                myRef.child(user_key).child("Experiencias").child(SujetosExperiencia.NOMBRE_EXPERIENCIA).removeValue();
            }
        });

    }


    public void setCamposDialog() {
        edit_nombre.setText( nombre.getText());
        edit_apellidos.setText(apellidos.getText());
        edit_fecha_nacimiento.setText(fecha_nacimiento.getText());

        if (sexo_sujeto.equals("hombre")) {
            ((RadioButton)dialog.findViewById(R.id.radioMasculino)).setChecked(true);
            ((RadioButton)dialog.findViewById(R.id.radioFemenino)).setChecked(false);
        }
        else {
            ((RadioButton)dialog.findViewById(R.id.radioMasculino)).setChecked(false);
            ((RadioButton)dialog.findViewById(R.id.radioFemenino)).setChecked(true);
        }

//        if ( tipoPrueba.equals("Sólo Vídeo") ) {
//            ((RadioButton)dialog.findViewById(R.id.radioVideo)).setChecked(true);
//            ((RadioButton)dialog.findViewById(R.id.radioAudio)).setChecked(false);
//            ((RadioButton)dialog.findViewById(R.id.radioNoMultimedia)).setChecked(false);
//        }
//        else {
//            if ( tipoPrueba.equals("Sólo Audio") ) {
//                ((RadioButton)dialog.findViewById(R.id.radioVideo)).setChecked(false);
//                ((RadioButton)dialog.findViewById(R.id.radioAudio)).setChecked(true);
//                ((RadioButton)dialog.findViewById(R.id.radioNoMultimedia)).setChecked(false);
//            }
//            else {
//                ((RadioButton)dialog.findViewById(R.id.radioVideo)).setChecked(false);
//                ((RadioButton)dialog.findViewById(R.id.radioAudio)).setChecked(false);
//                ((RadioButton)dialog.findViewById(R.id.radioNoMultimedia)).setChecked(true);
//            }
//        }
//
        edit_descripcion.setText(descripcion.getText());
    }
}
