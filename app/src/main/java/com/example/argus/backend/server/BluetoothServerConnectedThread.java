package com.example.argus.backend.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.argus.backend.client.BluetoothClientThread;
import com.example.argus.backend.common.MessageConstants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BluetoothServerConnectedThread extends Thread {
    private static final String TAG = "TAG";
    private final Handler handler;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private BluetoothSocket mmSocket;
    private byte[] mmBuffer;

    public BluetoothServerConnectedThread(Handler handler, BluetoothSocket socket) {
        this.handler = handler;
        this.mmSocket = socket;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

        String msg = "Connexion établie avec " + mmSocket.getRemoteDevice().getAddress();
        Message writtenMsg = handler.obtainMessage(
                MessageConstants.MESSAGE_WRITE, -1, -1, msg.getBytes(StandardCharsets.UTF_8));
        writtenMsg.sendToTarget();
    }

    public void run() {
        mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                // Read from the InputStream.
                numBytes = mmInStream.read(mmBuffer);
                // Send the obtained bytes to the UI activity.
                Message readMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_READ, numBytes, -1,
                        mmBuffer);
                readMsg.sendToTarget();
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                break;
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}
