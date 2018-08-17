package com.furazin.android.mbandgsr.RecyclerExperiencias;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.furazin.android.mbandgsr.R;

import java.util.ArrayList;
public class RecyclerViewAdapterListaSujetos extends RecyclerView.Adapter<RecyclerViewHoldersListaSujetos> {
    private ArrayList<ArrayList<String>> experiencias;
    protected Context context;
    private String terminada;
    public RecyclerViewAdapterListaSujetos(Context context, ArrayList<ArrayList<String>> experiencias) {
        this.experiencias = experiencias;
        this.context = context;

    }
    @Override
    public RecyclerViewHoldersListaSujetos onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHoldersListaSujetos viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_list, parent, false);
        viewHolder = new RecyclerViewHoldersListaSujetos(layoutView, experiencias);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHoldersListaSujetos holder, int position) {
        holder.nombreExperiencia.setText(experiencias.get(position).get(0));
        holder.fechaExperiencia.setText(experiencias.get(position).get(1));
        // Comprobamos si la experiencia está terminada o no y en función de ello coloreamos
        // el ImageView de verde(terminada) o de amarillo(no terminada)
        terminada = experiencias.get(position).get(2);
        if (terminada.equals("no")) {
            Drawable icon = this.context.getResources().getDrawable(R.drawable.ic_color_verde);
            holder.marcaExperienciaTerminada.setImageDrawable(icon);
        }
        else {
            Drawable icon = this.context.getResources().getDrawable(R.drawable.ic_color_amarillo);
            holder.marcaExperienciaTerminada.setImageDrawable(icon);
        }

    }
    @Override
    public int getItemCount() {
            return this.experiencias.size();
    }
}
