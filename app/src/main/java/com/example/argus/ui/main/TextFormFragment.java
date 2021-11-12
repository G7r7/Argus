package com.example.argus.ui.main;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.argus.databinding.FragmentTextFormBinding;

import com.example.argus.R;

import java.util.ArrayList;

import petrov.kristiyan.colorpicker.ColorPicker;


/**
 * A Text form fragment for sending text to display
 */
public class TextFormFragment extends Fragment {

    private PageViewModel pageViewModel;
    private FragmentTextFormBinding binding;
    private int textColor = 0;
    private int backgroundColor = 0;
    private String text = "";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("textColor", this.textColor);
        outState.putInt("backgroundColor", this.backgroundColor);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            this.textColor = savedInstanceState.getInt("textColor");
            this.backgroundColor = savedInstanceState.getInt("backgroundColor");
        }
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
                if(s.length() != 0) {
                    text = s.toString();
                    refreshTextBitmapPreview();
                }
            }
        });
        binding.button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openTextColorPicker();
            }
        });
        binding.button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openBackgroundColorPicker();
            }
        });
        if(textColor != 0) {
            binding.button2.setBackgroundColor(textColor);
        }
        if(backgroundColor != 0) {
            binding.button3.setBackgroundColor(backgroundColor);
        }
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void openTextColorPicker(){
        ColorPicker colorPicker = new ColorPicker(getActivity());
        colorPicker.setTitle(getString(R.string.tab_text_type_select_color_title));
        colorPicker.show();
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position,int color) {
                textColor = color;
                binding.button2.setBackgroundColor(color);
                refreshTextBitmapPreview();
            }
            @Override
            public void onCancel(){}
        });
    }

    public void openBackgroundColorPicker(){
        ColorPicker colorPicker = new ColorPicker(getActivity());
        colorPicker.setTitle(getString(R.string.tab_text_type_select_color_title));
        colorPicker.show();
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position,int color) {
                backgroundColor = color;
                binding.button3.setBackgroundColor(color);
                refreshTextBitmapPreview();
            }
            @Override
            public void onCancel(){}
        });
    }

    private void refreshTextBitmapPreview() {
        Bitmap b = generateTextBitmap();
        binding.textPreview.setImageBitmap(b);
    }

    private Bitmap generateTextBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // BG
        Paint paintBg = new Paint();
        paintBg.setColor(this.backgroundColor);
        canvas.drawRect(0, 0, 200, 200, paintBg);

        String string = text;
        float scale = getResources().getDisplayMetrics().density;
        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(this.textColor);
        paintText.setTextSize((int) (14 * scale));
        paintText.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text in the center
        Rect bounds = new Rect();
        paintText.getTextBounds(string, 0, string.length(), bounds);

        int x = (bitmap.getWidth() - bounds.width())/2;
        int y = (bitmap.getHeight() + bounds.height())/2;

        canvas.drawText(string, x, y, paintText);

        return bitmap;
    }
}