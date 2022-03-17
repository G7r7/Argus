package com.example.argus.ui.main.settings;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.argus.MainActivityViewModel;
import com.example.argus.databinding.FragmentSettingsFormBinding;
import com.example.argus.ui.main.PageViewModel;
import com.example.argus.ui.main.settings.bluetooth.client.BluetoothConnexionDialogFragment;
import com.example.argus.ui.main.settings.bluetooth.rawtext.BluetoothRawTextDialogFragment;
import com.example.argus.ui.main.settings.bluetooth.server.BluetoothServerDialogFragment;
import com.example.argus.ui.main.settings.colorbits.ColorBitsDialogFragment;
import com.example.argus.ui.main.settings.resolution.ResolutionDialogFragment;


/**
 * A Settings form fragment to change settings
 */
public class FragmentSettingsForm extends Fragment {

    private PageViewModel pageViewModel;
    private FragmentSettingsFormBinding binding;
    private ResolutionDialogFragment resolutionModal;
    private ColorBitsDialogFragment colorBitsModal;
    private BluetoothConnexionDialogFragment connexionModal;
    private BluetoothServerDialogFragment serverModal;
    private BluetoothRawTextDialogFragment rawTextModal;
    private MainActivityViewModel mainModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        resolutionModal = new ResolutionDialogFragment();
        colorBitsModal = new ColorBitsDialogFragment();
        connexionModal = new BluetoothConnexionDialogFragment();
        serverModal = new BluetoothServerDialogFragment();
        rawTextModal = new BluetoothRawTextDialogFragment();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        this.mainModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        this.mainModel.getWidthPx().observe(getViewLifecycleOwner(), settings -> { this.updatePreviews(); });
        this.mainModel.getHeightPx().observe(getViewLifecycleOwner(), settings -> { this.updatePreviews(); });
        this.mainModel.getClientThreadStatus().observe(getViewLifecycleOwner(), settings -> { this.updatePreviews(); });
        this.mainModel.getIsClientThreadConnected().observe(getViewLifecycleOwner(), settings -> { this.updatePreviews(); });
        this.mainModel.getServerThread().observe(getViewLifecycleOwner(), settings -> { this.updatePreviews(); });
        this.mainModel.getServerThreadStatus().observe(getViewLifecycleOwner(), settings -> { this.updatePreviews(); });
        this.mainModel.getBitsPerColor().observe(getViewLifecycleOwner(), settings -> { this.updatePreviews(); });


        binding = FragmentSettingsFormBinding.inflate(inflater, container, false);
        binding.openResolutionModal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolutionModal.show(getChildFragmentManager(), "Résolution");
            }
        });
        binding.openColorBitsModal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorBitsModal.show(getChildFragmentManager(), "ColorBits");
            }
        });
        binding.openConnexionModal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connexionModal.show(getChildFragmentManager(), "Connexion");
            }
        });
        binding.openServerModal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverModal.show(getChildFragmentManager(), "Serveur (DEBUG)");
            }
        });
        binding.openRawTextModal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rawTextModal.show(getChildFragmentManager(), "Raw Text (DEBUG)");
            }
        });
        View root = binding.getRoot();
        return root;
    }

    private void updatePreviews() {
        // Resolution preview
        int settingsWidthPx = this.mainModel.getWidthPx().getValue();
        int settingsHeightPx = this.mainModel.getHeightPx().getValue();
        String resolutionPreview = String.valueOf(settingsWidthPx) + " * " + String.valueOf(settingsHeightPx);
        binding.resolutionPreview.setText(resolutionPreview);

        // Bits per Color preview
        int bitsPerColor = this.mainModel.getBitsPerColor().getValue();
        binding.colorBitsPreview.setText(String.valueOf(bitsPerColor));

        // Connexion setting
        BluetoothDevice server = this.mainModel.getServerDevice().getValue();
        if (server != null) {
            binding.connexionPreview.setText(server.getName() + " " + server.getAddress());
        } else {
            binding.connexionPreview.setText("Aucun serveur");
        }

        // Server Preview
        if(this.mainModel.getServerThread().getValue() == null)
            binding.serverPreview.setText("Aucun thread Serveur");
        else if(this.mainModel.getServerThread().getValue().getState() == Thread.State.NEW)
            binding.serverPreview.setText("Thread serveur crée");
        else if(this.mainModel.getServerThread().getValue().getState() == Thread.State.RUNNABLE)
            binding.serverPreview.setText("Thread serveur démarré");
        else if(this.mainModel.getServerThread().getValue().getState() == Thread.State.TERMINATED)
            binding.serverPreview.setText("Serveur arrêté");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}