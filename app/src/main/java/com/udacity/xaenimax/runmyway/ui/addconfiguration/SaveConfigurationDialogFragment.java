package com.udacity.xaenimax.runmyway.ui.addconfiguration;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.udacity.xaenimax.runmyway.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SaveConfigurationDialogFragment extends DialogFragment {
    private Unbinder unbinder;
    private SaveConfigListener mListener;

    @BindView(R.id.save_button)
    Button confirmButton;
    @BindView(R.id.cancel_button)
    Button cancelButton;
    @BindView(R.id.config_name_et)
    EditText configNameEditText;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.fragment_save_configuration_dialog);
        // Create the AlertDialog object and return it
        return builder.create();

    }

    @Override
    public void onStart() {
        super.onStart();
        unbinder = ButterKnife.bind(this, getDialog());
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editable = configNameEditText.getText();
                if (editable != null && !editable.toString().equals(""))
                    mListener.onSaveButtonPressed(editable.toString());
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancelButtonPressed();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SaveConfigListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public interface SaveConfigListener{
        void onSaveButtonPressed(String configName);
        void onCancelButtonPressed();
    }
}
