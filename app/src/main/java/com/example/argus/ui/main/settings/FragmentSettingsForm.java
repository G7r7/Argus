package com.example.argus.ui.main.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
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
import com.example.argus.ui.main.settings.bluetooth.client.FragmentClient;
import com.example.argus.ui.main.settings.bluetooth.server.FragmentServer;
import com.example.argus.ui.main.settings.resolution.SetResolutionDialogFragment;

import java.util.HashMap;


/**
 * A Settings form fragment to change settings
 */
public class FragmentSettingsForm extends Fragment {

    private PageViewModel pageViewModel;
    private FragmentSettingsFormBinding binding;
    private SetResolutionDialogFragment resolutionModal;
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
        resolutionModal = new SetResolutionDialogFragment((MainActivity) getActivity());
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentSettingsFormBinding.inflate(inflater, container, false);
        binding.resolutionModal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolutionModal.show(getChildFragmentManager(), "Résolution");
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