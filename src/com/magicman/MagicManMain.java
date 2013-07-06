package com.magicman;

import android.app.Activity;
import android.os.Bundle;
import com.example.magicman.R;
import com.googlecode.tesseract.android.TessBaseAPI;

public class MagicManMain extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TessBaseAPI tessBaseAPI = new TessBaseAPI();

        setContentView(R.layout.main);
    }
}
