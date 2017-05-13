package com.example.jeka.exampledrawerbar.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import com.example.jeka.exampledrawerbar.R;

public class InfoFragment extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        return new AlertDialog.Builder(getActivity())
                .setView(R.layout.dialog_info)
                .setTitle(R.string.info_dialog_title)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
