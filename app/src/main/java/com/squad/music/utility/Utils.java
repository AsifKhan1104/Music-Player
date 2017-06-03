package com.squad.music.utility;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.squad.music.R;

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
}
