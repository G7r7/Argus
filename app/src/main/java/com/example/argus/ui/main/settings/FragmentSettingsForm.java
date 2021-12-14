package com.example.argus.ui.main.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.argus.MainActivity;
import com.example.argus.R;
import com.example.argus.databinding.FragmentSettingsFormBinding;
import com.example.argus.ui.main.PageViewModel;
import com.example.argus.ui.main.settings.client.FragmentClient;
import com.example.argus.ui.main.settings.server.FragmentServer;

import java.util.HashMap;


/**
 * A Settings form fragment to change settings
 */
public class FragmentSettingsForm extends Fragment {

    private PageViewModel pageViewModel;
    private FragmentSettingsFormBinding binding;
    private int settingsWidthPx = 0;
    private int settingsHeightPx = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.settingsWidthPx = ((MainActivity) getActivity()).settings.getWidthPx();
        this.settingsHeightPx = ((MainActivity) getActivity()).settings.getHeightPx();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentSettingsFormBinding.inflate(inflater, container, false);
        binding.editTextNumber.setText(String.valueOf(this.settingsWidthPx));
        binding.editTextNumber2.setText(String.valueOf(this.settingsHeightPx));
        binding.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(binding.editTextNumber.getText().toString().isEmpty()
                            || binding.editTextNumber2.getText().toString().isEmpty()) {
                       throw new Exception(getString(R.string.tab_settings_no_empty_error));
                    }
                    binding.textViewError.setVisibility(View.GONE);
                    int widthPx = Integer.parseInt(binding.editTextNumber.getText().toString());
                    int heightPx = Integer.parseInt(binding.editTextNumber2.getText().toString());
                    MainActivity myActivity = (MainActivity) getActivity();
                    HashMap<String, Object> settings = myActivity.settings.getSettings();
                    settings.put("widthPx", widthPx);
                    settings.put("heightPx", heightPx);
                    myActivity.settings.updateSettings(settings);
                    binding.textViewSuccess.setVisibility(View.VISIBLE);
                    AlphaAnimation fadeOut = new AlphaAnimation(1.0f , 0.0f );
                    binding.textViewSuccess.startAnimation(fadeOut);
                    fadeOut.setDuration(1000);
                    fadeOut.setFillAfter(true);
                    fadeOut.setStartOffset(2000);
                } catch (Exception e) {
                    Log.w("APPLY", "exception: " + e.getMessage() );
                    binding.textViewSuccess.setVisibility(View.GONE);
                    binding.textViewError.setText(e.getMessage());
                    binding.textViewError.setVisibility(View.VISIBLE);
                }
            }
        });
        displayFragment("client");
        binding.bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == false)
                    displayFragment("client");
                else if (b == true)
                    displayFragment("server");
            }
        });
        View root = binding.getRoot();
        return root;
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
            fragmentTransaction.add(binding.frameLayout.getId(), newFragment).addToBackStack(null).commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}