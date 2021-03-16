# SmartSwipe

项目原地址：[luckybilly/SmartSwipe](https://github.com/luckybilly/SmartSwipe)

**以下方案均为个人理解，存在错误望指正。**

## Q1：StretchConsumerActivity一直往下滑，整个列表拉伸，如果最后停留在屏幕上一段时间不动拉伸效果不会复原
原因：SmartSwipeWrapper中wrapperNestedScroll方法将mHelp置为空了
```
mHelper.nestedScrollingDrag(dxUnconsumed, dyUnconsumed, consumed, false);
if (consumer.getProgress() >= maxProgress || consumer.getProgress() <= 0) {
    mHelper = null;
}
```
同时也因为这个置空操作在触摸动作完成时不会调用mHelper.nestedScrollingRelease()，导致SwipeConsumer中onSwipeReleased回调方法不会被执行
```
public void onStopNestedScroll(View target, int type) {
    mNestedInProgress = false;
    helperOnStopNestedScroll(target, type);
    if (type == mCurNestedType) {
        mCurNestedType = NESTED_TYPE_INVALID;
        if (mHelper != null) {
            mHelper.nestedScrollingRelease();
        }
    }
}
```
直接将整个置空操作删除，试了以下没有啥问题

## Q2：SmartSwipeWrapper中onNestedPreScroll方法，消耗了x和y方向的距离为什么是减
```
public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {
    int consumedX = 0, consumedY = 0;
    boolean helperConsumed = false;
    if (mHelper == null || mHelper.getSwipeConsumer().getProgress() == 0) {
        helperConsumed = true;
        Arrays.fill(consumed, 0);
        helperOnNestedPreScroll(target, dx, dy, consumed, type);
        consumedX += consumed[0];
        consumedY += consumed[1];
    }
    if (mHelper != null && mHelper.getSwipeConsumer().getDirection() != DIRECTION_NONE) {
        Arrays.fill(consumed, 0);
        wrapperNestedScroll(dx - consumedX, dy - consumedY, consumed, type);
        consumedX -= consumed[0]; // TODO 为什么这个是减而不是加
        consumedY -= consumed[1];
    }
    if (!helperConsumed) {
        Arrays.fill(consumed, 0);
        helperOnNestedPreScroll(target, dx - consumedX, dy - consumedY, consumed, type);
        consumedX += consumed[0];
        consumedY += consumed[1];
        Log.d("fubang", "3: " + consumed[1]);
    }
    consumed[0] = consumedX;
    consumed[1] = consumedY;
}
```
原因：其实是因为SmartSwipeWrapper中wrapperNestedScroll方法，在使用dx和dy的时候，对其添加了一个负号，我猜这个可能是因为原作者当时写了很多，emmmm，然后发现距离没考虑正负号导致的。
```
mHelper.nestedScrollingDrag(-dxUnconsumed, -dyUnconsumed, consumed, false);
if (consumer.getProgress() >= maxProgress || consumer.getProgress() <= 0) {
    mHelper = null;
}
```
纠正dy，可以参考分支：fix_consumed_y，dx也是类似
