package com.example.argus.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.argus.databinding.FragmentTextFormBinding;

import java.util.ArrayList;

import petrov.kristiyan.colorpicker.ColorPicker;


/**
 * A Text form fragment for sending text to display
 */
public class TextFormFragment extends Fragment {

    private PageViewModel pageViewModel;
    private FragmentTextFormBinding binding;
    private int textColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentTextFormBinding.inflate(inflater, container, false);
        binding.EditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                    binding.textPreview.setText(s);
            }
        });
        binding.button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openColorPicker();
            }
        });
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void openColorPicker(){
        ColorPicker colorPicker = new ColorPicker(getActivity());
        ArrayList<String> colors = new ArrayList<>();
        colors.add("#82B926");
        colors.add("#a276eb");
        colors.add("#6a3ab2");
        colors.add("#666666");
        colors.add("#FFFF00");
        colors.add("#3C8D2F");
        colors.add("#FA9F00");
        colors.add("#FF0000");
        colorPicker.setColors(colors);
        colorPicker.show();
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position,int color) {
                textColor = color;
                binding.button2.setBackgroundColor(color);
                binding.textPreview.setTextColor(color);
            }

            @Override
            public void onCancel(){

            }
        });
    }
}