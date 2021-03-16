package com.billy.android.swipe.demo.nested;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.billy.android.swipe.demo.nested.items.NestedViewModel;

/**
 * @description:
 * @author: fubang
 * @date: 2021-03-11 14:49
 */
public class NestedScrollLayout extends FrameLayout {
    public NestedScrollLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public NestedScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NestedScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public NestedScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(Context context) {
        mScroller = new OverScroller(context, new Interpolator() {
            @Override
            public float getInterpolation(float t) {
                t -= 1.0f;
                return t * t * t * t * t + 1.0f;
            }
        });
    }

    private View mChildView;
    private RecyclerView mRootList;
    private RecyclerView mChildList;
    private OverScroller mScroller;
    private int mLastY;
    private NestedViewModel mScrollViewModel;

    public void setRootList(RecyclerView recyclerView) {
        mRootList = recyclerView;
    }

    public void setTarget(LifecycleOwner target) {
        if (target instanceof FragmentActivity) {
            mScrollViewModel = ViewModelProviders.of((FragmentActivity) target).get(NestedViewModel.class);
        } else if (target instanceof Fragment) {
            mScrollViewModel = ViewModelProviders.of((Fragment) target).get(NestedViewModel.class);
        } else {
            throw new IllegalArgumentException("target must be FragmentActivity or Fragment");
        }
        mScrollViewModel.getChildView().observe(target, new Observer<View>() {
            @Override
            public void onChanged(@Nullable View view) {
                mChildView = view;
            }
        });
        mScrollViewModel.getChildList().observe(target, new Observer<View>() {
            @Override
            public void onChanged(@Nullable View view) {
                mChildList = (RecyclerView) view;
            }
        });
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public int getNestedScrollAxes() {
        return ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        stopScroller();
        if (mChildView == null) {
            return;
        }

        if (target == mRootList) {
            onParentScrolling(mChildView.getTop(), dy, consumed);
        } else {
            onChildScrolling(mChildView.getTop(), dy, consumed);
        }
    }

    private void stopScroller() {
        mScroller.forceFinished(true);
    }

    private void onParentScrolling(int childTop, int dy, int[] consumed) {
        if (childTop == 0) {
            if (mChildList != null && (dy > 0 || mChildList.canScrollVertically(dy)) ) {
                mChildList.scrollBy(0, dy);
                consumed[1] = dy;
            }
        } else {
//            if (childTop < dy) {
//                consumed[1] = dy - childTop;
//            }
        }
    }

    private void onChildScrolling(int childTop, int dy, int[] consumed) {
        if (childTop == 0) {
            if (dy < 0 && !mChildList.canScrollVertically(dy)) {
                mRootList.scrollBy(0, dy);
                consumed[1] = dy;
            }
        } else {
            if (dy < 0 || childTop > dy) {
                mRootList.scrollBy(0, dy);
                consumed[1] = dy;
            } else {
                mRootList.scrollBy(0, childTop);
                consumed[1] = dy;
            }
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        mLastY = 0;
        mScroller.fling(0, 0, (int) velocityX, (int) velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        invalidate();
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currY = mScroller.getCurrY();
            int dy = currY - mLastY;
            mLastY = currY;
            if (dy != 0) {
                onFling(dy);
            }
            invalidate();
        }
        super.computeScroll();
    }

    private void onFling(int dy) {
        if (mChildView != null) {
            int top = mChildView.getTop();
            if (top == 0) {
                if (dy > 0) {
                    if (mChildList != null && mChildList.canScrollVertically(dy)) {
                        mChildList.scrollBy(0, dy);
                    } else {
                        stopScroller();
                    }
                } else {
                    if (mChildList != null && mChildList.canScrollVertically(dy)) {
                        mChildList.scrollBy(0, dy);
                    } else {
                        mRootList.scrollBy(0, dy);
                    }
                }
            } else {
                if (dy > 0) {
                    if (top > dy) {
                        mRootList.scrollBy(0, dy);
                    } else {
                        mRootList.scrollBy(0, top);
                    }
                } else {
                    if (mRootList.canScrollVertically(dy)) {
                        mRootList.scrollBy(0, dy);
                    } else {
                        stopScroller();
                    }
                }
            }
        } else {
            if (!mRootList.canScrollVertically(dy)) {
                stopScroller();
            } else {
                mRootList.scrollBy(0, dy);
            }
        }
    }
}
