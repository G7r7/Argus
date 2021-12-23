package com.example.argus.backend;

import android.bluetooth.BluetoothDevice;

import com.example.argus.backend.client.BluetoothClientThread;
import com.example.argus.backend.server.BluetoothServerConnectedThread;
import com.example.argus.backend.server.BluetoothServerThread;

import java.util.ArrayList;
import java.util.HashMap;

public class Settings {

    private int widthPx = 16;
    private int heightPx = 16;
    private BluetoothDevice server = null;
    private BluetoothClientThread clientThread = null;
    private BluetoothServerThread serverThread = null;
    private BluetoothServerConnectedThread serverConnectedThread = null;

    public void updateSettings(HashMap<String, Object> parameters) throws Exception {
        int widthPx = (int)parameters.get("widthPx");
        int heightPx = (int)parameters.get("heightPx");
        if(!(widthPx > 0 && heightPx > 0))
            throw new Exception("La hauteur et la largeur doivent être supérieures à zéro");
        this.widthPx = widthPx;
        this.heightPx = heightPx;
        BluetoothDevice server = (BluetoothDevice)parameters.get("server");
        this.server = server;
        BluetoothClientThread clientThread = (BluetoothClientThread)parameters.get("clientThread");
        this.clientThread = clientThread;
        BluetoothServerThread serverThread = (BluetoothServerThread) parameters.get("serverThread");
        this.serverThread = serverThread;
        BluetoothServerConnectedThread serverConnectedThread =
                (BluetoothServerConnectedThread) parameters.get("serverConnectedThread");
        this.serverConnectedThread = serverConnectedThread;
    }

    public HashMap<String, Object> getSettings() {
        HashMap<String, Object> settings = new HashMap<String, Object>();
        settings.put("widthPx", this.getWidthPx());
        settings.put("heightPx", this.getHeightPx());
        settings.put("server", this.getServer());
        settings.put("clientThread", this.getClientThread());
        settings.put("serverThread", this.getServerThread());
        settings.put("serverConnectedThread", this.getServerConnectedThread());
        return settings;
    }

    public int getWidthPx() {
        return widthPx;
    }
    public int getHeightPx() { return heightPx; }
    public BluetoothDevice getServer() { return server; }
    public BluetoothClientThread getClientThread() { return clientThread; }
    public BluetoothServerThread getServerThread() { return serverThread; }
    public BluetoothServerConnectedThread getServerConnectedThread() {
        return serverConnectedThread;
    }
}
