package com.example.argus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.argus.backend.Settings;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.argus.ui.main.SectionsPagerAdapter;
import com.example.argus.databinding.ActivityMainBinding;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public Settings settings = new Settings();

    public static final int MY_PERMISSION_CONNECT_BLUETOOTH = 1;
    public static final int MY_PERMISSION_ADVERTISE_BLUETOOTH = 2;
    public static final int MY_PERMISSION_SCAN_BLUETOOTH = 3;
    public static final int MY_PERMISSION_ADMIN_BLUETOOTH = 4;
    public static final int MY_PERMISSION_BLUETOOTH = 5;
    public static final int MY_PERMISSION_COARSE_LOCATION = 6;
    public static final int MY_PERMISSION_FINE_LOCATION = 7;
    public static final int MY_PERMISSION_ACCESS_BACKGROUND_LOCATION = 8;

    private static final String TAG = "TEST";

    private Handler mHandler = new Handler();
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateClientThreadStatus(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, 300);
            }
        }
    };

    void updateClientThreadStatus() {
        if(settings.getClientThread() == null)
            binding.clientThreadStatus.setText("Aucun thread client");
        else if(settings.getClientThread().getState() == Thread.State.NEW)
            binding.clientThreadStatus.setText("Thread client crée");
        else if(settings.getClientThread().getState() == Thread.State.RUNNABLE) {
            binding.clientThreadStatus.setText("Thread client démarré");
            boolean status = false;
            if(status = settings.getClientThread().getMmSocket().isConnected())
                binding.clientThreadStatus.setText("Socket client connecté");
            else
                binding.clientThreadStatus.setText("Socket client déconnecté");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.askBluetoothPermission();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        mStatusChecker.run();
    }

    private void askBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= 30 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Need bluetooth permission", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{ Manifest.permission.BLUETOOTH}, MY_PERMISSION_BLUETOOTH);
        } else {
            Log.w(TAG, "askBluetoothPermission: BLUETOOTH permission already granted" );
            Toast.makeText(getApplicationContext(), "BLUETOOTH permission already granted", Toast.LENGTH_SHORT).show();
        }
        if (Build.VERSION.SDK_INT >= 30 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Need bluetooth admin permission", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{ Manifest.permission.BLUETOOTH}, MY_PERMISSION_ADMIN_BLUETOOTH);
        } else {
            Log.w(TAG, "askBluetoothPermission: BLUETOOTH ADMIN permission already granted" );
            Toast.makeText(getApplicationContext(), "BLUETOOTH ADMIN permission already granted", Toast.LENGTH_SHORT).show();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Need coarse location permission", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION }, MY_PERMISSION_COARSE_LOCATION);
        } else {
            Log.w(TAG, "askBluetoothPermission: Coarse location permission already granted" );
            Toast.makeText(getApplicationContext(), "Coarse location permission already granted", Toast.LENGTH_SHORT).show();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Need fine location permission", Toast.LENGTH_SHORT);
            requestPermissions(new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSION_FINE_LOCATION);
        } else {
            Log.w(TAG, "askBluetoothPermission: Fine location permission already granted" );
            Toast.makeText(getApplicationContext(), "Fine location permission already granted", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "User accepted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "User denied", Toast.LENGTH_SHORT).show();
                }
        }
    }
}

