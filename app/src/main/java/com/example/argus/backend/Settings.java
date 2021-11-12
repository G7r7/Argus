package com.example.argus.backend;

public class Settings {

    private int widthPx = 16;
    private int heightPx = 16;

    public int getWidthPx() {
        return widthPx;
    }
    public int getHeightPx() {
        return heightPx;
    }

    public void setWidthAndHeightPx(int widthPx, int heightPx) throws Exception {
        if(widthPx > 0 && heightPx > 0) {
            this.widthPx = widthPx;
            this.heightPx = heightPx;
        }
        else
            throw new Exception("Width and Height must be > 0");
    }
}
