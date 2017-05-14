package com.furazin.android.mbandgsr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.furazin.android.mbandgsr.Dialog.DateDialog;

/**
 * Created by manza on 11/05/2017.
 */

public class Formulario extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        EditText txtDate = (EditText)findViewById(R.id.editTextFecha);
        txtDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    DateDialog dialog = new DateDialog(view);
                    android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");
                }
            }
        });

//        ImageButton dateButton = (ImageButton) findViewById(R.id.dateButton);
//        dateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DateDialog dialog = new DateDialog(view);
//                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
//                dialog.show(ft,"DatePicker");
//            }
//        });
    }

}
