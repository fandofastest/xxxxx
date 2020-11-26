package com.myappforu.wpnews.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdView;
import com.myappforu.wpnews.R;
import com.myappforu.wpnews.adapters.SelectableCategoryAdapter;
import com.myappforu.wpnews.api.http.ApiUtils;
import com.myappforu.wpnews.api.models.category.Category;
import com.myappforu.wpnews.api.params.HttpParams;
import com.myappforu.wpnews.data.constant.AppConstant;
import com.myappforu.wpnews.data.sqlite.SelectableCatDbController;
import com.myappforu.wpnews.listeners.ListItemClickListener;
import com.myappforu.wpnews.listeners.OnCategoryListChangedListener;
import com.myappforu.wpnews.listeners.OnStartDragListener;
import com.myappforu.wpnews.models.SelectableCategoryModel;
import com.myappforu.wpnews.utility.ActivityUtils;
import com.myappforu.wpnews.utility.AdUtils;
import com.myappforu.wpnews.utility.ItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SAIF-MCC on 11/20/2017.
 */

public class HomeCategoriesActivity extends BaseActivity implements OnCategoryListChangedListener,
        OnStartDragListener {

    private Activity mActivity;
    private Context mContext;
    private SelectableCategoryAdapter categoryAdapter;
    private RecyclerView categoriesRecyclerView;
    private int mPerPage = 5;
    private SelectableCatDbController selectableCatDbController;
    private List<Category> categoryList;
    private List<SelectableCategoryModel> selectableCategoryList;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        initListener();
    }

    private void initVar() {
        mActivity = HomeCategoriesActivity.this;
        mContext = mActivity.getApplicationContext();
        categoryList = new ArrayList<>();
        selectableCategoryList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_menu_or_home_cat_list);

        categoriesRecyclerView = (RecyclerView) findViewById(R.id.rv_menus);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        initLoader();

        initToolbar();
        setToolbarTitle(getString(R.string.home_cat_list));
        enableBackButton();

    }

    private void initFunctionality() {
        showLoader();
        loadCategories();

        categoryAdapter = new SelectableCategoryAdapter(mContext, (ArrayList) categoryList, this, this);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(categoryAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(categoriesRecyclerView);
        categoriesRecyclerView.setAdapter(categoryAdapter);

        getAllDatabaseData();

        // show banner ads
        AdUtils.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adView));

    }

    public void initListener() {
        categoryAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Category clickedCategory = categoryList.get(position);

                clickedCategory.setCategorySelected(!clickedCategory.isCategorySelected());
                if (clickedCategory.isCategorySelected()) {
                    selectableCatDbController.insertData(clickedCategory.getID().intValue(), clickedCategory.getName(), position);
                } else {
                    selectableCatDbController.deleteCat(clickedCategory.getID().intValue());
                }
                categoryAdapter.notifyDataSetChanged();
            }
        });
    }

    public void updateUI() {
        // sort categoryList according to category-order
        Comparator<Category> comparator = new Comparator<Category>() {
            @Override
            public int compare(Category left, Category right) {
                return left.getCategoryOrder() - right.getCategoryOrder();
            }
        };

        Collections.sort(categoryList, comparator);
        categoryAdapter.notifyDataSetChanged();
    }

    public void getAllDatabaseData() {
        if (selectableCatDbController == null) {
            selectableCatDbController = new SelectableCatDbController(mContext);
        }
        selectableCategoryList.clear();
        selectableCategoryList.addAll(selectableCatDbController.getAllData());
    }


    public void loadCategories() {
        ApiUtils.getApiInterface().getCategories(mPerPage).enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    int totalPages = Integer.parseInt(response.headers().get(HttpParams.HEADER_TOTAL_PAGE));

                    if (totalPages > 1) {
                        mPerPage = mPerPage * totalPages;
                        loadCategories();

                    } else {
                        List<Category> categories = response.body();
                        if (categories != null && !categories.isEmpty()) {
                            //for (Category category : categories) {
                            for (Iterator<Category> it = categories.iterator(); it.hasNext(); ) {
                                Category category = it.next();

                                if (category.getCount().intValue() > 0) {
                                    // if present category is selected to display at home
                                    //for (SelectableCategoryModel selectableCategoryModel : selectableCategoryList) {

                                    for (Iterator<SelectableCategoryModel> it1 = selectableCategoryList.iterator(); it1.hasNext(); ) {
                                        SelectableCategoryModel selectableCategoryModel = it1.next();

                                        if (selectableCategoryModel.getCategoryId() == category.getID().intValue()) {
                                            category.setCategorySelected(true);
                                            category.setCategoryOrder(selectableCategoryModel.getCategoryOrder());
                                            break;
                                        }
                                    }
                                    if (!category.isCategorySelected()) {
                                        category.setCategoryOrder(AppConstant.MAX_VALUE);
                                    }
                                    categoryList.add(category);
                                }
                            }
                        }

                        updateUI();
                    }

                    hideLoader();
                } else {
                    showEmptyView();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                t.printStackTrace();
                showEmptyView();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ActivityUtils.getInstance().invokeActivity(HomeCategoriesActivity.this, MainActivity.class, true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.getInstance().invokeActivity(HomeCategoriesActivity.this, MainActivity.class, true);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onListChanged(List<Category> categories) {
        //after drag and drop operation, the new list of Categories is passed in here
        getAllDatabaseData();
        for (Category category : categories) {
            // if present category is selected for home screen display
            for (SelectableCategoryModel selectableCategoryModel : selectableCategoryList) {
                if (selectableCategoryModel.getCategoryId() == category.getID().intValue()) {
                    selectableCatDbController.updateCategoryOrder(selectableCategoryModel.getId(), categories.indexOf(category));
                    break;
                }
            }
        }
    }
}
