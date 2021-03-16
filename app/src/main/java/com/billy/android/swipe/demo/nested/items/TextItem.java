package com.billy.android.swipe.demo.nested.items;

/**
 * @description:
 * @author: fubang
 * @date: 2021-03-11 16:23
 */
public class TextItem implements AdapterItem<String> {

    private String text;

    public TextItem(String text) {
        this.text = text;
    }

    @Override
    public String getDataModel() {
        return text;
    }

    @Override
    public int getViewType() {
        return ViewType.TYPE_TEXT;
    }
}
