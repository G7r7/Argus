package com.example.argus.ui.main.settings.bluetooth.server;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.argus.R;
import com.example.argus.ui.main.settings.bluetooth.client.FragmentClient;

public class BluetoothServerDialogFragment extends DialogFragment {

    private View view;
    private FragmentServer fragmentServer;

    public BluetoothServerDialogFragment() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_server, null);
        builder.setPositiveButton(R.string.start_server,  new DialogInterface.OnClickListener() {
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
        // Display client fragment
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentServer = FragmentServer.newInstance();
        if (fragmentServer != null)
            fragmentTransaction.add(R.id.frame_layout, fragmentServer).addToBackStack(null).commit();
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
                        fragmentServer.startServer();
                        // dismiss();
                    } catch (Exception e) {
                        Log.w("APPLY", "exception: " + e.getMessage() );
                    }
                }
            });
        }
    }
}

