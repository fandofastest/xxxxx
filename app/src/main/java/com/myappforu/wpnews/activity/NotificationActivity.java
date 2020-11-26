package com.myappforu.wpnews.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.myappforu.wpnews.R;
import com.myappforu.wpnews.adapters.NotificationAdapter;
import com.myappforu.wpnews.data.sqlite.NotificationDbController;
import com.myappforu.wpnews.listeners.ListItemClickListener;
import com.myappforu.wpnews.models.NotificationModel;
import com.myappforu.wpnews.utility.ActivityUtils;
import com.myappforu.wpnews.utility.AdUtils;
import com.myappforu.wpnews.utility.DialogUtils;

import java.util.ArrayList;

public class NotificationActivity extends BaseActivity {

    private Context mContext;
    private Activity mActivity;

    private RecyclerView rvNotification;
    private NotificationAdapter mAdapter;
    private ArrayList<NotificationModel> notificationList;

    private NotificationDbController notificationController;
    private TextView tvClearAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = NotificationActivity.this;
        mContext = mActivity.getApplicationContext();

        initVars();
        initialView();
        loadNotifications();
        initialListener();

        // Show full screen ad
        AdUtils.getInstance(mContext).showFullScreenAd();
    }

    private void initVars() {
        notificationList = new ArrayList<>();
        notificationController = new NotificationDbController(mContext);
    }

    private void initialView() {
        setContentView(R.layout.activity_notification);

        //productList
        rvNotification = (RecyclerView) findViewById(R.id.recycler_view);
        tvClearAll = findViewById(R.id.tvClearAll);

        mAdapter = new NotificationAdapter(mActivity, notificationList);
        rvNotification.setLayoutManager(new LinearLayoutManager(mActivity));
        rvNotification.setAdapter(mAdapter);

        initLoader();

        initToolbar();
        setToolbarTitle(getString(R.string.notifications));
        enableBackButton();
    }

    private void initialListener() {

        mAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(final int position, View view) {
                notificationController.updateStatus(notificationList.get(position).getId(), true);

                switch (view.getId()) {
                    case R.id.ivRemoveNotification:
                        // Remove notification from list

                        DialogUtils.showDialogPrompt(mActivity, null, getString(R.string.delete_notify_msg), getString(R.string.yes), getString(R.string.no), true, new DialogUtils.DialogActionListener() {
                            @Override
                            public void onPositiveClick() {
                                notificationController.deleteNotification(notificationList.get(position).getId());
                                loadNotifications();
                            }
                        });

                        break;
                    default:

                        ActivityUtils.getInstance().invokeNotificationDetails(mActivity, notificationList.get(position).getTitle(),
                                notificationList.get(position).getMessage(), notificationList.get(position).getPostId());

                        break;
                }
            }
        });

        tvClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notificationList.size()> 0) {
                    DialogUtils.showDialogPrompt(mActivity, null, getString(R.string.delete_all_notify_msg), getString(R.string.yes), getString(R.string.no), true, new DialogUtils.DialogActionListener() {
                        @Override
                        public void onPositiveClick() {
                            notificationController.deleteAllNotification();
                            loadNotifications();
                        }
                    });
                }
                else {
                    Toast.makeText(mContext, R.string.empty_list, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadNotifications(){

        showLoader();

        if (!notificationList.isEmpty()){
            notificationList.clear();
        }
        notificationList.addAll(notificationController.getAllData());
        hideLoader();

        mAdapter.notifyDataSetChanged();

        if (notificationList.isEmpty()) {
            showEmptyView();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        // Show banner ad
        AdUtils.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adView));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
