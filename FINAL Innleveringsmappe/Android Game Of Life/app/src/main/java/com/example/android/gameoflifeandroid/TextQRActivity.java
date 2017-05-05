package com.example.android.gameoflifeandroid;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TextQRActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.android.gameoflifeandroid.MESSAGE";

    /**
     * Overrides onCreate.
     * Initialize the activity. Defining UI with calling setContentView.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_qr);
        Log.d("CREATION", "TextQRActivity was created!");
    }

    /**
     * Checks the text that is entered and if passes startActivity, if not shows an warning message.
     *
     * @param view
     */
    public void convertTextToQR(View view) {
        Intent textQrIntent = new Intent(this, TextQrGol.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        if (message.length() > 500) {
            ToastLibrary.longToast(getApplicationContext(), "Please use less than 500 characters." + "\nCurrently you are using " + message.length() + " characters.");
        } else if (message.length() == 0) {
            ToastLibrary.longToast(getApplicationContext(), "Please enter a message.");
        } else {
            textQrIntent.putExtra(EXTRA_MESSAGE, message);
            startActivity(textQrIntent);
        }
    }
}
