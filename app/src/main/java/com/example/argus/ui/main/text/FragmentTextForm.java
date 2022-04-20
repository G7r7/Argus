package com.example.argus.ui.main.text;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.argus.MainActivityViewModel;
import com.example.argus.backend.EncodedBitmap;
import com.example.argus.databinding.FragmentTextFormBinding;

import com.example.argus.R;
import com.example.argus.ui.main.PageViewModel;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import petrov.kristiyan.colorpicker.ColorPicker;


/**
 * A Text form fragment for sending text to display
 */
public class FragmentTextForm extends Fragment {

    private FragmentTextFormBinding binding;
    private int textColor = 0;
    private int backgroundColor = 0;
    private float fontSizePx = 12;
    private String text = "";
    private MainActivityViewModel mainModel;
    private int[] rgbValues;
    private int[] bgrValues;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("textColor", this.textColor);
        outState.putInt("backgroundColor", this.backgroundColor);
        outState.putFloat("fontSizePx", this.fontSizePx);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            this.textColor = savedInstanceState.getInt("textColor");
            this.backgroundColor = savedInstanceState.getInt("backgroundColor");
            this.fontSizePx = savedInstanceState.getFloat("fontSizePx");
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        this.mainModel= new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        this.mainModel.getHeightPx().observe(getViewLifecycleOwner(), heightPx -> { this.refreshTextBitmapPreview(); });
        this.mainModel.getWidthPx().observe(getViewLifecycleOwner(), widthPx -> { this.refreshTextBitmapPreview(); });
        this.mainModel.getBitsPerColor().observe(getViewLifecycleOwner(), bits -> { this.refreshTextBitmapPreview(); });
        this.mainModel.getIsClientThreadConnected().observe(getViewLifecycleOwner(), connected -> { this.refreshSendButton(connected); });

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
        binding.editTextFontSize.addTextChangedListener(new TextWatcher() {

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
                    fontSizePx = Float.parseFloat(s.toString());
                    refreshTextBitmapPreview();
                }
            }
        });
        binding.buttonMinus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(fontSizePx > 1)
                    fontSizePx--;
                binding.editTextFontSize.setText(String.valueOf(fontSizePx));
                refreshTextBitmapPreview();
            }
        });
        binding.buttonPlus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fontSizePx++;
                binding.editTextFontSize.setText(String.valueOf(fontSizePx));
                refreshTextBitmapPreview();
            }
        });
        binding.buttonSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ByteBuffer buffer = ByteBuffer.allocate(bgrValues.length);
                for (int value : bgrValues) {
                    Byte octet = (byte)(value & 0xFF);
                    buffer.put(octet);
                }
                mainModel.getClientThread().getValue().write(buffer.array());
            }
        });
        if(textColor != 0) {
            binding.button2.setBackgroundColor(textColor);
        }
        if(backgroundColor != 0) {
            binding.button3.setBackgroundColor(backgroundColor);
        }
        if(fontSizePx != 0) {
            binding.editTextFontSize.setText(String.valueOf(fontSizePx));
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
        EncodedBitmap eb = new EncodedBitmap(b, this.mainModel.getBitsPerColor().getValue());
        this.rgbValues = eb.getEncodedRGBValues();
        this.bgrValues = eb.getEncodedBGRValues();
        binding.textPreview.setImageBitmap(eb.getTransformedBitmap());
    }

    private Bitmap generateTextBitmap() {
        int widthPx = this.mainModel.getWidthPx().getValue();
        int heightPx = this.mainModel.getHeightPx().getValue();
        Bitmap bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // BG
        Paint paintBg = new Paint();
        paintBg.setColor(this.backgroundColor);
        canvas.drawRect(0, 0, widthPx, heightPx, paintBg);

        String string = text;
        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(this.textColor);
        paintText.setTextSize(this.fontSizePx);


        // draw text in the center
        Rect bounds = new Rect();
        paintText.getTextBounds(string, 0, string.length(), bounds);

        int x = (bitmap.getWidth() - bounds.width())/2;
        int y = (bitmap.getHeight() + bounds.height())/2;

        canvas.drawText(string, x, y, paintText);

        return bitmap;
    }

    private void refreshSendButton(boolean connected) {
        if(connected)
            this.binding.buttonSend.setEnabled(true);
        else
            this.binding.buttonSend.setEnabled(false);
    }
}