package com.billy.android.swipe.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

/**
 * @description:
 * @author: fubang
 * @date: 2021-03-10 20:15
 */
public class SlidingpanelActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_panel);
        SlidingPaneLayout layout = findViewById(R.id.sliding_layout);
        final View leftView = layout.getChildAt(0);
        layout.setPanelSlideListener(new SlidingPaneLayout.SimplePanelSlideListener(){
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
//                leftView.setPivotX(-leftView.getWidth() / 6.0f);
//                leftView.setPivotY(leftView.getHeight() / 2.0f);
                leftView.setPivotX(0f);
                leftView.setScaleX(0.7f + 0.3f * slideOffset);
                leftView.setScaleY(0.7f + 0.3f * slideOffset);
            }
        });
    }
}
