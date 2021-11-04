package com.example.argus.ui.main;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.argus.databinding.FragmentPictureFormBinding;

import java.io.IOException;


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
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE) {
            Uri imageUri = data.getData();
            if (null != imageUri) {
                binding.imageView.setImageURI(imageUri);
                ContentResolver contentResolver = getContext().getContentResolver();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);
                    int smallestDimension = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() : bitmap.getWidth();
                    Bitmap squareBitmap = Bitmap.createBitmap(bitmap, 0, 0, smallestDimension, smallestDimension);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(squareBitmap, 16, 16, false);
                    binding.imagePreview.setImageBitmap(resizedBitmap);
                } catch (IOException E) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                    builder1.setMessage("Fichier introuvable");
                    builder1.setCancelable(true);
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        }
    }
}