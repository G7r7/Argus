package com.example.argus.ui.main.settings.bluetooth.client;

import androidx.lifecycle.ViewModelProvider;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.argus.MainActivityViewModel;
import com.example.argus.backend.client.BluetoothClientThread;
import com.example.argus.backend.common.BluetoothStateChangeReceiver;
import com.example.argus.databinding.FragmentClientBinding;
import com.example.argus.ui.main.settings.bluetooth.common.BluetoothDeviceListAdapter;
import com.example.argus.ui.main.settings.bluetooth.common.BondedDevicesList;
import com.example.argus.ui.main.settings.bluetooth.common.DiscoveredDevicesList;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.UUID;

public class FragmentClient extends Fragment {

    private static final String TAG = "LOG";
    private FragmentClientBinding binding;
    private MainActivityViewModel mainModel;

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

    public static FragmentClient newInstance() {
        return new FragmentClient();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.mainModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        // Loading layout
        binding = FragmentClientBinding.inflate(inflater);
        // Update View
        this.updateView();
        // Logic
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Callback de changement d'??tat du bluetooth
        bluetoothStateChangeReceiver = new BluetoothStateChangeReceiver(getContext());
        // Lists
        bondedDevicesList = new BondedDevicesList(getContext(), binding.bondedDeviceNamesList);
        bondedDevicesList.getBondedDevicesAdapter().setOnItemClickListener(new BluetoothDeviceListAdapter.OnItemClicked() {
            @Override
            public void onItemClick(int position) {
                selectedDevice = bondedDevicesList.getBondedDevices().get(position);
            }
        });
        discoveredDevicesList = new DiscoveredDevicesList(getContext(), binding.detectedDeviceNamesList);
        discoveredDevicesList.getDiscoveredDevicesAdapter().setOnItemClickListener(new BluetoothDeviceListAdapter.OnItemClicked() {
            @Override
            public void onItemClick(int position) {
                selectedDevice = discoveredDevicesList.getDiscoveredDevices().get(position);
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

        this.mainModel.getClientThreadStatus().observe(getViewLifecycleOwner(), threadStatus -> { this.updateView(); });
        this.mainModel.getIsClientThreadConnected().observe(getViewLifecycleOwner(), threadStatus -> { this.updateView(); });

        return binding.getRoot();
    }

    private void updateView() {
        if(this.mainModel.getClientThreadStatus().getValue() == null) {
            binding.connexion.setVisibility(View.VISIBLE);
            binding.loading.setVisibility(View.GONE);
            binding.connected.setVisibility(View.GONE);
        }
        else if(this.mainModel.getClientThreadStatus().getValue() == Thread.State.NEW){
            binding.connexion.setVisibility(View.GONE);
            binding.loading.setVisibility(View.VISIBLE);
            binding.connected.setVisibility(View.GONE);
        }
        else if(this.mainModel.getClientThreadStatus().getValue() == Thread.State.RUNNABLE) {
            {
            binding.connexion.setVisibility(View.GONE);
            binding.loading.setVisibility(View.VISIBLE);
            binding.connected.setVisibility(View.GONE);
        }
            if(this.mainModel.getIsClientThreadConnected().getValue()){
                binding.connexion.setVisibility(View.GONE);
                binding.loading.setVisibility(View.GONE);
                binding.connected.setVisibility(View.VISIBLE);
            }
        }
    }

    public void connectToServer() {
        if (selectedDevice == null) {
            Snackbar.make(getView(), "Veuillez s??lectionner un appareil", Snackbar.LENGTH_LONG).show();
            return;
        }

        if(this.mainModel.getClientThread().getValue() != null)
            try {
                this.mainModel.getClientThread().getValue().cancel();
            } catch (Exception e) {
                Snackbar.make(getView(), "Erreur arr??t thread client", Snackbar.LENGTH_LONG).show();
                Log.e(TAG, "connectToServer: ", e);
                return;
            }

        try {
            mainModel.setClientThread(
                    new BluetoothClientThread(requireActivity(), selectedDevice, BluetoothAdapter.getDefaultAdapter(), baseUUID));
        } catch (Exception e) {
            Snackbar.make(getView(), "Erreur cr??ation thread client", Snackbar.LENGTH_LONG).show();
            Log.e(TAG, "connectToServer: ", e);
            return;
        }

        try {
            this.mainModel.getClientThread().getValue().start();
            Snackbar.make(getView(),
                    "Connexion ?? \"" + selectedDevice.getName() + "\" " + selectedDevice.getAddress(),
                    Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            Snackbar.make(getView(), "Erreur d??marrage thread client", Snackbar.LENGTH_LONG).show();
            Log.e(TAG, "connectToServer: ", e);
            return;
        }
    }
}