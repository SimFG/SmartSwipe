package com.billy.android.swipe.demo.nested.items;

/**
 * @description:
 * @author: fubang
 * @date: 2021-03-11 16:21
 */
public interface AdapterItem<T> {
    T getDataModel();

    int getViewType();
}
