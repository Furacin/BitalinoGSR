package com.furazin.android.mbandgsr.RecyclerExperiencias;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.furazin.android.mbandgsr.MainActivity;
import com.furazin.android.mbandgsr.R;
import com.furazin.android.mbandgsr.SujetosExperiencia;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewHoldersListaExperiencias extends RecyclerView.ViewHolder{
    private static final String TAG = RecyclerViewHoldersListaExperiencias.class.getSimpleName();
    public ImageButton btnAbrirExperiencia;
    public TextView nombreExperiencia;
    public TextView fechaExperiencia;
    public CircleImageView marcaExperienciaTerminada;
    public ImageButton btnEliminarExperiencia;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("users");

    private ArrayList<ArrayList<String>> experienciasObject;
    public RecyclerViewHoldersListaExperiencias(final View itemView, final ArrayList<ArrayList<String>> experienciasObject) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(itemView.getContext(), SujetosExperiencia.class);
                i.putExtra("id_experiencia",nombreExperiencia.getText());
                itemView.getContext().startActivity(i);
            }
        });

        this.experienciasObject = experienciasObject;
        btnAbrirExperiencia = (ImageButton)itemView.findViewById(R.id.btnAbrirExperiencia);
        nombreExperiencia = (TextView)itemView.findViewById(R.id.experiencia_title);
        fechaExperiencia = (TextView)itemView.findViewById(R.id.fechaExperiencia);
        btnEliminarExperiencia = (ImageButton) itemView.findViewById(R.id.btnEliminarExperiencia);
        marcaExperienciaTerminada = (CircleImageView)itemView.findViewById(R.id.marcaExperienciaTerminada);
        btnAbrirExperiencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(itemView.getContext(), SujetosExperiencia.class);
                i.putExtra("id_experiencia",nombreExperiencia.getText());
                itemView.getContext().startActivity(i);
            }
        });
        btnEliminarExperiencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                String pregunta = "¿Eliminar ";
                // Extraemos el caracter del paréntesis
                final String experiencia = nombreExperiencia.getText().toString();
                String pregunta2 = "?";
                SpannableString str = new SpannableString(pregunta + experiencia + pregunta2);
                str.setSpan(new StyleSpan(Typeface.BOLD), pregunta.length(), pregunta.length() + experiencia.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setMessage(str)
                        .setPositiveButton(R.string.dialog_si, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                myRef.child(MainActivity.user_key).child("Experiencias").child(experiencia).removeValue();
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
    }
}