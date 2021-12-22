package com.example.argus.ui.main.settings.bluetooth.client;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Magnifier;

import com.example.argus.MainActivity;
import com.example.argus.backend.client.BluetoothClientThread;
import com.example.argus.backend.common.BluetoothStateChangeReceiver;
import com.example.argus.databinding.FragmentClientBinding;
import com.example.argus.ui.main.settings.bluetooth.common.BluetoothDeviceListAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
        // Callback de changement d'état du bluetooth
        bluetoothStateChangeReceiver = new BluetoothStateChangeReceiver(getContext());
        // Lists
        bondedDevicesList = new BondedDevicesList(getContext(), binding.bondedDeviceNamesList);
        discoveredDevicesList = new DiscoveredDevicesList(getContext(), binding.detectedDeviceNamesList);
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
        return binding.getRoot();
    }

    public void connectToServer() {
        if (selectedDevice == null) {
            Snackbar.make(getView(), "Veuillez sélectionner un appareil", Snackbar.LENGTH_LONG).show();
            return;
        }

        if(((MainActivity) getActivity()).settings.getClientThread() != null)
            try {
                ((MainActivity) getActivity()).settings.getClientThread().cancel();
            } catch (Exception e) {
                Snackbar.make(getView(), "Erreur arrêt thread client", Snackbar.LENGTH_LONG).show();
                Log.e(TAG, "connectToServer: ", e);
                return;
            }

        try {
            HashMap<String, Object> settings = ((MainActivity) getActivity()).settings.getSettings();
            clientThread = new BluetoothClientThread(clientHandler, selectedDevice, BluetoothAdapter.getDefaultAdapter(), baseUUID);
            settings.put("clientThread", clientThread);
            ((MainActivity)getActivity()).settings.updateSettings(settings);
        } catch (Exception e) {
            Snackbar.make(getView(), "Erreur création thread client", Snackbar.LENGTH_LONG).show();
            Log.e(TAG, "connectToServer: ", e);
            return;
        }

        try {
            ((MainActivity)getActivity()).settings.getClientThread().start();
            Snackbar.make(getView(),
                    "Connexion à \"" + selectedDevice.getName() + "\" " + selectedDevice.getAddress(),
                    Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            Snackbar.make(getView(), "Erreur démarrage thread client", Snackbar.LENGTH_LONG).show();
            Log.e(TAG, "connectToServer: ", e);
            return;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        // TODO: Use the ViewModel
    }
}