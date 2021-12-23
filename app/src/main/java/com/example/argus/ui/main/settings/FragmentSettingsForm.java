package com.example.argus.ui.main.settings;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.argus.MainActivity;
import com.example.argus.databinding.FragmentSettingsFormBinding;
import com.example.argus.ui.main.PageViewModel;
import com.example.argus.ui.main.settings.bluetooth.client.BluetoothConnexionDialogFragment;
import com.example.argus.ui.main.settings.bluetooth.rawtext.BluetoothRawTextDialogFragment;
import com.example.argus.ui.main.settings.bluetooth.server.BluetoothServerDialogFragment;
import com.example.argus.ui.main.settings.resolution.ResolutionDialogFragment;


/**
 * A Settings form fragment to change settings
 */
public class FragmentSettingsForm extends Fragment implements DialogInterface.OnDismissListener{

    private PageViewModel pageViewModel;
    private FragmentSettingsFormBinding binding;
    private ResolutionDialogFragment resolutionModal;
    private BluetoothConnexionDialogFragment connexionModal;
    private BluetoothServerDialogFragment serverModal;
    private BluetoothRawTextDialogFragment rawTextModal;

    private Handler mHandler = new Handler();
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateSettingsPreviews(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, 300);
            }
        }
    };

    void updateSettingsPreviews() {
        // Resolution preview
        // Done in "OnDismiss"
        // Client preview
        if(((MainActivity)getActivity()).settings.getClientThread() == null)
            binding.connexionPreview.setText("Aucun thread client");
        else if(((MainActivity)getActivity()).settings.getClientThread().getState() == Thread.State.NEW)
            binding.connexionPreview.setText("Thread client crée");
        else if(((MainActivity)getActivity()).settings.getClientThread().getState() == Thread.State.RUNNABLE) {
            binding.connexionPreview.setText("Thread client démarré");
            boolean status = false;
            if(status = ((MainActivity)getActivity()).settings.getClientThread().getMmSocket().isConnected())
                binding.connexionPreview.setText("Socket client connecté");
            else
                binding.connexionPreview.setText("Socket client déconnecté");
        }
        // Server Preview
        if(((MainActivity)getActivity()).settings.getServerThread() == null)
            binding.serverPreview.setText("Aucun thread Serveur");
        else if(((MainActivity)getActivity()).settings.getServerThread().getState() == Thread.State.NEW)
            binding.serverPreview.setText("Thread serveur crée");
        else if(((MainActivity)getActivity()).settings.getServerThread().getState() == Thread.State.RUNNABLE) {
            binding.serverPreview.setText("Thread serveur démarré");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        resolutionModal = new ResolutionDialogFragment();
        connexionModal = new BluetoothConnexionDialogFragment();
        serverModal = new BluetoothServerDialogFragment();
        rawTextModal = new BluetoothRawTextDialogFragment();
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
        BluetoothDevice server = ((MainActivity) getActivity()).settings.getServer();
        if (server != null) {
            binding.connexionPreview.setText(server.getName() + " " + server.getAddress());
        } else {
            binding.connexionPreview.setText("Aucun serveur");
        }
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
        mStatusChecker.run();
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