package com.billy.android.swipe.demo.nested.recycler;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.billy.android.swipe.demo.R;

/**
 * @description:
 * @author: fubang
 * @date: 2021-03-11 16:49
 */
@HolderAnnotation(layoutId = R.layout.item_text)
public class TextViewHolder extends BaseViewHolder<String> {

    private TextView textView;

    public TextViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews() {
        textView = itemView.findViewById(R.id.textview);
    }

    @Override
    public void bindView(String data) {
        textView.setText(data);
    }
}
