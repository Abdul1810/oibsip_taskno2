package com.oasis.todo.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.app.Dialog;

public class Utils {
    public void showDialog(Context context, String title, String message, Runnable onSuccess) {
        AlertDialog.Builder alert11 = new AlertDialog.Builder(context);
        alert11.setTitle(title);
        alert11.setMessage(message);
        alert11.setCancelable(true);
        // make the style modern
        alert11.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (onSuccess != null) {
                            onSuccess.run();
                        }
                        dialog.cancel();
                    }
                });
        alert11.show();
    }
}
