package com.example.argus.ui.main.settings.bluetooth.server;

import androidx.lifecycle.ViewModelProvider;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.argus.MainActivity;
import com.example.argus.backend.client.BluetoothClientThread;
import com.example.argus.backend.server.BluetoothServerThread;
import com.example.argus.databinding.FragmentServerBinding;
import com.google.android.material.snackbar.Snackbar;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

public class FragmentServer extends Fragment {

    private static final String TAG = "TAG";
    private ServerViewModel mViewModel;
    private FragmentServerBinding binding;
    UUID baseUUID = UUID.fromString("00000000-0000-1000-7007-00805F9B34FB");

    private Handler serverHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String text = new String((byte[])msg.obj, StandardCharsets.UTF_8);
            Snackbar.make(getView(), text, Snackbar.LENGTH_LONG).show();
            // Si on reçoit quelque chose on l'ajoute au log
            binding.log.append(text + "\n");
        }
    };

    public static FragmentServer newInstance() {
        return new FragmentServer();
    }

    private void updateServerStatus() {
        if(((MainActivity)getActivity()).settings.getServerThread() == null)
            binding.status.setText("Serveur arrêté");
        else {
            if (((MainActivity)getActivity()).settings.getServerThread().getState() == Thread.State.TERMINATED)
                binding.status.setText("Serveur arrêté");
            if (((MainActivity)getActivity()).settings.getServerThread().getState() == Thread.State.NEW)
                binding.status.setText("Serveur crée");
            if (((MainActivity)getActivity()).settings.getServerThread().getState() == Thread.State.RUNNABLE) {
                binding.status.setText("Serveur démarré");
                binding.log.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout
        binding = FragmentServerBinding.inflate(inflater);
        // Setting view corresponding to current thread state
        updateServerStatus();
        // return root
        return binding.getRoot();
    }

    public void stopServer() {
        if(((MainActivity) getActivity()).settings.getServerThread() != null)
            try {
                ((MainActivity) getActivity()).settings.getServerThread().cancel();
            } catch (Exception e) {
                Snackbar.make(getView(), "Erreur arrêt thread serveur", Snackbar.LENGTH_LONG).show();
                Log.e(TAG, "startServer: ", e);
                return;
            }
        Snackbar.make(getView(), "Serveur arrêté", Snackbar.LENGTH_LONG).show();
        binding.log.setText("");
        binding.log.setVisibility(View.GONE);
        updateServerStatus();
    }

    public void startServer() {
        if(((MainActivity) getActivity()).settings.getServerThread() != null)
            try {
                ((MainActivity) getActivity()).settings.getServerThread().cancel();
            } catch (Exception e) {
                Snackbar.make(getView(), "Erreur arrêt thread serveur", Snackbar.LENGTH_LONG).show();
                Log.e(TAG, "startServer: ", e);
                return;
            }

        try {
            HashMap<String, Object> settings = ((MainActivity) getActivity()).settings.getSettings();
            BluetoothServerThread serverThread = new BluetoothServerThread(serverHandler, BluetoothAdapter.getDefaultAdapter(), baseUUID);
            settings.put("serverThread", serverThread);
            ((MainActivity)getActivity()).settings.updateSettings(settings);
        } catch (Exception e) {
            Snackbar.make(getView(), "Erreur création thread serveur", Snackbar.LENGTH_LONG).show();
            Log.e(TAG, "startServer: ", e);
            return;
        }

        try {
            ((MainActivity)getActivity()).settings.getServerThread().start();
            Snackbar.make(getView(),
                    "Démarrage du thread serveur",
                    Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            Snackbar.make(getView(), "Erreur démarrage thread serveur", Snackbar.LENGTH_LONG).show();
            Log.e(TAG, "connectToServer: ", e);
            return;
        }

        Snackbar.make(getView(), "Serveur démarré", Snackbar.LENGTH_LONG).show();
        binding.log.setVisibility(View.VISIBLE);
        updateServerStatus();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ServerViewModel.class);
        // TODO: Use the ViewModel
    }

}