package com.example.argus.backend;

import java.util.HashMap;

public class Settings {

    private int widthPx = 16;
    private int heightPx = 16;
    private float textScale = 1;
    private int textFontSize = 14;

    public void updateSettings(HashMap<String, Object> parameters) throws Exception {
        int widthPx = (int)parameters.get("widthPx");
        int heightPx = (int)parameters.get("heightPx");
        float textScale = (float)parameters.get("textScale");
        int textFontSize = (int)parameters.get("textFontSize");
        if(!(widthPx > 0 && heightPx > 0))
            throw new Exception("La hauteur et la largeur doivent être supérieures à zéro");
        if(!(textFontSize > 0))
            throw new Exception("La taille de police doit être supérieure à zéro");
        if(!(textScale > 0))
            throw new Exception("L'échelle doit être supérieure à zéro");
        this.widthPx = widthPx;
        this.heightPx = heightPx;
        this.textScale = textScale;
        this.textFontSize = textFontSize;
    }

    public HashMap<String, Object> getSettings() {
        HashMap<String, Object> settings = new HashMap<String, Object>();
        settings.put("widthPx", this.getWidthPx());
        settings.put("heightPx", this.getHeightPx());
        settings.put("textScale", this.getTextScale());
        settings.put("textFontSize", this.getTextFontSize());
        return settings;
    }

    public int getWidthPx() {
        return widthPx;
    }
    public int getHeightPx() {
        return heightPx;
    }

    public float getTextScale() {
        return textScale;
    }

    public int getTextFontSize() {
        return textFontSize;
    }
}
