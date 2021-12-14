package com.example.argus.backend.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.argus.backend.common.MessageConstants;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import android.os.Handler;

public class BluetoothServerThread extends Thread {
    private static final String TAG = "TAG";
    private final BluetoothServerSocket mmServerSocket;
    private Handler handler;
    private BluetoothServerConnectedThread connectedThread = null;

    public BluetoothServerThread(Handler serverHandler, BluetoothAdapter bluetoothAdapter, UUID uuid) {
        handler = serverHandler;
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("Argus server", uuid);
        } catch (IOException e) {
            Log.e(TAG, "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                String msg = "En attente de conexion ...";
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, msg.getBytes(StandardCharsets.UTF_8));
                writtenMsg.sendToTarget();
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                try {
                    connectedThread = new BluetoothServerConnectedThread(handler, socket);
                    connectedThread.start();
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
            if (connectedThread != null)
                connectedThread.cancel();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}
