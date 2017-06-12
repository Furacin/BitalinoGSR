package com.furazin.android.mbandgsr.RecyclerExperiencias;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.furazin.android.mbandgsr.R;

import java.util.List;
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {
    private List<String> experiencias;
    protected Context context;
    public RecyclerViewAdapter(Context context, List<String> experiencias) {
        this.experiencias = experiencias;
        this.context = context;
    }
    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolders viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_list, parent, false);
        viewHolder = new RecyclerViewHolders(layoutView, experiencias);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        holder.Name.setText(experiencias.get(position));
    }
    @Override
    public int getItemCount() {
        return this.experiencias.size();
    }
}
