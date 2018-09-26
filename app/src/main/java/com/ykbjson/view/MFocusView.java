package com.ykbjson.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

/**
 * com.ykbjson.view.MFocusView
 *
 * @author Kebin.Yan
 * @Description 子view获得焦点时焦点框有动画效果的视图容器
 * @date Create At :2015年7月2日 上午9:01:37
 */
public class MFocusView extends RelativeLayout implements OnFocusChangeListener, OnClickListener {
    private final String TAG = getClass().getSimpleName();

    /**
     * 视图大小
     */
    final private int focusSize = 300;
    final private int otherSize = 240;

    /**
     * 焦点框
     */
    private FloatView floatView;
    /**
     * 是否初始化过
     */
    private boolean isInit;

    public MFocusView(Context context) {
        this(context, null);
    }

    public MFocusView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public MFocusView(Context context, AttributeSet attr, int style) {
        super(context, attr, style);
    }

    /**
     * 初始化焦点框
     */
    @SuppressLint("ResourceType")
    private void initFloatView() {
        final View child = getChildAt(0); //获取ChildView[1](此应用即为第二个button)的位置

        if (null != child) {
            /* 更改选择view的大小 */
            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            //Log.i(TAG, "lp.width  " + lp.width + "lp.height" + lp.height);

            lp.width=focusSize;
            lp.height=focusSize;
            lp.rightMargin=-10;

            child.setLayoutParams(lp);
            //child.requestLayout();

            //child.offsetLeftAndRight(-50);//正数向右，负数向左
            //child.offsetTopAndBottom(-50);//正数向下，负数向上

            int firstChildWidth = lp.width;
            int firstChildHeight = lp.height;
            int firstChildLeft = child.getRight()-firstChildWidth;
            int firstChildTop = child.getBottom()-firstChildHeight;

            /* 实例化一个ImageView对象 */
            floatView = new FloatView(getContext());
            /* 根据获取第一个子view的宽和高生成参数 */
            LayoutParams floatParams = new LayoutParams(firstChildWidth, firstChildHeight);
            /* 定义该view的ID */
            floatView.setId(30016);
            /* 根据配置好的参数设置view */
            floatView.setLayoutParams(floatParams);
            /* 能否通过光标移动获取焦点 */
            floatView.setFocusable(false);
            /* 能否通过触摸屏点击获取焦点 */
            floatView.setFocusableInTouchMode(false);
            /* 不按比例拉伸图片以填充view */
            floatView.setScaleType(ScaleType.FIT_XY);
            /* 设置图片资源 */
            floatView.setImageResource(R.drawable.focus_bound);
            /* 添加子view */
            addView(floatView);

            floatView.onReLayout(firstChildLeft, firstChildTop, firstChildWidth, firstChildHeight);

            /* 等待参数更新，延时执行 */
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    child.performClick();//execute the task
                }
            }, 3);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !isInit) {
            isInit = !isInit;

            /* 设置监听移动 */
            if (getChildCount() > 0) {
                Log.i(TAG, "onLayout,childCount :  " + getChildCount());
                for (int i = 0; i < getChildCount(); i++) {
                    /* 触屏点击时的监听 */
                    getChildAt(i).setOnClickListener(this);
                    /* 焦点移动时的监听 */
                    getChildAt(i).setOnFocusChangeListener(this);
                }
            }

            /**
             * 初始化视图框
             * */
            initFloatView();
        }
    }

    @Override
    public void onClick(View child) { //button被点击时调用
        onChildChange(child);
    }


    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        Log.w(TAG, "onFocusChanged,gainFocus :  " + gainFocus + " direction : " + direction);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        onChildChange();
    }

    @Override
    public void onFocusChange(View child, boolean hasFocus) { //焦点移动时调用
        Log.i(TAG, "onFocusChanged,hasFocus :  " + hasFocus);
        if (!hasFocus)
            return;
        onChildChange(child);
    }

    /**
     * 焦点框视图改变
     */
    private void onChildChange() {
        View child = findFocus();
        Log.i(TAG, "onChildChange(),child:"+child);
        onChildChange(child);
    }

    /**
     * 焦点框视图改变
     *
     * @param child 当前右焦有的视图
     */
    private void onChildChange(final View child) {
        if (null == child)
            return;

        /* 更改选择view的大小 */
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if(lp.width!=focusSize || lp.height!=focusSize){
            lp.width=focusSize;
            lp.height=focusSize;
            lp.leftMargin=-10;
            lp.rightMargin=-10;
            child.setLayoutParams(lp);

            /* 将其他的视图置小,最后一个child view是飘框 */
            if (getChildCount() > 1) {
                for (int i = 0; i < getChildCount()-1; i++) {
                    if(getChildAt(i).getId() != child.getId()){
                        LayoutParams otherLp = (LayoutParams) getChildAt(i).getLayoutParams();
                        otherLp.width=otherSize;
                        otherLp.height=otherSize;
                        otherLp.leftMargin=0;
                        otherLp.rightMargin=0;
                        getChildAt(i).setLayoutParams(otherLp);
                        Log.i(TAG, "getChildCount():"+i);
                    }
                }
            }

            //child.requestLayout();

            /* 模拟再次点击按钮更新坐标 */
            //child.performClick();

            /* 等待参数更新，延时执行 */
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    child.performClick();//execute the task
                }
            }, 1);
            return;
        }

        /* 根据当前子view的坐标、宽、高更新视图框 */
        float focusX = child.getLeft();
        float focusY = child.getTop();
        float focusW = child.getRight() - focusX;
        float focusH = child.getBottom() - focusY;

        floatView.onReLayout(focusX, focusY, focusW, focusH);

        Log.i(TAG, "onChildChange(View child),child:"+child+"- X"+focusX+"|Y"+focusY);
    }
}

