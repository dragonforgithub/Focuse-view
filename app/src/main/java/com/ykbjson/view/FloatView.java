package com.ykbjson.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

/**
 * com.ykbjson.view.FloatView
 *
 * @author Kebin.Yan
 * @Description 焦点框视图
 * @date Create At :2015年7月2日 上午9:02:14
 */
@SuppressLint("AppCompatCustomView")
public class FloatView extends ImageView {
    private final String TAG = getClass().getSimpleName();
    /**
     * x轴缩放率
     */
    private float scalX = 1.0f;
    /**
     * y轴缩放率
     */
    private float scalY = 1.0f;
    /**
     * 上一次view的x坐标
     */
    public float nFocusX;
    /**
     * 上一次view的y坐标
     */
    public float nFocusY;
    /**
     * 上一次view的宽度
     */
    public int nFocusW;
    /**
     * 上一次view的高度
     */
    public int nFocusH;

    public FloatView(Context context) {
        super(context);
    }

    /**
     * 改变当前视图位置和大小
     *
     * @param focusX 新的x坐标
     * @param focusY 新的y坐标
     * @param focusW 新的宽度
     * @param focusH 新的高度
     */
    public void onReLayout(final float focusX, final float focusY, final float focusW, final float focusH) {
        /* 计算缩放比例 */
        scalX = focusW / getWidth();
        scalY = focusH / getHeight();
        /**
        ObjectAnimatior : 允许你直接更改View的新属性;
        ValueAnimatior  : 只是定义和执行动画流程，并没有实现任何的动画逻辑，
                          需要添加动画更新的监听，在执行过程中进行自定义的动画逻辑;
        */
        ValueAnimator valueAnimator = new ValueAnimator();
        /* 实例化PropertyValueHolder对象,并传入参数 */
        valueAnimator.setObjectValues(new LayoutParams(nFocusW, nFocusH));
        /* 添加线性插值器 */
        valueAnimator.setInterpolator(new LinearInterpolator());
        /* 添加估值器,实现渐变过程 */
        valueAnimator.setEvaluator(new TypeEvaluator<LayoutParams>() {
            @Override
            //重写TypeEvaluator返回所需的数值类型
            //参数：fraction（开始与结束的之间部分的数值比例，范围在0-1之间）
            //startValue：起始值 endValue结束值
            public LayoutParams evaluate(float fraction, LayoutParams startValue, LayoutParams endValue) {
                Log.d(TAG, "evaluate , fraction[0~1] = " + fraction);
                LayoutParams params = new LayoutParams(0, 0);
                params.width = (int) ((focusW - getWidth()) * fraction);
                params.height = (int) ((focusH - getHeight()) * fraction);
                return params;
            }
        });

        //添加更新监听事件
        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //获取每次变化的数值
                LayoutParams nParams = (LayoutParams) animation.getAnimatedValue();
                LayoutParams params = (LayoutParams) getLayoutParams();
                params.width += nParams.width;
                params.height += nParams.height;
                setLayoutParams(params);
            }
        });

        //延迟进行缩放，可根据setDuration的值设置合适的时间就在移动到对应位置看到缩放变化
        valueAnimator.setStartDelay(100);

        //用于控制一组动画的执行：线性，一起，每个动画的先后执行等
        AnimatorSet set = new AnimatorSet();
        //设置动画同时作用([ObjectAnimator]边移动 [valueAnimator]边缩放)：X坐标，Y坐标，缩放
        set.playTogether(ObjectAnimator.ofFloat(this, "translationX", nFocusX, focusX), ObjectAnimator.ofFloat(this, "translationY", nFocusY, focusY), valueAnimator);
        //设置变化过程时间长度
        set.setDuration(200);
        set.setTarget(this);
        set.start();

        nFocusX = focusX;
        nFocusY = focusY;
        nFocusW = (int) focusW;
        nFocusH = (int) focusH;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "onDraw " + " scalX : " + scalX + " scalY : " + scalY + " nFocusX : " + nFocusX + " nFocusY : " + nFocusY);
        super.onDraw(canvas);
    }
}
