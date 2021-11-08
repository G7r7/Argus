package com.example.argus.ui.main;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.argus.backend.Debug;
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
    String imageUri = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imageUri", this.imageUri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            this.imageUri = savedInstanceState.getString("imageUri", null);
        }
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
        if (this.imageUri != null) {
            this.displayAndConvertUri(Uri.parse(this.imageUri));
        }
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void displayAndConvertUri(Uri imageUri) {
        try {
            binding.imageView.setImageURI(imageUri);
            ContentResolver contentResolver = getContext().getContentResolver();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);
            int smallestDimension = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() : bitmap.getWidth();
            Bitmap squareBitmap = Bitmap.createBitmap(bitmap, 0, 0, smallestDimension, smallestDimension);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(squareBitmap, 16, 16, false);
            binding.imagePreview.setImageBitmap(resizedBitmap);
        } catch (IOException E) {
            Log.e("File", "Fichier introuvable.");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE) {
            Uri imageUri = data.getData();
            this.imageUri = imageUri.toString();
            displayAndConvertUri(imageUri);
        }
    }
}