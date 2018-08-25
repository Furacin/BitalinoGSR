package com.furazin.android.mbandgsr.Dialog;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import com.furazin.android.mbandgsr.Formulario;

import java.util.Calendar;

/**
 * Created by manza on 14/05/2017.
 */

@SuppressLint("ValidFragment")
public class NuevoSujetoDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

//    EditText txtDate;
//    public NuevoSujetoDialog(View view) {
//        txtDate = (EditText)view;
//    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String date = day + "-" + (month+1) + "-" + year;
        Formulario.txtDate.setText(date);
    }
}
