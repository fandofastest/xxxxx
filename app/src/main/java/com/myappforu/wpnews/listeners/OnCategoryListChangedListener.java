package com.myappforu.wpnews.listeners;


import com.myappforu.wpnews.api.models.category.Category;

import java.util.List;

/**
 * Created by Ashiq on 4/13/2017.
 */
public interface OnCategoryListChangedListener {
    void onListChanged(List<Category> categories);
}
