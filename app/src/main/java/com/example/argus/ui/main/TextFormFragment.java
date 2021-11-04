package com.example.argus.ui.main;

import android.os.Bundle;
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

import petrov.kristiyan.colorpicker.ColorPicker;


/**
 * A Text form fragment for sending text to display
 */
public class TextFormFragment extends Fragment {

    private PageViewModel pageViewModel;
    private FragmentTextFormBinding binding;

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
        colorPicker.show();
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position,int color) {
                // put code
            }

            @Override
            public void onCancel(){

            }
        });
    }
}