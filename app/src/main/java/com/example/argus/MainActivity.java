package com.example.argus;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.argus.backend.Settings;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.argus.ui.main.SectionsPagerAdapter;
import com.example.argus.databinding.ActivityMainBinding;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainActivityViewModel model;

    public static final int MY_PERMISSION_CONNECT_BLUETOOTH = 1;
    public static final int MY_PERMISSION_ADVERTISE_BLUETOOTH = 2;
    public static final int MY_PERMISSION_SCAN_BLUETOOTH = 3;
    public static final int MY_PERMISSION_ADMIN_BLUETOOTH = 4;
    public static final int MY_PERMISSION_BLUETOOTH = 5;
    public static final int MY_PERMISSION_COARSE_LOCATION = 6;
    public static final int MY_PERMISSION_FINE_LOCATION = 7;
    public static final int MY_PERMISSION_ACCESS_BACKGROUND_LOCATION = 8;

    private static final String TAG = "TEST";

    private void updateClientThreadStatus() {
        if(model.getClientThreadStatus().getValue() == null)
            binding.clientThreadStatus.setText("Aucun thread client");
        else if(model.getClientThreadStatus().getValue() == Thread.State.NEW)
            binding.clientThreadStatus.setText("Thread client crée");
        else if(model.getClientThreadStatus().getValue() == Thread.State.RUNNABLE) {
            binding.clientThreadStatus.setText("Thread client démarré");
            if(model.getIsClientThreadConnected().getValue())
                binding.clientThreadStatus.setText("Socket client connecté");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        model = new ViewModelProvider(this).get(MainActivityViewModel.class);
        this.updateClientThreadStatus();
        model.getClientThreadStatus().observe(this, status -> { this.updateClientThreadStatus(); });
        model.getIsClientThreadConnected().observe(this, connected -> { this.updateClientThreadStatus(); });
        this.askBluetoothPermission();
        setContentView(binding.getRoot());
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

