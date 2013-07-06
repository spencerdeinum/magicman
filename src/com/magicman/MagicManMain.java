package com.magicman;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.example.magicman.R;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.*;
import java.util.List;

public class MagicManMain extends Activity {

    public static final String MAGICMAN = "MagicMan";

    public static final String APP_DATA_DIRECTORY = Environment.getExternalStorageDirectory() + "/" + MAGICMAN + "/";
    public static final String TESSDATA_PATH = APP_DATA_DIRECTORY + "tessdata";

    public enum ActivityRequest {
        TAKE_PICTURE
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        File tessdataPath = new File(TESSDATA_PATH);
        tessdataPath.mkdirs();

        Log.i(MAGICMAN, APP_DATA_DIRECTORY);
        Log.i(MAGICMAN, TESSDATA_PATH);
        File traineddata = new File(TESSDATA_PATH + "/eng.traineddata");
        try {
            traineddata.createNewFile();
        } catch (IOException e) {
            Log.e(MAGICMAN, e.getMessage());
            e.printStackTrace();
        }

        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outputStream = new FileOutputStream(TESSDATA_PATH + "/eng.traineddata");
            byte[] buffer = new byte[1024];
            int read;

            while((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            inputStream.close();
            outputStream.close();
            Log.i(MAGICMAN, "Trained data copied.");
        } catch (IOException e) {
            Log.e(MAGICMAN, e.getMessage());
            e.printStackTrace();
        }

        Button takePictureButton = (Button) findViewById(R.id.takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture(ActivityRequest.TAKE_PICTURE.ordinal());
            }
        });
    }

    private void takePicture(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, true);
        startActivityForResult(takePictureIntent, actionCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if( resultCode == RESULT_OK && requestCode == ActivityRequest.TAKE_PICTURE.ordinal() ) {
            Log.i(MAGICMAN, "Picture taken " + intent.toString());
            Bundle extras = intent.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");

            processImage(bitmap);
        }
    }

    private void processImage(Bitmap bitmap) {
        // Tess requires ARGB_8888
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.setDebug(true);
        tessBaseAPI.init(APP_DATA_DIRECTORY, "eng");
        tessBaseAPI.setImage(bitmap);
        Log.i(MAGICMAN, tessBaseAPI.toString());
        Log.i(MAGICMAN, tessBaseAPI.getUTF8Text());
        tessBaseAPI.end();
    }

    public static boolean isIntentAvailible(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
