package com.furazin.android.mbandgsr.RecyclerExperiencias;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.furazin.android.mbandgsr.R;

import java.util.List;

/**
 * Created by manza on 15/06/2017.
 */

public class RecyclerViewAdapterDatosUsuario extends RecyclerView.Adapter<RecyclerViewHoldersDatosUsuario> {
    private List<String> experiencias;
    protected Context context;
    public RecyclerViewAdapterDatosUsuario(Context context, List<String> experiencias) {
        this.experiencias = experiencias;
        this.context = context;
    }
    @Override
    public RecyclerViewHoldersDatosUsuario onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHoldersDatosUsuario viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_list, parent, false);
        viewHolder = new RecyclerViewHoldersDatosUsuario(layoutView, experiencias);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHoldersDatosUsuario holder, int position) {
        holder.Name.setText(experiencias.get(position));
    }

    @Override
    public int getItemCount() {
        return this.experiencias.size();
    }
}
