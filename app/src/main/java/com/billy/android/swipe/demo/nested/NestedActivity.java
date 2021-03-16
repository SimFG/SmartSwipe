package com.billy.android.swipe.demo.nested;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.billy.android.swipe.demo.R;
import com.billy.android.swipe.demo.nested.items.AdapterItem;
import com.billy.android.swipe.demo.nested.items.NestedViewModel;
import com.billy.android.swipe.demo.nested.items.PageItem;
import com.billy.android.swipe.demo.nested.items.PageVO;
import com.billy.android.swipe.demo.nested.items.ParentItem;
import com.billy.android.swipe.demo.nested.items.ViewType;
import com.billy.android.swipe.demo.nested.recycler.BaseAdapter;
import com.billy.android.swipe.demo.nested.recycler.BaseViewHolder;
import com.billy.android.swipe.demo.nested.recycler.ImageViewHolder;
import com.billy.android.swipe.demo.nested.recycler.PagerViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: fubang
 * @date: 2021-03-11 16:15
 */
public class NestedActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private RecyclerView.Adapter adapter;

    private NestedViewModel viewModel;

    private NestedScrollLayout container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_nested_1);
        container = findViewById(R.id.rootview);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        container.setRootList(recyclerView);
        container.setTarget(this);
        initAdapter();
        viewModel = ViewModelProviders.of(this).get(NestedViewModel.class);
        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = container.getMeasuredHeight();
                viewModel.getPagerHeight().setValue(height);
            }
        });
    }

    private void initAdapter() {
        SparseArray<Class<? extends BaseViewHolder>> viewHolders = new SparseArray<>();
        viewHolders.put(ViewType.TYPE_PARENT, ImageViewHolder.class);
        viewHolders.put(ViewType.TYPE_PAGER, PagerViewHolder.class);
        int[] ids = new int[]{R.mipmap.pic1, R.mipmap.pic2, R.mipmap.pic3, R.mipmap.pic4, R.mipmap.pic5};
        List<AdapterItem> itemList = new ArrayList<>();
        for (int id : ids) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
            itemList.add(new ParentItem(bitmap));
        }
        List<PageVO> pageList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            pageList.add(new PageVO(Color.WHITE, "tab" + i));
        }
        itemList.add(new PageItem(pageList));
        adapter = new BaseAdapter(itemList, this, viewHolders);
        recyclerView.setAdapter(adapter);
    }
}
