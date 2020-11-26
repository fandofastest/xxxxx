package com.myappforu.wpnews.holders;

/**
 * Created by Ashiq on 4/13/2017.
 */
public interface ItemTouchHelperViewHolder {
    /**
     * Implementations should update the item view to indicate it's active state.
     */
    void onItemSelected();


    /**
     * state should be cleared.
     */
    void onItemClear();
}
