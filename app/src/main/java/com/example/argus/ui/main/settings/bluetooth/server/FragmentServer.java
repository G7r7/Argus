package com.example.argus.ui.main.settings.bluetooth.server;

import androidx.lifecycle.ViewModelProvider;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.argus.backend.server.BluetoothServerThread;
import com.example.argus.databinding.FragmentServerBinding;
import com.google.android.material.snackbar.Snackbar;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FragmentServer extends Fragment {

    private ServerViewModel mViewModel;
    private FragmentServerBinding binding;
    private BluetoothServerThread serverThread = null;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout
        binding = FragmentServerBinding.inflate(inflater);
        // return root
        return binding.getRoot();
    }

    public void stopServer() {
        serverThread.cancel();
        Snackbar.make(getView(), "Serveur arrêté", Snackbar.LENGTH_LONG).show();
        binding.log.setText("");
        binding.log.setVisibility(View.GONE);
    }

    public void startServer() {
        serverThread = new BluetoothServerThread(serverHandler, BluetoothAdapter.getDefaultAdapter(), baseUUID);
        serverThread.start();
        Snackbar.make(getView(), "Serveur démarré", Snackbar.LENGTH_LONG).show();
        binding.log.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ServerViewModel.class);
        // TODO: Use the ViewModel
    }

}