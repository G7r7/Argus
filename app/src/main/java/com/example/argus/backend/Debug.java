package com.example.argus.backend;

import android.app.AlertDialog;
import android.content.Context;

public class Debug {
    public static void alert(Context context, String msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(msg);
        builder1.setCancelable(true);
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
