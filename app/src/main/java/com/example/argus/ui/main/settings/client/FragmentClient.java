package com.example.argus.ui.main.settings.client;

import androidx.lifecycle.ViewModelProvider;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.argus.backend.client.BluetoothClientThread;
import com.example.argus.backend.common.BluetoothStateChangeReceiver;
import com.example.argus.databinding.FragmentClientBinding;
import com.example.argus.ui.main.settings.common.BluetoothDeviceListAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

public class FragmentClient extends Fragment {

    private static final String TAG = "LOG";
    private ClientViewModel mViewModel;
    private FragmentClientBinding binding;

    private BluetoothAdapter bluetoothAdapter;
    ArrayList<BluetoothDevice> bondedDevices = new ArrayList<>();
    private static BluetoothDeviceListAdapter bondedDevicesAdapter;
    ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<>();
    private static BluetoothDeviceListAdapter discoveredDevicesAdapter;
    UUID baseUUID = UUID.fromString("00000000-0000-1000-7007-00805F9B34FB");
    private BluetoothStateChangeReceiver bluetoothStateChangeReceiver;
    private BondedDevicesList bondedDevicesList;
    private DiscoveredDevicesList discoveredDevicesList;
    public BluetoothDevice selectedDevice = null;
    private BluetoothClientThread clientThread;

    private Handler clientHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String text = new String((byte[])msg.obj, StandardCharsets.UTF_8);
            Snackbar.make(getView(), text, Snackbar.LENGTH_LONG).show();
            // Si on reçoit quelque chose on active le bouton
            binding.send.setEnabled(true);
        }
    };


    public static FragmentClient newInstance() {
        return new FragmentClient();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Loading layout
        binding = FragmentClientBinding.inflate(inflater);
        // Logic
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // connexion
        binding.connectToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToServer();
            }
        });
        binding.disconnectFromServer.setEnabled(false);
        binding.disconnectFromServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnectFromServer();
                binding.send.setEnabled(false);
            }
        });
        // Callback de changement d'état du bluetooth
        bluetoothStateChangeReceiver = new BluetoothStateChangeReceiver(getContext());
        // Lists
        bondedDevicesList = new BondedDevicesList(getContext(), binding.bondedDeviceNamesList);
        binding.bondedDeviceNamesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDevice = bondedDevicesList.getBondedDevices().get(position);
                Snackbar.make(view, selectedDevice.getName(), Snackbar.LENGTH_LONG).show();
                for (int i = 0; i < binding.bondedDeviceNamesList.getChildCount(); i++) {
                    if(position == i){
                        binding.bondedDeviceNamesList.getChildAt(i).setBackgroundColor(Color.GREEN);
                    }else{
                        binding.bondedDeviceNamesList.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            }
        });
        discoveredDevicesList = new DiscoveredDevicesList(getContext(), binding.detectedDeviceNamesList);
        binding.detectedDeviceNamesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDevice = discoveredDevicesList.getDiscoveredDevices().get(position);
                Snackbar.make(view, selectedDevice.getName(), Snackbar.LENGTH_LONG).show();
                for (int i = 0; i < binding.detectedDeviceNamesList.getChildCount(); i++) {
                    if(position == i){
                        binding.detectedDeviceNamesList.getChildAt(i).setBackgroundColor(Color.GREEN);
                    }else{
                        binding.detectedDeviceNamesList.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            }
        });
        // Callback de click sur le boutton
        binding.getBondedDevices.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectedDevice = null;
                bondedDevicesList.clear();
                discoveredDevicesList.clear();
                bondedDevicesList.fetchBondedDevices();
            }
        });
        // Callback discovery button
        binding.searchBluetoothDevices.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectedDevice = null;
                bondedDevicesList.clear();
                discoveredDevicesList.clear();
                discoveredDevicesList.discoverDevices();
            }
        });
        // Callback send button
        binding.send.setEnabled(false);
        binding.send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                byte[] data = String.valueOf(binding.text.getText()).getBytes(StandardCharsets.UTF_8);
                clientThread.write(data);
                binding.text.setText("");
            }
        });
        return binding.getRoot();
    }

    private void disconnectFromServer() {
        binding.disconnectFromServer.setEnabled(false);
        clientThread.cancel();
        Snackbar.make(getView(), "Connexion arrêté", Snackbar.LENGTH_LONG).show();
        binding.connectToServer.setEnabled(true);
    }

    private void connectToServer() {
        if (selectedDevice == null) {
            Snackbar.make(getView(), "Veuillez sélectionner un appareil", Snackbar.LENGTH_LONG).show();
            return;
        }
        binding.connectToServer.setEnabled(false);

        clientThread = new BluetoothClientThread(clientHandler, selectedDevice, BluetoothAdapter.getDefaultAdapter(), baseUUID);
        clientThread.start();
        Snackbar.make(getView(),
                "Connexion à \"" + selectedDevice.getName() + "\" " + selectedDevice.getAddress(),
                Snackbar.LENGTH_LONG).show();
        binding.disconnectFromServer.setEnabled(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        // TODO: Use the ViewModel
    }
}