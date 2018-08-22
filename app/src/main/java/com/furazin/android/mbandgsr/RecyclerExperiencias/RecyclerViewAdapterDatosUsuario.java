package com.furazin.android.mbandgsr.RecyclerExperiencias;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.furazin.android.mbandgsr.R;

import java.util.ArrayList;

/**
 * Created by manza on 15/06/2017.
 */

public class RecyclerViewAdapterDatosUsuario extends RecyclerView.Adapter<RecyclerViewHoldersDatosUsuario> {
    private ArrayList<ArrayList<String>> usuario;
    protected Context context;
    private String opcion_multimedia;
    public RecyclerViewAdapterDatosUsuario(Context context, ArrayList<ArrayList<String>> usuarios) {
        this.usuario = usuarios;
        this.context = context;
    }
    @Override
    public RecyclerViewHoldersDatosUsuario onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerViewHoldersDatosUsuario viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_list_3, parent, false);
        viewHolder = new RecyclerViewHoldersDatosUsuario(layoutView, usuario,context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHoldersDatosUsuario holder, int position) {
//        holder.Name.setText(experiencias.get(position));
        holder.Name.setText(usuario.get(position).get(0));
        if (usuario.get(position).get(0).equals("Finalizada")) {
            holder.Name.setTextColor(Color.parseColor("#4EE57F"));
            holder.Name.setTypeface(null, Typeface.BOLD_ITALIC);
        }
        else {
            if (usuario.get(position).get(0).equals("Pendiente")) {
                holder.Name.setTextColor(Color.parseColor("#E5DD81"));
                holder.Name.setTypeface(null, Typeface.BOLD_ITALIC);
            }
        }
        holder.Apellidos.setText(usuario.get(position).get(1));
        opcion_multimedia = usuario.get(position).get(2);
        Drawable icon;
        switch (opcion_multimedia) {
            case "Sólo Audio":
                icon = this.context.getResources().getDrawable(R.drawable.audio);
                holder.MarcaMultimedia.setImageDrawable(icon);
                break;
            case "Sólo Vídeo":
                icon = this.context.getResources().getDrawable(R.drawable.video);
                holder.MarcaMultimedia.setImageDrawable(icon);
                break;
            case "Ninguno":
                icon = this.context.getResources().getDrawable(R.drawable.ic_ninguno);
                holder.MarcaMultimedia.setImageDrawable(icon);
                break;
        }
        holder.sujeto_key = usuario.get(position).get(3).toString();
    }

    @Override
    public int getItemCount() {
        return this.usuario.size();
    }
}
