package com.furazin.android.mbandgsr.RecyclerExperiencias;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.furazin.android.mbandgsr.NuevaExperiencia;
import com.furazin.android.mbandgsr.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


/**
 * Created by manza on 16/06/2017.
 */

public class RecyclerViewHoldersUsuariosExperiencia extends RecyclerView.ViewHolder {

    private static final String TAG = RecyclerViewHoldersUsuariosExperiencia.class.getSimpleName();

    public TextView nombre_usuario;
    public ImageButton btnEliminarSujeto;

    private List<String> lista_usuarios;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("users");

    public RecyclerViewHoldersUsuariosExperiencia(final View itemView, final List<String> lista_usuarios) {
        super(itemView);
        this.lista_usuarios = lista_usuarios;
        nombre_usuario = (TextView) itemView.findViewById(R.id.usuario_name);
        btnEliminarSujeto = (ImageButton) itemView.findViewById(R.id.btnEliminarSujeto);
        btnEliminarSujeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                String pregunta = "¿Quitar de la lista a ";
                // Extraemos el caracter del paréntesis
                String nombreUsuario = nombre_usuario.getText().toString().substring(3,nombre_usuario.getText().toString().length());
                String pregunta2 = " ?";
                SpannableString  str = new SpannableString(pregunta + nombreUsuario + pregunta2);
                str.setSpan(new StyleSpan(Typeface.BOLD), pregunta.length(), pregunta.length() + nombreUsuario.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setMessage(str)
                        .setPositiveButton(R.string.dialog_si, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String i = String.valueOf(nombre_usuario.getText().toString().charAt(0));
                                myRef.child(NuevaExperiencia.user_key).child("Experiencias").child(NuevaExperiencia.NOMBRE_EXPERIENCIA).child(i).removeValue();
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancelar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        })
                        .show();
                builder.create();
            }
        });
//        start_exp = (Button) itemView.findViewById(R.id.start_exp);
//        start_exp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                System.out.println("HOLA" + Name.getText());
//                Intent i = new Intent(itemView.getContext(), DatosGSR.class);
//                i.putExtra("id_usuario",nombre_usuario.getText());
//                itemView.getContext().startActivity(i);
//            }
//        });
    }
}
