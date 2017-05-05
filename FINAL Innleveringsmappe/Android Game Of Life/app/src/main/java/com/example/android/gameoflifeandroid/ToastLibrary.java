package com.example.android.gameoflifeandroid;

import android.content.Context;
import android.widget.Toast;

public class ToastLibrary {

    public ToastLibrary() {
    }

    /**
     * Displays a message at the bottom of the users screen.
     * Long lifespan.
     *
     * @param context Origin context of the toast.
     * @param string Message the toast should contain
     */
    public static void longToast(Context context, String string) {
        CharSequence text = string;
        int duration = Toast.LENGTH_LONG;

        Toast longToast = Toast.makeText(context, text, duration);
        longToast.show();
    }

    /**
     * Displays a message at the bottom of the users screen.
     * Short lifespan.
     *
     * @param context Origin context of the toast.
     * @param string Message the toast should contain
     */
    public static void shortToast(Context context, String string) {
        CharSequence text = string;
        int duration = Toast.LENGTH_SHORT;

        Toast shortToast = Toast.makeText(context, text, duration);
        shortToast.show();
    }
}