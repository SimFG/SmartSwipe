package com.billy.android.swipe.demo.nested.recycler;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @description:
 * @author: fubang
 * @date: 2021-03-11 16:26
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void initViews();

    public abstract void bindView(T data);


}
