package com.myappforu.wpnews.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.myappforu.wpnews.R;
import com.myappforu.wpnews.utility.ActivityUtils;
import com.myappforu.wpnews.utility.AppUtils;

public class SplashActivity extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;
    private RelativeLayout rootLayout;

    // Constants
    private static final int SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initVariable();
        initView();

    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = SplashActivity.this;
    }

    private void initView() {
        setContentView(R.layout.activity_splash);
        rootLayout = (RelativeLayout) findViewById(R.id.splashBody);
    }

    private void initFunctionality() {
        if (AppUtils.isNetworkAvailable(mContext)) {
            rootLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ActivityUtils.getInstance().invokeActivity(mActivity, MainActivity.class, true);
                }
            }, SPLASH_DURATION);
        } else {
            AppUtils.noInternetWarning(rootLayout, mContext);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFunctionality();
    }
}

