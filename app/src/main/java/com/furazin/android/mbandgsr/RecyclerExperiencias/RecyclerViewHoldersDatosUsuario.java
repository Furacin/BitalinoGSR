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
    private static final String TAG = RecyclerViewHoldersListaUsuarios.class.getSimpleName();
    //    public ImageView markIcon;
    public Button Name;
    //    public ImageView deleteIcon;
    private List<String> experienciasObject;
    public RecyclerViewHoldersDatosUsuario(final View itemView, final List<String> experienciasObject, final Context parent) {
        super(itemView);
        this.experienciasObject = experienciasObject;
        Name = (Button)itemView.findViewById(R.id.experiencia_title);
        Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                System.out.println("HOLA" + Name.getText());
                Intent i = new Intent(itemView.getContext(), InfoExperiencia.class);
                i.putExtra("id_usuario",Name.getText());
                itemView.getContext().startActivity(i);

                // Cerramos el activity del que venimos
                ((UsuariosExperiencia)parent).finish();
            }
        });
//        markIcon = (ImageView)itemView.findViewById(R.id.task_icon);
//        deleteIcon = (ImageView)itemView.findViewById(R.id.task_delete);
//        deleteIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "Delete icon has been clicked", Toast.LENGTH_LONG).show();
//                String taskTitle = experienciasObject.get(getAdapterPosition()).getNombre();
//                Log.d(TAG, "Task Title " + taskTitle);
//                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
//                Query applesQuery = ref.orderByChild("task").equalTo(taskTitle);
//                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
//                            appleSnapshot.getRef().removeValue();
//                        }
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.e(TAG, "onCancelled", databaseError.toException());
//                    }
//                });
//            }
//        });
    }
}
