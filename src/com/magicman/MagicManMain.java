package com.magicman;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import com.example.magicman.R;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.util.List;

public class MagicManMain extends Activity {

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

        takePicture(ActivityRequest.TAKE_PICTURE.ordinal());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode < 0 && requestCode == ActivityRequest.TAKE_PICTURE.ordinal() ) {
            Log.i("MagicMan", "Picture taken " + data.toString());
        }
    }

    private void takePicture(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, true);
        startActivityForResult(takePictureIntent, actionCode);
    }

    public static boolean isIntentAvailible(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
