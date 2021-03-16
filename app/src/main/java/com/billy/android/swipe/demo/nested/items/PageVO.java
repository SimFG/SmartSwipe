package com.billy.android.swipe.demo.nested.items;

/**
 * @description:
 * @author: fubang
 * @date: 2021-03-11 16:19
 */
public class PageVO {
    private int color;
    private String title;

    public PageVO(int color, String title) {
        this.color = color;
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public String getTitle() {
        return title;
    }
}
