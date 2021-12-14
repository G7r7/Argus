package com.example.argus.backend.common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.example.argus.R;
import com.example.argus.ui.main.settings.client.DiscoveredDevicesList;

import java.util.ArrayList;

public class BluetoothDeviceFoundReceiver extends BroadcastReceiver {
    private Context context;
    private DiscoveredDevicesList list;

    public BluetoothDeviceFoundReceiver(Context context, DiscoveredDevicesList list) {
        this.context = context;
        this.list = list;
        register();
    }

    private void register() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.context.registerReceiver(this,filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        // Callback de détection d'un appareil bluetooth
        if (action.equals(BluetoothDevice.ACTION_FOUND)) {
            // Discovery has found a device. Get the BluetoothDevice
            // object and its info from the Intent.
            Toast.makeText(context, R.string.device_detected, Toast.LENGTH_SHORT).show();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            list.addDevice(device);
        }
    }
}
