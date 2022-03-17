package com.example.argus.ui.main.picture;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.argus.MainActivityViewModel;
import com.example.argus.backend.EncodedBitmap;
import com.example.argus.databinding.FragmentPictureFormBinding;
import com.example.argus.ui.main.PageViewModel;

import java.io.IOException;


/**
 * A Picture form fragment for sending text to display
 */
public class FragmentPictureForm extends Fragment {
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
        MainActivityViewModel model = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        model.getWidthPx().observe(getViewLifecycleOwner(), widthPx -> {
            if (this.imageUri != null) {
                this.displayAndConvertUri(Uri.parse(this.imageUri));
            }
        });
        model.getHeightPx().observe(getViewLifecycleOwner(), heightPx -> {
            if (this.imageUri != null) {
                this.displayAndConvertUri(Uri.parse(this.imageUri));
            }
        });
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

            MainActivityViewModel model = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
            int widthPx = model.getWidthPx().getValue();
            int heightPx = model.getHeightPx().getValue();
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(squareBitmap, widthPx, heightPx, false);
            EncodedBitmap encodedBitmap = new EncodedBitmap(resizedBitmap, 2);

            binding.imagePreview.setImageBitmap(encodedBitmap.getTransformedBitmap());
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

    private int[] rgbValuesFromBitmap(Bitmap bitmap)
    {
        ColorMatrix colorMatrix = new ColorMatrix();
        ColorFilter colorFilter = new ColorMatrixColorFilter(
                colorMatrix);
        Bitmap argbBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(argbBitmap);

        Paint paint = new Paint();

        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int componentsPerPixel = 3;
        int totalPixels = width * height;
        int totalElements = totalPixels * componentsPerPixel;

        int[] rgbValues = new int[totalElements];
        @ColorInt int[] argbPixels = new int[totalPixels];
        argbBitmap.getPixels(argbPixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < totalPixels; i++) {
            @ColorInt int argbPixel = argbPixels[i];
            int red = Color.red(argbPixel);
            int green = Color.green(argbPixel);
            int blue = Color.blue(argbPixel);
            rgbValues[i * componentsPerPixel + 0] = red;
            rgbValues[i * componentsPerPixel + 1] = green;
            rgbValues[i * componentsPerPixel + 2] = blue;
        }

        return rgbValues;
    }

    private Bitmap BitmapFromRgbvalues1bit(int[] rgbValues, int width, int height)
    {
        int[] colors = new int[width*height];
        for (int i = 2; i < rgbValues.length; i += 3) {
            int A = 255;
            int R = rgbValues[i-2]*255;
            int G = rgbValues[i-1]*255;
            int B = rgbValues[i]*255;
            int color = (A & 0xff) << 24 | (R & 0xff) << 16 | (G & 0xff) << 8 | (B & 0xff);
            colors[i/3] = color;
        }

        return Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
    }
}