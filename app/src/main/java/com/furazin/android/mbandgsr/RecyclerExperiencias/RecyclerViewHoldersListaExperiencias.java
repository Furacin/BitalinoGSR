package com.furazin.android.mbandgsr.RecyclerExperiencias;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.furazin.android.mbandgsr.R;
import com.furazin.android.mbandgsr.SujetosExperiencia;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewHoldersListaExperiencias extends RecyclerView.ViewHolder{
    private static final String TAG = RecyclerViewHoldersListaExperiencias.class.getSimpleName();
    public ImageButton btnAbrirExperiencia;
    public TextView nombreExperiencia;
    public TextView fechaExperiencia;
    public CircleImageView marcaExperienciaTerminada;

    private ArrayList<ArrayList<String>> experienciasObject;
    public RecyclerViewHoldersListaExperiencias(final View itemView, final ArrayList<ArrayList<String>> experienciasObject) {
        super(itemView);
        this.experienciasObject = experienciasObject;
        btnAbrirExperiencia = (ImageButton)itemView.findViewById(R.id.btnAbrirExperiencia);
        nombreExperiencia = (TextView)itemView.findViewById(R.id.experiencia_title);
        fechaExperiencia = (TextView)itemView.findViewById(R.id.fechaExperiencia);
        marcaExperienciaTerminada = (CircleImageView)itemView.findViewById(R.id.marcaExperienciaTerminada);
        btnAbrirExperiencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(itemView.getContext(), SujetosExperiencia.class);
                i.putExtra("id_experiencia",nombreExperiencia.getText());
                itemView.getContext().startActivity(i);
            }
        });
    }
}