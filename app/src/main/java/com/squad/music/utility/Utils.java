package com.squad.music.utility;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.squad.music.R;

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
}
