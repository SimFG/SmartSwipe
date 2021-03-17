# SmartSwipe

项目原地址：[luckybilly/SmartSwipe](https://github.com/luckybilly/SmartSwipe)

**以下方案均为个人理解，存在错误望指正。**

- [ 分析](#head1)
	- [StretchConsumer - 滑动机制详解](#head2)
		- [ 前言](#head3)
		- [ 事件流程分析](#head4)
	- [ DrawerConsumer](#head5)
		- [ 布局加载](#head6)
		- [ ScrimView](#head7)
- [ 疑问](#head8)
	- [ Q1：StretchConsumerActivity一直往下滑，整个列表拉伸，如果最后停留在屏幕上一段时间不动拉伸效果不会复原](#head9)
	- [ Q2：SmartSwipeWrapper中onNestedPreScroll方法，消耗了x和y方向的距离为什么是减](#head10)
	- [ Q3：为什么DecorView的第一个子类是SmartSwipeWrapper呢？](#head11)
	- [ Q4：调试SmartSwipe-在StretchConsumerActivity中，点击事件最开始会调用onStartNestedScroll方法三次](#head12)
	- [ Q5：DrawerConsumer中的tryAcceptMoving方法为什么要对mCachedSwipeDistanceX和mCachedSwipeDistanceY做判零处理](#head13)
# <span id="head1"> 分析</span>
## <span id="head2">StretchConsumer - 滑动机制详解</span>
### <span id="head3"> 前言</span>
为了分析滑动机制，建议把demo中的MyApp中SmartSwipeBack部分代码进行注释；阅读以下内容，建议对于以下文章进行阅读，包括了：
- ViewDragerHelp源码分析：<https://www.cnblogs.com/lqstayreal/p/4500219.html>
- NestedScrolling解析：
    - <https://www.jianshu.com/p/eeae65885043>
    - <https://www.jianshu.com/p/20efb9f65494>
    - <https://blog.csdn.net/King1425/article/details/61915758/>
- SmartSwipe介绍：<https://juejin.im/post/5d3fdc3af265da03c02bdbde>
### <span id="head4"> 事件流程分析</span>
事件流程图

<img src="https://user-images.githubusercontent.com/21985684/111441123-fb14cb00-8741-11eb-888a-95b253f82f1d.png" width="50%" height="50%">

可以发现，SmartSwipeWrapper中的onTouch没有执行，这个原因是因为RecyclerView被拦截了，是通过NestedScrolling将事件发送到SmartSwipeWrapper，可参考文章：<https://www.jianshu.com/p/a4dc607cd08f>

正常滑动的时候，滑动距离被RecyclerView消耗，当滑动到顶部接着往下滑或者滑动到底部接着往上滑，这时候，RecyclerView不消耗这个事件，那么这时候距离就会通过NestedScrolling机制传递到SmartSwipeWrapper中。

也就是说：在StretchConsumerActivity中，其实SmartSwipeWrapper的onInterceptTouchEvent和onTouchEvent两个方法其实都是没有作用的，自然也就可以分析出mHelper的赋值地方，也就是第一次RecyclerView不消耗触摸事件的时候，在onNestedScroll方法中调用wrapperNestedScroll()进行赋值的。

## <span id="head5"> DrawerConsumer</span>
这个实现的效果就是androidx中的DrawerLayout的效果。
### <span id="head6"> 布局加载</span>
DrawerLayout实现的效果就是可以在边缘中弹出一个View。对于这部分，弹出的View是如何在这个框架中进行加载的呢，见下图：

<img src="https://user-images.githubusercontent.com/21985684/111445586-81331080-8746-11eb-9f57-cda9316e6b24.png" width="50%" height="50%">

从上面途中可以看出，initChildrenFormXml()这个方法只有在xml中使用SmartSwipeWrapper才会执行，代码中执行相同逻辑在setXxxDrawerView()方法。
### <span id="head7"> ScrimView</span>
这个View其实就是滑动的时候出现的阴影，在DrawerConsumer类中，有一个mShowScrimAndShadowOutsideContentView的boolean类型的属性，这个属性是控制边缘阴影在哪一边，为true的时候，也就是黑色阴影在整个内容View上，false的时候表示黑色阴影在滑出View上。

# <span id="head8"> 疑问</span>
## <span id="head9"> Q1：StretchConsumerActivity一直往下滑，整个列表拉伸，如果最后停留在屏幕上一段时间不动拉伸效果不会复原</span>
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

## <span id="head10"> Q2：SmartSwipeWrapper中onNestedPreScroll方法，消耗了x和y方向的距离为什么是减</span>
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

## <span id="head11"> Q3：为什么DecorView的第一个子类是SmartSwipeWrapper呢？</span>
这个是在MyApp中onCreate中进行注册的，即SmartSwipeBack#activityBezierBack。

## <span id="head12"> Q4：调试SmartSwipe-在StretchConsumerActivity中，点击事件最开始会调用onStartNestedScroll方法三次</span>
- 事件发起是在RecyclerView中，onInterceptTouchEvent
- 第一次进入onStartNestedScroll，是RecyclerView的父类
- 其实当前布局中存在两个SmartSwipeWrapper，一个是RecyclerView的父类，另外一个是DecorView的第一个子类
- 这个方法会进入最后的handle为true，会调用startNestedScroll，这个是第二次进入onStartNestedScroll
- 最后一次进入是因为在第一次onStartNestedScroll方法返回了true，调用的onNestedScrollAccepted方法，然后导致了第三次进入onStartNestedScroll

## <span id="head13"> Q5：DrawerConsumer中的tryAcceptMoving方法为什么要对mCachedSwipeDistanceX和mCachedSwipeDistanceY做判零处理</span>
看了下没什么用，因为赋值的地方只有SwipeConsumer的onSwipeAccepted这个方法，但是这个方法在该类被重写了，也就是这两个变量在这个类中一直为零。
```
@Override
public boolean tryAcceptMoving(int pointerId, float downX, float downY, float dx, float dy) {
    boolean handle = super.tryAcceptMoving(pointerId, downX, downY, dx, dy);
    if (handle && mCachedSwipeDistanceX == 0 && mCachedSwipeDistanceY == 0) { 
        if (mDrawerViewRequired && getDrawerView(mDirection) == null) { 
            handle = false;
        }
    }
    return handle;
//        return handle && (!mDrawerViewRequired || getDrawerView(mDirection) != null);
}
```

同理rawerConsumer中的onSwipeAccepted方法对于这两个变量判断是否为零也是没有作用，if中的语句一定会被执行。
```
@Override
public void onSwipeAccepted(int activePointerId, boolean settling, float initialMotionX, float initialMotionY) {
    if (mCachedSwipeDistanceX == 0 && mCachedSwipeDistanceY == 0) {
        changeDrawerViewVisibility(INVISIBLE);
        mCurDrawerView = getDrawerView(mDirection);
        changeDrawerViewVisibility(VISIBLE);
    }
    int w = mWidth;
    int h = mHeight;
    if (mCurDrawerView != null) {
        w = mCurDrawerView.getMeasuredWidth();
        h = mCurDrawerView.getMeasuredHeight();
    } else if (mDrawerViewRequired) {
        return;
    }
    if (!mOpenDistanceSpecified) { // TODO 不调用setOpenDistance这个值都是false
        if ((mDirection & DIRECTION_HORIZONTAL) > 0) {
            mOpenDistance = w;
        } else {
            mOpenDistance = h;
        }
    }
    calculateDrawerDirectionInitPosition(mDirection, w, h);
    changeDrawerViewVisibility(VISIBLE); // TODO 语句和上面重复了
    initScrimView();
    layoutChildren();
    orderChildren();
    super.onSwipeAccepted(activePointerId, settling, initialMotionX, initialMotionY);
}
```

