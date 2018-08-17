package com.furazin.android.mbandgsr.RecyclerExperiencias;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.furazin.android.mbandgsr.InfoExperiencia;
import com.furazin.android.mbandgsr.R;
import com.furazin.android.mbandgsr.UsuariosExperiencia;

import java.util.List;

/**
 * Created by manza on 15/06/2017.
 */

public class RecyclerViewHoldersDatosUsuario extends RecyclerView.ViewHolder {
    private static final String TAG = RecyclerViewHoldersListaSujetos.class.getSimpleName();
    //    public ImageView markIcon;
    public Button Name;

    private List<String> experienciasObject;
    public RecyclerViewHoldersDatosUsuario(final View itemView, final List<String> experienciasObject, final Context parent) {
        super(itemView);
        this.experienciasObject = experienciasObject;
        Name = (Button)itemView.findViewById(R.id.experiencia_title_3);
        Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(itemView.getContext(), InfoExperiencia.class);
                i.putExtra("id_usuario",Name.getText());
                itemView.getContext().startActivity(i);

                // Cerramos el activity del que venimos
                ((UsuariosExperiencia)parent).finish();
            }
        });
    }
}
