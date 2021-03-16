package com.billy.android.swipe.demo.nested.recycler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @description:
 * @author: fubang
 * @date: 2021-03-11 16:28
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HolderAnnotation {
    int layoutId();
}
