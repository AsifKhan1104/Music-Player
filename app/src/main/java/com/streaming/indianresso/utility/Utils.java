package com.streaming.indianresso.utility;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.streaming.indianresso.R;

import java.io.File;

/**
 * Created by mdasifkhan on 3/6/17.
 */

public class Utils {

    public static void shareSong(Context context, String filePath) {
        Uri uri = Uri.parse(filePath);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(share, context.getString(R.string.share)));
    }

    public static boolean deleteSong(Context context, String filePath) {
        boolean deleteSuccess = false;
        File fdelete = new File(filePath);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                deleteSuccess = true;
                Toast.makeText(context, context.getString(R.string.song_deleted), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getString(R.string.some_error_occurred), Toast.LENGTH_SHORT).show();
            }
        }
        return deleteSuccess;
    }

    public static void openAppInGooglePlay(Context context, String packageName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (android.content.ActivityNotFoundException e) { // if there is no Google Play on device
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }
}
