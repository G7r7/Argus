package com.example.argus.ui.main.settings.bluetooth.client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.argus.MainActivity;
import com.example.argus.MainActivityViewModel;
import com.example.argus.R;
import com.example.argus.backend.client.BluetoothClientThread;
import com.example.argus.ui.main.settings.bluetooth.client.FragmentClient;
import com.example.argus.ui.main.settings.bluetooth.server.FragmentServer;
import com.google.android.material.snackbar.Snackbar;

import java.nio.channels.AlreadyBoundException;
import java.util.HashMap;

public class BluetoothConnexionDialogFragment extends DialogFragment {

    private View view;
    private FragmentClient fragmentClient;
    private MainActivityViewModel model;
    private AlertDialog d;

    public BluetoothConnexionDialogFragment() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.model = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_bluetooth, null);
        builder.setPositiveButton(R.string.connect_to_server,  new DialogInterface.OnClickListener() {
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
        fragmentClient = FragmentClient.newInstance();
        if (fragmentClient != null)
            fragmentTransaction.add(R.id.frame_layout, fragmentClient).addToBackStack(null).commit();
        // Dialog buttons
        this.d = (AlertDialog)getDialog();
        this.updateButton();
        this.model.getClientThreadStatus().observe(this, threadStatus -> { this.updateButton(); });
        this.model.getIsClientThreadConnected().observe(this, threadStatus -> { this.updateButton(); });
    }

    private void updateButton() {
        if(this.model.getClientThreadStatus().getValue() == null) {
            if(this.d != null) {
                Button positiveButton = (Button) this.d.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setText(R.string.connect_to_server);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            fragmentClient.connectToServer();
                            // dismiss();
                        } catch (Exception e) {
                            Log.w("APPLY", "exception: " + e.getMessage() );
                        }
                    }
                });
            }
        }
        else if(this.model.getClientThreadStatus().getValue() == Thread.State.NEW){
            if(this.d != null) {
                Button positiveButton = (Button) this.d.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setText(R.string.abort_connexion);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            model.getClientThread().getValue().cancel();
                            // dismiss();
                        } catch (Exception e) {
                            Log.w("APPLY", "exception: " + e.getMessage() );
                        }
                    }
                });
            }
        }
        else if(this.model.getClientThreadStatus().getValue() == Thread.State.RUNNABLE) {
            if(this.d != null) {
                Button positiveButton = (Button) this.d.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setText(R.string.abort_connexion);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            model.getClientThread().getValue().cancel();
                            // dismiss();
                        } catch (Exception e) {
                            Log.w("APPLY", "exception: " + e.getMessage() );
                        }
                    }
                });
            }
            if(this.model.getIsClientThreadConnected().getValue()){
                if(this.d != null) {
                    Button positiveButton = (Button) this.d.getButton(Dialog.BUTTON_POSITIVE);
                    positiveButton.setText(R.string.disconnect);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                model.getClientThread().getValue().cancel();
                                // dismiss();
                            } catch (Exception e) {
                                Log.w("APPLY", "exception: " + e.getMessage() );
                            }
                        }
                    });
                }
            }
        }
    }
}

