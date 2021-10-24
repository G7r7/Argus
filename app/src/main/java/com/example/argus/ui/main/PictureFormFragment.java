package com.example.argus.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.argus.databinding.FragmentPictureFormBinding;
import com.example.argus.databinding.FragmentTextFormBinding;


/**
 * A Picture form fragment for sending text to display
 */
public class PictureFormFragment extends Fragment {
    private PageViewModel pageViewModel;
    private FragmentPictureFormBinding binding;
    int SELECT_PICTURE = 200;
    int RESULT_OK = 200;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentPictureFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button chooseImageButton = binding.button;
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE) {
            Uri selectedImageUri = data.getData();
            if (null != selectedImageUri) {
                binding.imageView2.setImageURI(selectedImageUri);
            }
        }
    }
}