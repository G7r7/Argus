package com.example.argus.ui.main.settings.resolution;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.argus.MainActivity;
import com.example.argus.R;

import java.util.HashMap;

public class SetResolutionDialogFragment extends DialogFragment {

    private MainActivity mainActivity;
    private View view;

    public SetResolutionDialogFragment(MainActivity mainActivity) {
        super();
        this.mainActivity = mainActivity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_resolution, null);
        builder.setPositiveButton(R.string.tab_settings_update,  new DialogInterface.OnClickListener() {
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
        AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            EditText widthTextEdit = view.findViewById(R.id.editTextNumber);
            EditText heightTextEdit = view.findViewById(R.id.editTextNumber2);
            TextView errorTextView = view.findViewById(R.id.textViewError);
            TextView successTextView = view.findViewById(R.id.textViewSuccess);

            widthTextEdit.setText(String.valueOf(mainActivity.settings.getWidthPx()));
            heightTextEdit.setText(String.valueOf(mainActivity.settings.getHeightPx()));

            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // User clicked OK, so save the selectedItems results somewhere
                    // or return them to the component that opened the dialog
                    try {
                        if(widthTextEdit.getText().toString().isEmpty()
                                || heightTextEdit.getText().toString().isEmpty()) {
                            throw new Exception(getString(R.string.tab_settings_no_empty_error));
                        }
                        errorTextView.setVisibility(View.GONE);
                        int widthPx = Integer.parseInt(widthTextEdit.getText().toString());
                        int heightPx = Integer.parseInt(heightTextEdit.getText().toString());
                        MainActivity myActivity = (MainActivity) getActivity();
                        HashMap<String, Object> settings = myActivity.settings.getSettings();
                        settings.put("widthPx", widthPx);
                        settings.put("heightPx", heightPx);
                        myActivity.settings.updateSettings(settings);
                        successTextView.setVisibility(View.VISIBLE);
                        AlphaAnimation fadeOut = new AlphaAnimation(1.0f , 0.0f );
                        successTextView.startAnimation(fadeOut);
                        fadeOut.setDuration(1000);
                        fadeOut.setFillAfter(true);
                        fadeOut.setStartOffset(2000);
                        dismiss();
                    } catch (Exception e) {
                        Log.w("APPLY", "exception: " + e.getMessage() );
                        successTextView.setVisibility(View.GONE);
                        errorTextView.setText(e.getMessage());
                        errorTextView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}

