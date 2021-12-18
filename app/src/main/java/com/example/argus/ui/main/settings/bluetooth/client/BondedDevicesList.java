package com.example.argus.ui.main.settings.bluetooth.client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.widget.ListView;

import com.example.argus.ui.main.settings.bluetooth.common.BluetoothDeviceListAdapter;

import java.util.ArrayList;

public class BondedDevicesList {
    private final BluetoothDeviceListAdapter bondedDevicesAdapter;

    public ArrayList<BluetoothDevice> getBondedDevices() {
        return bondedDevices;
    }

    private ArrayList<BluetoothDevice> bondedDevices;

    public BondedDevicesList(Context context, ListView list) {
        // Init
        bondedDevices = new ArrayList<BluetoothDevice>();
        // Bonded devices list
        bondedDevicesAdapter = new BluetoothDeviceListAdapter(bondedDevices, context);
        list.setAdapter(bondedDevicesAdapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    public void fetchBondedDevices() {
        bondedDevices.addAll(BluetoothAdapter.getDefaultAdapter().getBondedDevices());
        bondedDevicesAdapter.notifyDataSetChanged();
    }

    public void clear() {
        bondedDevices.clear();
        bondedDevicesAdapter.notifyDataSetChanged();
    }
}
