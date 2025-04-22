package com.streaming.indianresso.ui.activity;

import static com.streaming.indianresso.utility.MarshMallowUtils.getNotificationPermissionDeniedCount;
import static com.streaming.indianresso.utility.MarshMallowUtils.getReadPermissionDeniedCount;
import static com.streaming.indianresso.utility.MarshMallowUtils.incrementNotificationPermissionDeniedCount;
import static com.streaming.indianresso.utility.MarshMallowUtils.incrementReadPermissionDeniedCount;
import static com.streaming.indianresso.utility.MarshMallowUtils.resetNotificationPermissionDeniedCount;
import static com.streaming.indianresso.utility.MarshMallowUtils.resetReadPermissionDeniedCount;
import static com.streaming.indianresso.utility.MarshMallowUtils.showGoToSettingsDialog;

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

import com.streaming.indianresso.Common;
import com.streaming.indianresso.R;
import com.streaming.indianresso.helper.TypefaceHelper;
import com.streaming.indianresso.helper.UIElementsHelper;
import com.streaming.indianresso.utility.MarshMallowUtils;

import java.util.function.Consumer;
import java.util.function.Function;

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

        Common mApp = (Common) this.getApplicationContext();
        if (mApp.getCurrentTheme() == Common.LIGHT_THEME) {
            mLinearLayout.setBackgroundResource(R.drawable.splash_bg_themed_light);
        }
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
                //checkForPermissions();
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
        if (grantResults.length == 0) return;

        if (requestCode == REQUEST_READ_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                resetReadPermissionDeniedCount(this);
                goToLauncherScreen();
            } else {
                handleReadPermissionDenied();
            }
        } else if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                resetNotificationPermissionDeniedCount(this);
                goToLauncherScreen();
            } else {
                handleNotificationPermissionDenied();
            }
        }
    }

    private void handleReadPermissionDenied() {
        incrementReadPermissionDeniedCount(this);
        int denialCount = getReadPermissionDeniedCount(this);

        if (denialCount >= 2) {
            showGoToSettingsDialog(mContext, getString(R.string.goto_permission_settings_msg));
        } else {
            MarshMallowUtils.showDialog(this, getString(R.string.permission_denial_message), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    checkForPermissions(); // retry permission
                }
            });
        }
    }

    private void handleNotificationPermissionDenied() {
        incrementNotificationPermissionDeniedCount(this);
        int denialCount = getNotificationPermissionDeniedCount(this);

        if (denialCount >= 2) {
            goToLauncherScreen();
        } else {
            MarshMallowUtils.showDialog(this, getString(R.string.permission_denial_message_notification), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    checkForPermissions(); // retry permission
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UIElementsHelper.setStatusBarColor(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkForPermissions();
    }
}
