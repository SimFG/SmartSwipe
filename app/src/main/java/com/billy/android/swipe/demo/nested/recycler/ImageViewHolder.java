package com.billy.android.swipe.demo.nested.recycler;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.billy.android.swipe.demo.R;

/**
 * @description:
 * @author: fubang
 * @date: 2021-03-11 16:30
 */
@HolderAnnotation(layoutId = R.layout.item_parent_holder)
public class ImageViewHolder extends BaseViewHolder<Bitmap> {

    private ImageView imageView;

    private Bitmap bitmap;

    public ImageViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews() {
        imageView = itemView.findViewById(R.id.imageview);
    }

    @Override
    public void bindView(Bitmap data) {
        if (bitmap == data) {
            return;
        }
        bitmap = data;
        imageView.setImageBitmap(bitmap);
    }
}
