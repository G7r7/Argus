package com.example.argus;

import android.os.Bundle;

import com.example.argus.backend.Settings;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import com.example.argus.ui.main.SectionsPagerAdapter;
import com.example.argus.databinding.ActivityMainBinding;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public Settings settings = new Settings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            HashMap<String, Object> settings = this.settings.getSettings();
            settings.put("textScale", getResources().getDisplayMetrics().density);
            this.settings.updateSettings(settings);
        } catch (Exception e) {
            Log.e("ERR", "Can't set text Scale");
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
    }
}