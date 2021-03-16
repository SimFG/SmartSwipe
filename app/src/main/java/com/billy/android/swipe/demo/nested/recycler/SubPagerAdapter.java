package com.billy.android.swipe.demo.nested.recycler;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.billy.android.swipe.demo.nested.items.PageVO;

import java.util.List;

/**
 * @description:
 * @author: fubang
 * @date: 2021-03-11 17:05
 */
public class SubPagerAdapter extends FragmentStatePagerAdapter {

    private List<PageVO> itemList;

    public SubPagerAdapter(FragmentManager fm, List<PageVO> itemList) {
        super(fm);
        this.itemList = itemList;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new SubFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("color", itemList.get(position).getColor());
        bundle.putInt("position",position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return itemList == null ? 0 : itemList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return itemList.get(position).getTitle();
    }
}