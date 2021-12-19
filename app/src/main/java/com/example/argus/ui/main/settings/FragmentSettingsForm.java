package com.example.argus.ui.main.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.argus.MainActivity;
import com.example.argus.databinding.FragmentSettingsFormBinding;
import com.example.argus.ui.main.PageViewModel;
import com.example.argus.ui.main.settings.bluetooth.BluetoothConnexionDialogFragment;
import com.example.argus.ui.main.settings.resolution.ResolutionDialogFragment;


/**
 * A Settings form fragment to change settings
 */
public class FragmentSettingsForm extends Fragment implements DialogInterface.OnDismissListener{

    private PageViewModel pageViewModel;
    private FragmentSettingsFormBinding binding;
    private ResolutionDialogFragment resolutionModal;
    private BluetoothConnexionDialogFragment connexionModal;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        resolutionModal = new ResolutionDialogFragment((MainActivity) getActivity());
        connexionModal = new BluetoothConnexionDialogFragment((MainActivity) getActivity());
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentSettingsFormBinding.inflate(inflater, container, false);
        int settingsWidthPx = ((MainActivity) getActivity()).settings.getWidthPx();
        int settingsHeightPx = ((MainActivity) getActivity()).settings.getHeightPx();
        String resolutionPreview = String.valueOf(settingsWidthPx) + " * " + String.valueOf(settingsHeightPx);
        binding.resolutionPreview.setText(resolutionPreview);
        binding.openResolutionModal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolutionModal.show(getChildFragmentManager(), "Résolution");
            }
        });
        binding.openConnexionModal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connexionModal.show(getChildFragmentManager(), "Connexion");
            }
        });
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        int settingsWidthPx = ((MainActivity) getActivity()).settings.getWidthPx();
        int settingsHeightPx = ((MainActivity) getActivity()).settings.getHeightPx();
        String resolutionPreview = String.valueOf(settingsWidthPx) + " * " + String.valueOf(settingsHeightPx);
        binding.resolutionPreview.setText(resolutionPreview);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}