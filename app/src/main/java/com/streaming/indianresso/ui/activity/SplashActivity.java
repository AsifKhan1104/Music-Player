package com.streaming.indianresso.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
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
            ActivityCompat.requestPermissions(this, new String[]{permissionNeeded}, REQUEST_READ_PERMISSION);
        } else {
            goToLauncherScreen();
        }
    }

    //put the callback(if needed) on request for permission (allowed or denied)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_PERMISSION && grantResults.length == 1) {
            //do your code here
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goToLauncherScreen();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                MarshMallowUtils.showDialog(this, getString(R.string.permission_denial_message), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        goToLauncherScreen();
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UIElementsHelper.setStatusBarColor(this);
    }
}
