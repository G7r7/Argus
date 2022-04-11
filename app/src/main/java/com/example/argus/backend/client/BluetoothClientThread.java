package com.example.argus.backend.client;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.argus.MainActivity;
import com.example.argus.MainActivityViewModel;
import com.example.argus.backend.common.MessageConstants;
import com.example.argus.ui.main.settings.bluetooth.client.FragmentClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BluetoothClientThread extends Thread {
    private static final String TAG = "TAG";
    private BluetoothAdapter bluetoothAdapter;

    public BluetoothSocket getMmSocket() {
        return mmSocket;
    }

    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private byte[] mmBuffer; // mmBuffer store for the stream
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private MainActivityViewModel model;

    public BluetoothClientThread(FragmentActivity activity, BluetoothDevice device, BluetoothAdapter bluetoothAdapter, UUID uuid) {
        this.model = new ViewModelProvider(activity).get(MainActivityViewModel.class);
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        this.bluetoothAdapter = bluetoothAdapter;
        BluetoothSocket tmp = null;
        mmDevice = device;
        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            UUID moduleUuid = device.getUuids()[0].getUuid();
            Log.e(TAG, "BluetoothClientThread: " + moduleUuid.toString());
            tmp = device.createRfcommSocketToServiceRecord(moduleUuid);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
        model.setClientThreadStatus(this.getState());
        model.setIsClientThreadConnected(this.mmSocket.isConnected());
    }

    public void run() {
        mmBuffer = new byte[1024];
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = mmSocket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

//        String text = "Connexion établie !";

        model.setServerDevice(mmSocket.getRemoteDevice());
        model.setClientThreadStatus(this.getState());
        model.setIsClientThreadConnected(this.mmSocket.isConnected());
    }

    // Call this from the main activity to send data to the remote device.
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);
//            String msg = "Error occurred when sending data";
        }
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
        model.setClientThreadStatus(null);
        model.setIsClientThreadConnected(false);
        model.setClientThread(null);
        model.setServerDevice(null);
    }
}
