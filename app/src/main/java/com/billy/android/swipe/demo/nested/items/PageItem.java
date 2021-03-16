package com.billy.android.swipe.demo.nested.items;

import java.util.List;

/**
 * @description:
 * @author: fubang
 * @date: 2021-03-11 16:21
 */
public class PageItem implements AdapterItem<List<PageVO>> {

    private List<PageVO> model;

    public PageItem(List<PageVO> model) {
        this.model = model;
    }

    @Override
    public List<PageVO> getDataModel() {
        return model;
    }

    @Override
    public int getViewType() {
        return ViewType.TYPE_PAGER;
    }
}
