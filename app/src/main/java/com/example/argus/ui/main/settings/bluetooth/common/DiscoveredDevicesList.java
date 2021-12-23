package com.example.argus.ui.main.settings.bluetooth.common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argus.R;
import com.example.argus.backend.common.BluetoothDeviceFoundReceiver;
import com.example.argus.ui.main.settings.bluetooth.common.BluetoothDeviceListAdapter;

import java.util.ArrayList;

public class DiscoveredDevicesList {
    public BluetoothDeviceListAdapter getDiscoveredDevicesAdapter() {
        return discoveredDevicesAdapter;
    }
    private final BluetoothDeviceListAdapter discoveredDevicesAdapter;
    private final BluetoothDeviceFoundReceiver bluetoothDeviceFoundReceiver;
    private final Context context;

    public ArrayList<BluetoothDevice> getDiscoveredDevices() {
        return discoveredDevices;
    }

    public ArrayList<BluetoothDevice> discoveredDevices;

    public DiscoveredDevicesList(Context context, RecyclerView list) {
        // init
        this.context = context;
        this.discoveredDevices = new ArrayList<BluetoothDevice>();
        bluetoothDeviceFoundReceiver = new BluetoothDeviceFoundReceiver(context, this);
        discoveredDevicesAdapter = new BluetoothDeviceListAdapter(discoveredDevices);
        list.setAdapter(discoveredDevicesAdapter);
        list.setLayoutManager(new LinearLayoutManager(context));
    }

    public void discoverDevices() {
        if (BluetoothAdapter.getDefaultAdapter().startDiscovery()) {
            Toast.makeText(context, R.string.discovery_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.discovery_failed, Toast.LENGTH_SHORT).show();
        }
    }

    public void addDevice(BluetoothDevice device) {
        discoveredDevices.add(device);
        discoveredDevicesAdapter.notifyDataSetChanged();
    }

    public void clear() {
        discoveredDevices.clear();
        discoveredDevicesAdapter.notifyDataSetChanged();
    }
}
