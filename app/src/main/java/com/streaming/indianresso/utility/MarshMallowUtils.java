package com.streaming.indianresso.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import android.util.Log;

import com.streaming.indianresso.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Harish Sharma on 8/1/16.
 * <p/>
 * If you want to  make your application fully support the Runtime Permission
 * set targetSdkVersion  to the latest version, 23.
 * <p/>
 * Add the follwing line in your build.gradle under dependecies
 * compile 'com.android.support:support-v13:23.1.0'
 */
public class MarshMallowUtils {

    private static final String TAG = "MarshMallowUtils";


    /**
     * @param context
     * @param permission
     * @return this function checks whether a permission is granted or not to the app.
     */
    public static boolean isPermissionGranted(Context context, String permission) {
        int permissionValue = ContextCompat.checkSelfPermission(context, permission);
        if (permissionValue == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param activity    activity which require the permission
     * @param permission  permission string to be granted
     * @param requestCode an integer numper assigned to this request for permission
     * @param message     any message to be shown to user.(e.g. why this permission in required)
     *                    <p/>
     *                    To check the result of this method call, implement a callback method in your activity
     *                    i.e.  public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults)
     *                    <p/>
     *                    Even if you need the permission in fragment, the callback method of activity will be called.
     */
    public static void requestPermission(final Activity activity, final String permission, final int requestCode, String message) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            showDialog(activity, message,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                        }
                    });
            return;
        }
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }

    /**
     * @param activity
     * @param permissions string array of permissions to be granted.
     * @param requestCode an integer numper assigned to this request for permission
     * @param message     message to be shown to user regarding permission access.
     */
    public static void requestPermission(final Activity activity, final String[] permissions, final int requestCode, String message) {
        if (!message.isEmpty()) {
            showDialog(activity, message,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, permissions, requestCode);
                            dialog.dismiss();
                        }
                    });
            return;
        }
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    /**
     * @param activity
     * @param permissions hasmap of permission need to be checked for access.
     * @param requestCode an integer numper assigned to this request for permission
     *                    <p/>
     *                    This method prepare the message to be shown to the user from passed keys in hashmap(permissions) and request for required permimssion.
     */
    public static void requestPermission(final Activity activity, HashMap<String, String> permissions, int requestCode) {

        String message = "Need multiple permissions";
        boolean needPermission = false;//check whether to show some message or not.
        for (Map.Entry<String, String> entry : permissions.entrySet()) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, entry.getValue().toString())) {
                needPermission = true;
                message += entry.getKey().toString() + ",";
            }
        }
        if (!needPermission) {
            message = "";
        } else if (message.endsWith(",")) {
            message = message.substring(0, message.length() - 1);
        }
        ArrayList<String> per = new ArrayList<>(permissions.values());
        requestPermission(activity, per.toArray(new String[per.size()]), requestCode, message);
    }

    /**
     * @param context
     * @param permissions hasmap of permission need to be checked for access.
     * @return returns a filtered hashmap from input hashmap with permissions to be granted
     * <p/>
     * This method is to check multiple permissions at once for access.
     */
    public static HashMap checkForMultiplePermissionsToGrant(Context context, HashMap permissions) {
        Iterator iterator = permissions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            System.out.println(entry.getKey() + " = " + entry.getValue());
            if (isPermissionGranted(context, entry.getValue().toString())) {
                iterator.remove();
            }
        }
        return permissions;
    }

    /**
     * @param context
     * @param message
     * @param okListener show a dialog
     */
    public static void showDialog(Context context, String message, DialogInterface.OnClickListener okListener) {
        try {
            new AlertDialog.Builder(context, R.style.MyCustomDialogTheme)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(context.getResources().getString(R.string.ok), okListener)
                    /*.setNegativeButton(context.getResources().getString(R.string.label_cancel), null)*/
                    .create()
                    .show();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

}
