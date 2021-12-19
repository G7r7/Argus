package com.example.argus.ui.main.settings.bluetooth;

import android.app.AlertDialog;
import android.app.Dialog;
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

import com.example.argus.MainActivity;
import com.example.argus.R;
import com.example.argus.ui.main.settings.bluetooth.client.FragmentClient;
import com.example.argus.ui.main.settings.bluetooth.server.FragmentServer;

import java.util.HashMap;

public class BluetoothConnexionDialogFragment extends DialogFragment {

    private MainActivity mainActivity;
    private View view;

    public BluetoothConnexionDialogFragment(MainActivity mainActivity) {
        super();
        this.mainActivity = mainActivity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_bluetooth, null);
        builder.setPositiveButton(R.string.tab_settings_update,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing here because we override this button later to change the close behaviour.
            }
        });
        Switch switchButton = view.findViewById(R.id.bluetooth_switch);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == false)
                    displayFragment("client");
                else if (b == true)
                    displayFragment("server");
            }
        });
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
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
                        dismiss();
                    } catch (Exception e) {
                        Log.w("APPLY", "exception: " + e.getMessage() );
                    }
                }
            });
        }
        displayFragment("client");
    }

    public void displayFragment(String type) {
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            getChildFragmentManager().beginTransaction().remove(fragment).commit();
        }
        Fragment newFragment = null;
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (type == "client") {
            newFragment = FragmentClient.newInstance();
        } else if (type == "server") {
            newFragment = FragmentServer.newInstance();
        }
        if (newFragment != null)
            fragmentTransaction.add(R.id.frame_layout, newFragment).addToBackStack(null).commit();
    }
}

