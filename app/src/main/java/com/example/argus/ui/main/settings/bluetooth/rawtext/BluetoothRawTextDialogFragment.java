package com.example.argus.ui.main.settings.bluetooth.rawtext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.argus.MainActivityViewModel;
import com.example.argus.R;
import com.google.android.material.snackbar.Snackbar;

import java.nio.charset.StandardCharsets;

public class BluetoothRawTextDialogFragment extends DialogFragment {

    private MainActivityViewModel mainModel;
    private static final String TAG = "TAG";
    private View view;
    public BluetoothRawTextDialogFragment() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.mainModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_raw_text, null);
        builder.setPositiveButton(R.string.send,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing here because we override this button later to change the close behaviour.
            }
        });
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Dialog buttons
        AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // User clicked OK, so save the selectedItems results somewhere
                    // or return them to the component that opened the dialog
                    try {
                        sendMessage();
                        // dismiss();
                    } catch (Exception e) {
                        Log.w("APPLY", "exception: " + e.getMessage() );
                    }
                }
            });
        }
    }

    private void sendMessage() {
        try {
            if(this.mainModel.getClientThread().getValue() != null) {
                EditText editText = ((EditText)view.findViewById(R.id.raw_edit_text));
                byte[] data = String.valueOf(editText.getText()).getBytes(StandardCharsets.UTF_8);
                this.mainModel.getClientThread().getValue().write(data);
                editText.setText("");
            }
            else {
                throw new Exception("Not connected !");
            }
        } catch (Exception e) {
            Log.e(TAG, "sendMessage: ", e);
            Snackbar.make(view, "Veuillez vous connecter pour envoyer", Snackbar.LENGTH_LONG).show();
        }
    }
}

