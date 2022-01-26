package com.example.argus;

import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.argus.backend.client.BluetoothClientThread;
import com.example.argus.backend.server.BluetoothServerConnectedThread;
import com.example.argus.backend.server.BluetoothServerThread;

import java.nio.channels.MulticastChannel;
import android.os.Handler;

public class MainActivityViewModel extends ViewModel {

    private final MutableLiveData<Integer> widthPx = new MutableLiveData<>(16);
    private final MutableLiveData<Integer> heightPx = new MutableLiveData<>(16);
    private final MutableLiveData<BluetoothDevice> server = new MutableLiveData<>();
    private final MutableLiveData<BluetoothClientThread> clientThread = new MutableLiveData<>();
    private final MutableLiveData<BluetoothServerThread> serverThread = new MutableLiveData<>();
    private final MutableLiveData<BluetoothServerConnectedThread> serverConnectedThread = new MutableLiveData<>();
    private final MutableLiveData<Thread.State> clientThreadStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isClientThreadConnected = new MutableLiveData<>(false);
    private final MutableLiveData<Thread.State> serverThreadStatus = new MutableLiveData<>();


    public void setResolution(int widthPx, int heightPx) throws Exception {
        if(!(widthPx > 0 && heightPx > 0))
            throw new Exception("La hauteur et la largeur doivent être supérieures à zéro");
        this.widthPx.setValue(widthPx);
        this.heightPx.setValue(heightPx);
    }
    public void setServerDevice(BluetoothDevice device) { this.server.setValue(device); }
    public void setClientThread(BluetoothClientThread thread) { this.clientThread.setValue(thread); }
    public void setServerThread(BluetoothServerThread thread) { this.serverThread.setValue(thread); }
    public void setServerConnectedThread(BluetoothServerConnectedThread thread) { this.serverConnectedThread.postValue(thread); }
    public void setClientThreadStatus(Thread.State state) { this.clientThreadStatus.postValue(state); }
    public void setIsClientThreadConnected(Boolean bool) { this.isClientThreadConnected.postValue(bool); }
    public void setServerThreadStatus(Thread.State state) { this.serverThreadStatus.postValue(state); }


    public LiveData<Integer> getWidthPx() { return this.widthPx; }
    public LiveData<Integer> getHeightPx() { return this.heightPx; }
    public MutableLiveData<BluetoothDevice> getServerDevice() { return this.server; }
    public MutableLiveData<BluetoothClientThread> getClientThread() { return this.clientThread; }
    public MutableLiveData<Thread.State> getClientThreadStatus() { return this.clientThreadStatus; }
    public MutableLiveData<Boolean> getIsClientThreadConnected() { return this.isClientThreadConnected; }
    public MutableLiveData<BluetoothServerThread> getServerThread() { return this.serverThread; }
    public MutableLiveData<BluetoothServerConnectedThread> getServerConnectedThread() { return this.serverConnectedThread; }
    public MutableLiveData<Thread.State> getServerThreadStatus() { return this.serverThreadStatus; }

}
