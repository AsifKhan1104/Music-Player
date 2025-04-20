package com.streaming.indianresso.ui.activity;

import static com.streaming.indianresso.utility.MarshMallowUtils.getPermissionDeniedCount;
import static com.streaming.indianresso.utility.MarshMallowUtils.incrementPermissionDeniedCount;
import static com.streaming.indianresso.utility.MarshMallowUtils.resetPermissionDeniedCount;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.streaming.indianresso.R;
import com.streaming.indianresso.helper.TypefaceHelper;
import com.streaming.indianresso.helper.UIElementsHelper;
import com.streaming.indianresso.utility.MarshMallowUtils;

public class SplashActivity extends AppCompatActivity {
    private Context mContext;
    private TextView mTextViewAppName;
    private ImageView mImageViewAppLogo;
    private LinearLayout mLinearLayout;
    public static final int REQUEST_READ_PERMISSION = 786;
    public static final int REQUEST_NOTIFICATION_PERMISSION = 788;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        linkViewId();
        startAnimations();
    }

    private void linkViewId() {
        mContext = SplashActivity.this;
        mTextViewAppName = (TextView) findViewById(R.id.textview_app_name);
        mTextViewAppName.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Light"));
        mImageViewAppLogo = (ImageView) findViewById(R.id.imageview_logo);
        mLinearLayout = (LinearLayout) findViewById(R.id.linear_layout);
    }

    private void goToLauncherScreen() {
        Intent intent = new Intent(mContext, LauncherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void startAnimations() {
        /*Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        mLinearLayout.clearAnimation();
        mLinearLayout.startAnimation(anim);*/

        Animation.AnimationListener listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                checkForPermissions();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_right);
        anim.reset();
        mTextViewAppName.clearAnimation();
        mTextViewAppName.startAnimation(anim);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        animation.reset();
        animation.setAnimationListener(listener);
        mImageViewAppLogo.clearAnimation();
        mImageViewAppLogo.startAnimation(animation);
    }

    // To check a single permission call (for marshmallow permission system)
    private void checkForPermissions() {
        String permissionNeeded = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            permissionNeeded = Manifest.permission.READ_MEDIA_AUDIO;
        } else {
            permissionNeeded = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        if (!MarshMallowUtils.isPermissionGranted(this, permissionNeeded)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, new String[]{permissionNeeded, Manifest.permission.POST_NOTIFICATIONS}, REQUEST_READ_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permissionNeeded}, REQUEST_READ_PERMISSION);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!MarshMallowUtils.isPermissionGranted(this, Manifest.permission.POST_NOTIFICATIONS)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
                } else {
                    goToLauncherScreen();
                }
            } else {
                goToLauncherScreen();
            }
        }
    }

    //put the callback(if needed) on request for permission (allowed or denied)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                resetPermissionDeniedCount(SplashActivity.this);
                goToLauncherScreen();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                incrementPermissionDeniedCount(SplashActivity.this);
                int denialCount = getPermissionDeniedCount(SplashActivity.this);
                if (denialCount >= 2) {
                    // 3rd time — redirect to settings
                    showGoToSettingsDialog();
                } else {
                    // 1st or 2nd denial — show retry dialog
                    MarshMallowUtils.showDialog(this, getString(R.string.permission_denial_message), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            checkForPermissions(); // ask again
                        }
                    });
                }
            }
        }
    }

    private void showGoToSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Needed")
                .setMessage("You've denied permission multiple times. Please allow it manually from settings.")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UIElementsHelper.setStatusBarColor(this);
    }
}
