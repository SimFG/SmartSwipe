package com.billy.android.swipe.demo.nested.items;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @description:
 * @author: fubang
 * @date: 2021-03-11 16:52
 */
public class NestedViewModel extends ViewModel {

    private MutableLiveData<Integer> pagerHeight;

    private MutableLiveData<View> childView;

    private MutableLiveData<RecyclerView> childList;

    public MutableLiveData<Integer> getPagerHeight() {
        if (pagerHeight == null) {
            pagerHeight = new MutableLiveData<>();
        }
        return pagerHeight;
    }

    public MutableLiveData<View> getChildView() {
        if (childView == null) {
            childView = new MutableLiveData<>();
        }
        return childView;
    }

    public MutableLiveData<RecyclerView> getChildList() {
        if (childList == null) {
            childList = new MutableLiveData<>();
        }
        return childList;
    }
}

