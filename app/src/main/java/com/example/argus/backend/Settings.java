package com.example.argus.backend;

import java.util.HashMap;

public class Settings {

    private int widthPx = 16;
    private int heightPx = 16;
    private float textScale = 1;

    public void updateSettings(HashMap<String, Object> parameters) throws Exception {
        int widthPx = (int)parameters.get("widthPx");
        int heightPx = (int)parameters.get("heightPx");
        float textScale = (float)parameters.get("textScale");
        if(!(widthPx > 0 && heightPx > 0))
            throw new Exception("La hauteur et la largeur doivent être supérieures à zéro");
        if(!(textScale > 0))
            throw new Exception("L'échelle doit être supérieure à zéro");
        this.widthPx = widthPx;
        this.heightPx = heightPx;
        this.textScale = textScale;
    }

    public HashMap<String, Object> getSettings() {
        HashMap<String, Object> settings = new HashMap<String, Object>();
        settings.put("widthPx", this.getWidthPx());
        settings.put("heightPx", this.getHeightPx());
        settings.put("textScale", this.getTextScale());
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

}
