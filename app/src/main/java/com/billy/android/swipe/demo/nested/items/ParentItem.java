package com.billy.android.swipe.demo.nested.items;

import android.graphics.Bitmap;

/**
 * @description:
 * @author: fubang
 * @date: 2021-03-11 16:22
 */
public class ParentItem implements AdapterItem<Bitmap> {
    private Bitmap bitmap;

    public ParentItem(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public Bitmap getDataModel() {
        return bitmap;
    }

    @Override
    public int getViewType() {
        return ViewType.TYPE_PARENT;
    }
}
