package com.furazin.android.mbandgsr.FirebaseBD;

import android.util.Pair;

import java.util.ArrayList;

/**
 * Created by manza on 08/06/2017.
 */

public class ValoresGraficas {
    ArrayList<Pair<String,String>> valores_gsr;
    ArrayList<Pair<String,String>> valores_temperatura;
    ArrayList<Pair<String,String>> valores_fc;

    public ValoresGraficas() {

    }

    public ValoresGraficas(ArrayList<Pair<String, String>> valores_gsr, ArrayList<Pair<String, String>> valores_temperatura, ArrayList<Pair<String, String>> valores_fc) {
        this.valores_gsr = valores_gsr;
        this.valores_temperatura = valores_temperatura;
        this.valores_fc = valores_fc;
    }

    public ArrayList<Pair<String, String>> getValores_gsr() {
        return valores_gsr;
    }

    public ArrayList<Pair<String, String>> getValores_temperatura() {
        return valores_temperatura;
    }

    public ArrayList<Pair<String, String>> getValores_fc() {
        return valores_fc;
    }
}
