package com.furazin.android.mbandgsr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.furazin.android.mbandgsr.Dialog.DateDialog;

/**
 * Created by manza on 11/05/2017.
 */

public class Formulario extends AppCompatActivity{

    public static EditText txtDate;
    EditText nombre, apellidos, fecha_nacimiento, descripcion;
    RadioButton sexo_masculino, sexo_femenino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

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

}
