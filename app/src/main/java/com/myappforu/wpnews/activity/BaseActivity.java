package com.myappforu.wpnews.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.myappforu.wpnews.R;

import com.myappforu.wpnews.data.constant.AppConstant;
import com.myappforu.wpnews.utility.ActivityUtils;
import com.myappforu.wpnews.utility.AdUtils;
import com.myappforu.wpnews.utility.AppUtils;
import com.myappforu.wpnews.utility.PermissionUtils;

/**
 * Created by Ashiq on 10/10/16.
 */
public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context mContext;
    private Activity mActivity;

    // global toolbar
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    private LinearLayout loadingView, noDataView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = BaseActivity.this;
        mContext = mActivity.getApplicationContext();

        // uncomment this line to disable ad
        //AdUtils.getInstance(mContext).disableBannerAd();
        //AdUtils.getInstance(mContext).disableInterstitialAd();

    }

    public void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    public void initDrawer(boolean enable) {

        // Initialize drawer view
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (enable) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle
                    (this, mDrawerLayout, mToolbar, R.string.openDrawer, R.string.closeDrawer) {
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);

                }

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);

                }
            };


            mDrawerLayout.setDrawerListener(toggle);
            toggle.syncState();

            mNavigationView = (NavigationView) findViewById(R.id.navigationView);
            mNavigationView.setItemIconTintList(null);
            getNavigationView().setNavigationItemSelectedListener(this);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }


    public void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    public void enableBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Access toolbar
     */
    public Toolbar getToolBar() {
        return mToolbar;
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public NavigationView getNavigationView() {
        return mNavigationView;
    }

    public void initLoader() {
        loadingView = (LinearLayout) findViewById(R.id.loadingView);
        noDataView = (LinearLayout) findViewById(R.id.noDataView);
    }


    public void showLoader() {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }

        if (noDataView != null) {
            noDataView.setVisibility(View.GONE);
        }
    }

    public void hideLoader() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (noDataView != null) {
            noDataView.setVisibility(View.GONE);
        }
    }

    public void showEmptyView() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (noDataView != null) {
            noDataView.setVisibility(View.VISIBLE);
        }
    }

    public void showAdThenActivity(final Class<?> activity, final boolean shouldFinish) {
        if (AdUtils.getInstance(mContext).showFullScreenAd()) {
            AdUtils.getInstance(mContext).getInterstitialAd().setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    ActivityUtils.getInstance().invokeActivity(mActivity, activity, shouldFinish);
                }
            });
        } else {
            ActivityUtils.getInstance().invokeActivity(mActivity, activity, shouldFinish);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionUtils.isPermissionResultGranted(grantResults)) {
            if (requestCode == PermissionUtils.REQUEST_CALL) {
                AppUtils.makePhoneCall(mActivity, AppConstant.CALL_NUMBER);
            }
        } else {
            AppUtils.showToast(mActivity, getString(R.string.permission_not_granted));
        }

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            ActivityUtils.getInstance().invokeActivity(mActivity, MainActivity.class, true);
            finish();
        } else if (id == R.id.action_menus) {
            showAdThenActivity(MenuListActivity.class, false);
        } else if (id == R.id.action_categories) {
            showAdThenActivity(CategoryListActivity.class, false);
        } else if (id == R.id.action_favourite) {
            showAdThenActivity(FavouriteListActivity.class, false);
        } else if (id == R.id.action_home_cat) {
            showAdThenActivity(HomeCategoriesActivity.class, false);
        }

        // social
        else if (id == R.id.action_youtube) {
            AppUtils.youtubeLink(mActivity);
        } else if (id == R.id.action_facebook) {
            AppUtils.faceBookLink(mActivity);
        } else if (id == R.id.action_twitter) {
            AppUtils.twitterLink(mActivity);
        } else if (id == R.id.action_google_plus) {
            AppUtils.googlePlusLink(mActivity);
        }

        // support
        else if (id == R.id.action_call) {
            AppUtils.makePhoneCall(mActivity, AppConstant.CALL_NUMBER);
        } else if (id == R.id.action_message) {
            AppUtils.sendSMS(mActivity, AppConstant.SMS_NUMBER, AppConstant.SMS_TEXT);
        } else if (id == R.id.action_messenger) {
            AppUtils.invokeMessenger(mActivity);
        } else if (id == R.id.action_email) {
            AppUtils.sendEmail(mActivity, AppConstant.EMAIL_ADDRESS, AppConstant.EMAIL_SUBJECT, AppConstant.EMAIL_BODY);
        }

        // others
        else if (id == R.id.action_share) {
            AppUtils.shareApp(mActivity);
        } else if (id == R.id.action_rate_app) {
            AppUtils.rateThisApp(mActivity); // this feature will only work after publish the app
        } else if (id == R.id.action_settings) {
            ActivityUtils.getInstance().invokeActivity(mActivity, SettingsActivity.class, false);
        } else if (id == R.id.terms_conditions) {
            ActivityUtils.getInstance().invokeCustomPostAndLink(mActivity, CustomLinkAndPageActivity.class, getResources().getString(R.string.terms), getResources().getString(R.string.terms_url), false);
        } else if (id == R.id.privacy_policy) {
            ActivityUtils.getInstance().invokeCustomPostAndLink(mActivity, CustomLinkAndPageActivity.class, getResources().getString(R.string.privacy), getResources().getString(R.string.privacy_url), false);
        } else if (id == R.id.faq) {
            ActivityUtils.getInstance().invokeCustomPostAndLink(mActivity, CustomLinkAndPageActivity.class, getResources().getString(R.string.faq), getResources().getString(R.string.faq_url), false);
        } else if (id == R.id.action_exit) {
            AppUtils.appClosePrompt(mActivity);
        }

        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }

        return true;

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}