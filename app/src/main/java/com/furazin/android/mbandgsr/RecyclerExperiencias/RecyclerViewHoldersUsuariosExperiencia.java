package com.furazin.android.mbandgsr.RecyclerExperiencias;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.furazin.android.mbandgsr.DatosGSR;
import com.furazin.android.mbandgsr.R;

import java.util.List;


/**
 * Created by manza on 16/06/2017.
 */

public class RecyclerViewHoldersUsuariosExperiencia extends RecyclerView.ViewHolder {

    private static final String TAG = RecyclerViewHoldersUsuariosExperiencia.class.getSimpleName();

    public TextView nombre_usuario;
    public Button start_exp;

    private List<String> lista_usuarios;

    public RecyclerViewHoldersUsuariosExperiencia(final View itemView, final List<String> lista_usuarios) {
        super(itemView);
        this.lista_usuarios = lista_usuarios;
        nombre_usuario = (TextView) itemView.findViewById(R.id.usuario_name);
        start_exp = (Button) itemView.findViewById(R.id.start_exp);
        start_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                System.out.println("HOLA" + Name.getText());
                Intent i = new Intent(itemView.getContext(), DatosGSR.class);
                i.putExtra("id_usuario",nombre_usuario.getText());
                itemView.getContext().startActivity(i);
            }
        });
    }
}
