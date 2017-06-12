package com.furazin.android.mbandgsr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by manza on 12/06/2017.
 */

public class InfoExperiencia extends AppCompatActivity {

    String experiencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiencia);

        experiencia = getIntent().getExtras().getString("id_experiencia");
//        System.out.println(experiencia)
        System.out.println("HOLA" + experiencia);
    }
}
