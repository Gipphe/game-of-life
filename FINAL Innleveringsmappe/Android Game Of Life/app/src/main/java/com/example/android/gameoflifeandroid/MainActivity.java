package com.example.android.gameoflifeandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    /**
     * Overrides onCreate method.
     * Initialize the activity. Defining UI with calling setContentView.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Method for the QR button.
     *
     * @param view
     */
    public void textQRButton(View view) {
        Intent intent = new Intent(this, TextQRActivity.class);
        startActivity(intent);
    }

    /**
     * Method for the about button.
     *
     * @param view
     */
    public void aboutButton(View view) {
        ToastLibrary.longToast(getApplicationContext(), "Current feature set:"
                                                        + "\n• Text-To-QR Game of Life"
                                                        + "\n\nProgramutvikling DATS1600");
    }
}
