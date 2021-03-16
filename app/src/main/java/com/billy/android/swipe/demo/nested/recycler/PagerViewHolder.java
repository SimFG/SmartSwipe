package com.billy.android.swipe.demo.nested.recycler;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.billy.android.swipe.demo.R;
import com.billy.android.swipe.demo.nested.items.NestedViewModel;
import com.billy.android.swipe.demo.nested.items.PageVO;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

/**
 * @description:
 * @author: fubang
 * @date: 2021-03-11 17:01
 */
@HolderAnnotation(layoutId = R.layout.item_pager)
public class PagerViewHolder extends BaseViewHolder<List<PageVO>> {

    private ViewPager viewPager;

    private TabLayout tabLayout;

    private PagerAdapter pagerAdapter;

    private List<PageVO> model;

    private NestedViewModel viewModel;

    private Observer<Integer> observer = new Observer<Integer>() {
        @Override
        public void onChanged(Integer height) {
            if (height != null) {
                itemView.getLayoutParams().height = height;
                itemView.requestLayout();
            }
        }
    };

    public PagerViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews() {
        viewPager = itemView.findViewById(R.id.viewpager);
        tabLayout = itemView.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager, true);
        viewPager.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                viewPager.requestLayout();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });

        itemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                if (viewModel != null) {
                    viewModel.getChildView().setValue(itemView);
                }
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                if (viewModel != null) {
                    viewModel.getChildView().setValue(null);
                }
            }
        });
    }

    @Override
    public void bindView(List<PageVO> data) {
        if (model == data) {
            return;
        }
        model = data;
        Context context = itemView.getContext();
        if (context instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) context;
            pagerAdapter = new SubPagerAdapter(fragmentActivity.getSupportFragmentManager(), model);
            viewPager.setAdapter(pagerAdapter);
            pagerAdapter.notifyDataSetChanged();
            viewModel = ViewModelProviders.of(fragmentActivity).get(NestedViewModel.class);
            viewModel.getPagerHeight().removeObserver(observer);
            viewModel.getPagerHeight().observe(fragmentActivity, observer);
            if (viewModel.getPagerHeight().getValue() != null)
                itemView.getLayoutParams().height = viewModel.getPagerHeight().getValue();
        }
    }
}
