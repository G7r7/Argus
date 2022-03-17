package com.example.argus.backend;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import androidx.annotation.ColorInt;

public class EncodedBitmap {
    private Bitmap source;
    private Bitmap.Config config = Bitmap.Config.ARGB_8888;
    private int bitsPerColor;
    private int[] RGBValues;
    private int[] encodedRGBValues;
    private int multiplier;
    private Bitmap transformedBitmap;

    public EncodedBitmap(Bitmap bitmap, int bitsPerColor) {
        this.source = bitmap;
        this.bitsPerColor = bitsPerColor;
        this.multiplier = (int) Math.pow(2, 8 - this.bitsPerColor + 1) - 1;
        this.RGBValues = this.getRGBValues();
        this.encodedRGBValues = this.getEncodedRGBValues();
        this.transformedBitmap = this.getTransformedBitmap();
    }

    public int[] getRGBValues()
    {
        if (this.RGBValues != null)
            return this.RGBValues;
        else {
            ColorMatrix colorMatrix = new ColorMatrix();
            ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
            Bitmap argbBitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), this.config);
            Canvas canvas = new Canvas(argbBitmap);
            Paint paint = new Paint();
            paint.setColorFilter(colorFilter);
            canvas.drawBitmap(source, 0, 0, paint);

            int width = source.getWidth();
            int height = source.getHeight();
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
    }

    public Bitmap getTransformedBitmap()
    {
        if (this.transformedBitmap != null)
            return this.transformedBitmap;
        else {
            int[] RGBs = this.getEncodedRGBValues();
            int multiplier = (int) Math.pow(2, 8 - this.bitsPerColor + 1) - 1;
            int arraySize = this.source.getWidth() * this.source.getHeight();
            int[] colors = new int[arraySize];
            for (int i = 2; i < RGBs.length; i += 3) {
                int A = 255;
                int R = RGBs[i-2] * multiplier;
                int G = RGBs[i-1] * multiplier;
                int B = RGBs[i] * multiplier;
                int color = (A & 0xff) << 24 | (R & 0xff) << 16 | (G & 0xff) << 8 | (B & 0xff);
                colors[i/3] = color;
            }
            return Bitmap.createBitmap(colors, this.source.getWidth(), this.source.getHeight(), this.config);
        }

    }

    public int[] getEncodedRGBValues() {
        if(this.encodedRGBValues != null)
            return this.encodedRGBValues;
        else {
            int[] encodedRGBValues = new int[this.RGBValues.length];
            for (int i = 0; i < this.RGBValues.length; i++) {
                encodedRGBValues[i] = Math.round((float) this.RGBValues[i] / this.multiplier);
            }
            return encodedRGBValues;
        }
    }
}
