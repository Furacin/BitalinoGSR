package com.furazin.android.mbandgsr.RecyclerExperiencias;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.furazin.android.mbandgsr.R;

import java.util.List;
public class RecyclerViewAdapterListaSujetos extends RecyclerView.Adapter<RecyclerViewHoldersListaSujetos> {
    private List<String> experiencias;
    protected Context context;
    public RecyclerViewAdapterListaSujetos(Context context, List<String> experiencias) {
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
        holder.Name.setText(experiencias.get(position));
    }
    @Override
    public int getItemCount() {
            return this.experiencias.size();
    }
}
