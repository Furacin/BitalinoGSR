package com.furazin.android.mbandgsr.RecyclerExperiencias;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.furazin.android.mbandgsr.R;

import java.util.List;

/**
 * Created by manza on 16/06/2017.
 */

public class RecyclerViewAdapterUsuariosExperiencia extends RecyclerView.Adapter<RecyclerViewHoldersUsuariosExperiencia>{

    private List<String> usuarios_creados;
    protected Context context;

    public RecyclerViewAdapterUsuariosExperiencia(List<String> usuarios_creados, Context context) {
        this.usuarios_creados = usuarios_creados;
        this.context = context;
    }

    @Override
    public RecyclerViewHoldersUsuariosExperiencia onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHoldersUsuariosExperiencia viewHolder = null;
        View layouView = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_list_2, parent, false);
        viewHolder = new RecyclerViewHoldersUsuariosExperiencia(layouView,usuarios_creados);
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHoldersUsuariosExperiencia holder, int position) {
        holder.nombre_usuario.setText(usuarios_creados.get(position));
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
