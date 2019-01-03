package com.eagletsoft.yotaassistant.assistant;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.eagletsoft.yotaassistant.utils.DualDisplayManager;
import com.eagletsoft.yotaassistant.R;

public class AssistantHelper {

    // 定义浮动窗口布局
    private LinearLayout mFloatLayout;
    private WindowManager.LayoutParams wmParams;
    // 创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;

    private ImageButton mFloatView;

    private Context context;
    public void create(Context context) {
        this.context = context;

        this.createFloatView();
    }

    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();

        // 通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = DualDisplayManager.getInstance().getActiveWindowManager(context);
        // 设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        // 设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 调整悬浮窗显示的停靠位置为右侧底部
        wmParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 400;
        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(context);
        // 获取浮动窗口视图所在布局

        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.circlemenu, null);
        // 添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        // 浮动窗口按钮
        mFloatView = (ImageButton) mFloatLayout.findViewById(R.id.alert_window_imagebtn);

        /*
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
*/
        afterDrag(mFloatView);
        // 设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            private static final int MAX_DISTANCE_FOR_CLICK = 5;
            private static final int MAX_DOUBLE_CLICK_INTERVAL = 500;

            private float downX;
            private float downY;

            private float rawX;
            private float rawY;

            private long firstDown;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        beginDrag(v);

                        //myCountDownTimer.cancel();
                        downX = event.getX();
                        downY = event.getY();
                        rawX = event.getRawX();
                        rawY = event.getRawY();

                        if (firstDown > 0 && System.currentTimeMillis() - firstDown <= MAX_DOUBLE_CLICK_INTERVAL) {

                        }
                        else {
                            firstDown = System.currentTimeMillis();
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:
                        // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        int distanceX = (int) (event.getRawX() - rawX);
                        int distanceY = (int) (event.getRawY() - rawY);

                        wmParams.x = wmParams.x - distanceX;
                        wmParams.y = wmParams.y - distanceY;
                        // 刷新
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        rawX = event.getRawX();
                        rawY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float tempX = (int) event.getX();
                        float tempY = (int) event.getY();

                        if (Math.abs(tempX - downX) > MAX_DISTANCE_FOR_CLICK || Math.abs(tempY - downY) > MAX_DISTANCE_FOR_CLICK) {

                        }
                        else {
                            this.onClick();
                        }
                        afterDrag(v);
                }
                return false;
            }

            private void onClick() {
                AssistantMenuLauncher.getInstance().show(context, wmParams.x, wmParams.y);
            }
        });
    }

    private ValueAnimator translateA = null;
    private ValueAnimator alphaA = null;
    private static final int YAXIS_ATTACH = 200;

    private void beginDrag(View v) {
        if (null != translateA) {
            translateA.cancel();
        }
        if (null != alphaA) {
            alphaA.cancel();
        }
        v.setAlpha(1.0f);
    }

    private void afterDrag(final View v) {
        DisplayMetrics metric = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metric);

        int tempX = wmParams.x;
        int tempY = wmParams.y;

        int newX = tempX;
        int newY = tempY;

        if (tempY < YAXIS_ATTACH) {
            newY = 0;
        }
        else if (tempY + YAXIS_ATTACH + mFloatLayout.getHeight() > metric.heightPixels) {
            newY = (int)(metric.heightPixels - mFloatLayout.getHeight());
        }
        else {
            if (tempX < (metric.widthPixels - mFloatLayout.getWidth()) / 2) {
                newX = 0;
            }
            else {
                newX = (int)(metric.widthPixels - mFloatLayout.getWidth());
            }
        }

        Point startPoint = new Point(tempX, tempY);
        Point endPoint = new Point(newX, newY);
        translateA = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
        translateA.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Point currentPoint = (Point) animation.getAnimatedValue();
                wmParams.x = currentPoint.x;
                wmParams.y = currentPoint.y;
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
            }
        });
        translateA.setDuration(300);
        translateA.start();

        alphaA = ValueAnimator.ofFloat(v.getAlpha(), 0.35f);
        alphaA.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float current = (float) animation.getAnimatedValue();
                v.setAlpha(current);
            }
        });
        alphaA.setDuration(1000);
        alphaA.setStartDelay(1000);
        alphaA.start();
    }

    public class PointEvaluator implements TypeEvaluator {

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            Point startPoint = (Point) startValue;
            Point endPoint = (Point) endValue;
            float x = startPoint.x + fraction * (endPoint.x - startPoint.x);
            float y = startPoint.y + fraction * (endPoint.y - startPoint.y);
            Point point = new Point((int)x, (int)y);
            return point;
        }
    }

    public void destroy() {
        if (mFloatLayout != null) {
            // 移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
        }
    }
}
