package com.pohe.hencoderpohe.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.Scroller;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 类描述：
 * 创建人：dl
 * 创建时间：2017/10/16 15:55
 */
public class ColorRuler extends View implements IRulerActionStandard {
    private static final String DEFAULT_NORMAL_COLOR = "#7DA8EF";
    private static final String DEFAULT_ABNORMAL_COLOR = "#FFB2C2";
    private int mTextSize;
    private float mDecimalDigits, mCurrentValue, mStart, mEnd;
    private int mUnitDistance;
    private float mVelocity = 1.0f;
    private Scroller mScroller;
    private GestureDetector mGestureDetector;
    private Rect clear = new Rect();
    private ArrayList<Rect> threshold = new ArrayList();
    private MeasureThresholdEntity mThreshold;
    public MeasureViewHelper.InputType mInputType;

    private int mSmallScaleHeight;
    private int mMiddleScaleHeight;
    private int mLargeScaleHeight;
    private int mTextMarginBottom;
    private int defaultTextSize = 16;
    private String defaultBackgroud = "#4CC9FF";
    private String mBackgroudColor;
    private int defaultUnitDistance = UIUtil.dip2px(8);
    private static final int mRulerCursorWidth = UIUtil.dip2px(16);
    private static final int mRulerCursorHeight = UIUtil.dip2px(12);
    private String mNormalColor = DEFAULT_NORMAL_COLOR;
    private String mAbNormalColor = DEFAULT_ABNORMAL_COLOR;
    private String mNormalColorDark = "#6A90CC"; //更深一点的颜色
    private String mAbNormalColorDark = "#E09DAB";
    private int mWidthPixels = getContext().getResources().getDisplayMetrics().widthPixels;

    private Paint paint = new Paint();

    private Path path = new Path();
    private MeasureViewHelper.ScrollListening listening;

    public float getStart() {
        return mStart;
    }

    public float getEnd() {
        return mEnd;
    }

    public ColorRuler(Context context, MeasureViewHelper.InputType inputType) {
        super(context);
        this.mInputType = inputType;
        mDecimalDigits = mInputType == MeasureViewHelper.InputType.FLOAT ? 1 : 0;
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (MeasureSpec.AT_MOST == heightMode || MeasureSpec.UNSPECIFIED == heightMode) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(UIUtil.dip2px(30), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
        mUnitDistance = defaultUnitDistance;
        mTextSize = defaultTextSize;
        mGestureDetector = new GestureDetector(context, new CustomGestureListener());
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int height = getHeight();
        if (mSmallScaleHeight == 0) {
            mSmallScaleHeight = height * 14 / 55;
            mMiddleScaleHeight = height * 17 / 55;
            mLargeScaleHeight = height * 21 / 55;
            mTextMarginBottom = height * 15 / 55;
            mDistance = (int) (mEnd - mStart) * mUnitDistance * (mInputType == MeasureViewHelper.InputType.FLOAT ? 10 : 1) - getWidth() / 2;
        }
        paint.setTextAlign(Paint.Align.CENTER);

        canvas.drawColor(Color.WHITE); // 画背景

        drawBoundary(canvas);
        //画刻度线
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3.f);
        float startX = 0, startY = 0, stopX, stopY;
        int start = Float.valueOf(mStart).intValue();
        for (int i = start; i <= mEnd; i++) {
            if (mInputType == MeasureViewHelper.InputType.INT) {
                startX = i != mStart ? startX + mUnitDistance : startX;
                paint.setColor(Color.parseColor(isAbnormalValue(i) ? mAbNormalColor : mNormalColor));
                if (i == start) {
                    paint.setTextSize(sp2px(mTextSize));
                    canvas.drawText(String.valueOf(i), startX + 2, height - mTextMarginBottom, paint);
                }
                if (i % 10 == 0) {// 画整数值，整数刻度线长度变量
                    paint.setTextSize(sp2px(mTextSize));
                    stopY = mLargeScaleHeight;
                    canvas.drawText(String.valueOf(i), startX + 2, height - mTextMarginBottom, paint);
                } else if (i % 5 == 0) {// 每逢值为5的时候，刻度线稍长的长度变量
                    stopY = mMiddleScaleHeight;
                } else {// 正常刻度线长度变量
                    stopY = mSmallScaleHeight;
                }
                stopX = startX;
                canvas.drawLine(startX, startY, stopX, stopY, paint);
            } else {
                if (i == mEnd)
                    break;
                int n = i;
                for (int j = 0; j < 11; j++) {
                    paint.setColor(Color.parseColor(isAbnormalValue(n + j / 10f) ? mAbNormalColor : mNormalColor));
                    startX = j != 0 ? startX + mUnitDistance : startX;
                    if (j == 0 || j == 10) {// 画整数值，整数刻度线长度变量
                        paint.setTextSize(sp2px(mTextSize));
                        stopY = mLargeScaleHeight;
                        canvas.drawText(String.valueOf(j == 0 ? n : n + 1), startX + 2, getHeight() - mTextMarginBottom, paint);
                    } else if (j == 5) {// 每逢值为5的时候，刻度线稍长的长度变量
                        stopY = mMiddleScaleHeight;
                    } else {// 正常刻度线长度变量
                        stopY = mSmallScaleHeight;
                    }
                    stopX = startX;
                    canvas.drawLine(startX, startY, stopX, stopY, paint);
                }
            }
        }

        // 绘制中间指针
//        paint.setColor(Color.WHITE);
        paint.setColor(Color.parseColor(isAbnormalValue(getCurrentVal()) ? mAbNormalColor : mNormalColor));
        paint.setStyle(Paint.Style.FILL);
        int center = getWidth() / 2;

        // 坐半边指针
        path.reset();
        path.moveTo(center + mScroller.getCurrX() - mRulerCursorWidth / 2, 0);
        path.lineTo(center + mScroller.getCurrX(), 0);
        path.lineTo(center + mScroller.getCurrX(), mRulerCursorHeight);
        path.lineTo(center + mScroller.getCurrX() - mRulerCursorWidth / 2, 0);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, paint);

        // 右半边指针
        path.reset();
        paint.setColor(Color.parseColor(isAbnormalValue(getCurrentVal()) ? mAbNormalColorDark : mNormalColorDark));
        paint.setStyle(Paint.Style.FILL);
        path.moveTo(center + mScroller.getCurrX(), 0);
        path.lineTo(center + mScroller.getCurrX() + mRulerCursorWidth / 2, 0);
        path.lineTo(center + mScroller.getCurrX(), mRulerCursorHeight);
        path.lineTo(center + mScroller.getCurrX(), 0);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, paint);
//        setDefault(6.7f);
    }

    /**
     * 绘制两条边界线
     *
     * @param canvas
     */
    private void drawBoundary(Canvas canvas) {
        int startX = 0, stopX = 0;
        if (mInputType == MeasureViewHelper.InputType.INT) {
            startX = -getWidth() / 2;
        } else if (mInputType == MeasureViewHelper.InputType.FLOAT) {
            startX = -getWidth() / 2;
        }
        if (mInputType == MeasureViewHelper.InputType.INT) {
            stopX = (int) ((mEnd - mStart) * mUnitDistance + getWidth() / 2);
        } else if (mInputType == MeasureViewHelper.InputType.FLOAT) {
            stopX = (int) ((mEnd - mStart) * mUnitDistance * 10 + getWidth() / 2);
        }
        paint.setColor(Color.parseColor(mNormalColor));
        canvas.drawLine(startX, 0, stopX, 0, paint);
        canvas.drawLine(startX, getHeight(), stopX, getHeight(), paint);
        if (threshold.size() > 0) {
//            paint.setColor(Color.WHITE);
//            canvas.drawRect(clear, paint);
            paint.setColor(Color.parseColor(mAbNormalColor));
            paint.setAlpha(255);
            for (Rect rect : threshold) {
                canvas.drawLine(rect.left, 0, rect.right, 0, paint);
                canvas.drawLine(rect.left, getHeight(), rect.right, getHeight(), paint);
//                canvas.drawRect(rect, paint);
            }
        }
    }

    /**
     * @param value
     * @return (true:异常值, false:正常值)
     */
    private boolean isAbnormalValue(float value) {
        try {
            if (mThreshold != null) {
                if (mThreshold.oneWay != null) {
                    // 暂时不考虑
                }
                if (mThreshold.twoWay != null) {
                    if (mThreshold.twoWay[0][0] <= value && value < mThreshold.twoWay[0][1]) {
                        return true;
                    }
                    if (mThreshold.canEqualMaxValue) {
                        if (mThreshold.twoWay[1][0] < value && value <= mThreshold.twoWay[1][1]) {
                            return true;
                        }
                    } else {
                        if (mThreshold.twoWay[1][0] <= value && value <= mThreshold.twoWay[1][1]) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ViewParent parent = getParent();
        if (parent != null && !(MotionEvent.ACTION_DOWN == ev.getAction()) && mGestureDetector.onTouchEvent(ev)) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            if (!mScroller.isFinished()) {
                mScroller.setFinalX(mScroller.getCurrX());
                mScroller.setFinalY(mScroller.getCurrY());
                mScroller.forceFinished(true);
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (e2.getAction() == MotionEvent.ACTION_UP) {
                scrollToMiddle();
            }
            int dis = (int) (distanceX * mVelocity);
            if (mScroller.getFinalX() > mDistance) {
                mScroller.startScroll(mDistance, 0, 0, 0);
            } else if (mScroller.getFinalX() < -getWidth() / 2) {
                mScroller.startScroll(-getWidth() / 2, 0, 0, 0);
            } else {
                mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dis, 0);
            }
            invalidate();
            if (Math.abs(distanceY) <= Math.abs(distanceX)) {
                return true;
            }
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScroller.abortAnimation();
            mScroller.fling(getScrollX(), 0, -(int) (velocityX * mVelocity), 0, -getWidth() / 2, mDistance, 0, 0);
            //invalidate();
            return false;
        }
    }

    private int mDistance;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float value = (getWidth() / 2.0f + l) / (mUnitDistance);
        if (mInputType == MeasureViewHelper.InputType.FLOAT) {
            value = mStart + value / 10f;
        } else if (mInputType == MeasureViewHelper.InputType.INT) {
            value = mStart + value;
        } else {
            throw new IllegalStateException("onScroll():mTenVal = (" + mInputType.name() + "). mTenVal error!  ");
        }
        mCurrentValue = mDecimalDigits == 0 ? new BigDecimal(value)
                .setScale(0, BigDecimal.ROUND_HALF_UP).floatValue()
                : new BigDecimal(value).setScale(1,
                BigDecimal.ROUND_HALF_UP).floatValue();
        mCurrentValue = mCurrentValue < mStart ? mStart : mCurrentValue;
        mCurrentValue = mCurrentValue > mEnd ? mEnd : mCurrentValue;
        if (listening != null) {
            listening.getScrollValue(mCurrentValue);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();

            if (!mScroller.computeScrollOffset()){
                scrollToMiddle();
            }
        }
    }

    private void scrollToMiddle() {
        int width = getWidth();
        int currX = mScroller.getFinalX() - (mWidthPixels - width) / 2;

        int offSet = currX % mUnitDistance;
        if (offSet != 0) {
            if (offSet > mUnitDistance / 2) {
                mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), mUnitDistance - offSet, 0);
            } else {
                mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), -offSet, 0);
            }
            postInvalidate();
        }
    }

    private int sp2px(float spValue) {
        float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    public void setThreshold(MeasureThresholdEntity entity) {
        if (entity != null) {
            this.mThreshold = entity;
            if (entity.oneWay != null) {
                setThreshold(entity.oneWay);
            } else if (entity.twoWay != null) {
                setThreshold(entity.twoWay);
            }
        }
    }


    private void setThreshold(float[][] x) {
        try {
            threshold.clear();
            Rect rect = new Rect();
            if (mInputType == MeasureViewHelper.InputType.INT) {
                if (x[0][0] > mStart) {
                    rect = new Rect((int) ((x[0][0] - mStart) * mUnitDistance), 0, (int) ((x[0][1] - mStart) * mUnitDistance), getHeight());
                } else {
                    rect = new Rect((int) ((x[0][0] - mStart) * mUnitDistance - getWidth() / 2), 0, (int) ((x[0][1] - mStart) * mUnitDistance), getHeight());
                }
            } else if (mInputType == MeasureViewHelper.InputType.FLOAT) {
                if (x[0][0] > mStart) {
                    rect = new Rect((int) ((x[0][0] - mStart) * mUnitDistance * 10), 0, (int) ((x[0][1] - mStart) * mUnitDistance * 10), getHeight());
                } else {
                    rect = new Rect((int) ((x[0][0] - mStart) * mUnitDistance * 10 - getWidth() / 2), 0, (int) ((x[0][1] - mStart) * mUnitDistance * 10), getHeight());
                }
            }
            threshold.add(rect);

            Rect rect1 = new Rect();
            if (mInputType == MeasureViewHelper.InputType.INT) {
                if (x[1][1] < mEnd) {
                    rect1 = new Rect((int) ((x[1][0] - mStart) * mUnitDistance), 0, (int) ((x[1][1] - mStart) * mUnitDistance), getHeight());
                } else {
                    rect1 = new Rect((int) ((x[1][0] - mStart) * mUnitDistance), 0, (int) ((x[1][1] - mStart) * mUnitDistance + getWidth() / 2), getHeight());
                }
            } else if (mInputType == MeasureViewHelper.InputType.FLOAT) {
                if (x[1][1] < mEnd) {
                    rect1 = new Rect((int) ((x[1][0] - mStart) * mUnitDistance * 10), 0, (int) ((x[1][1] - mStart) * mUnitDistance * 10), getHeight());
                } else {
                    rect1 = new Rect((int) ((x[1][0] - mStart) * mUnitDistance * 10), 0, (int) ((x[1][1] - mStart) * mUnitDistance * 10 + getWidth() / 2), getHeight());
                }
            }
            threshold.add(rect1);
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setThreshold(float[] x) {
        try {
            threshold.clear();
            Rect rect = new Rect();
            if (mInputType == MeasureViewHelper.InputType.INT) {
                rect = new Rect((int) ((x[0] - mStart) * mUnitDistance), 0, (int) ((x[1] - mStart) * mUnitDistance), getHeight());
            } else if (mInputType == MeasureViewHelper.InputType.FLOAT) {
                rect = new Rect((int) ((x[0] - mStart) * mUnitDistance * 10), 0, (int) ((x[1] - mStart) * mUnitDistance * 10 + getWidth() / 2), getHeight());
            }
            threshold.add(rect);
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRulerViewRange(float minValue, float maxValue) {
        // if (minValue != 0 && maxValue != 0) {
        this.mStart = minValue;
        this.mEnd = maxValue;
        invalidate();
        // }
    }

    @Override
    public void setScrollListening(MeasureViewHelper.ScrollListening listening) {
        this.listening = listening;
    }

    @Override
    public void setDefault(float defaultValue) {
        float x;
        if (mInputType == MeasureViewHelper.InputType.INT) {
            x = (defaultValue - mStart) * mUnitDistance;
        } else {
            x = (defaultValue * 10 - mStart * 10) * mUnitDistance;
        }
        mScroller.startScroll(0, 0, (int) x - getWidth() / 2, 0, 100);
        postInvalidate();
    }

    @Override
    public void rulerScrollTo(float offsetValue) {
        int startX = 0;
        int endX = 0;
        int disX = 0;
        if (MeasureViewHelper.InputType.INT == mInputType) {
            startX = (int) (mCurrentValue * mUnitDistance);
            endX = (int) (offsetValue) * mUnitDistance;
            disX = endX - startX;
        } else if (MeasureViewHelper.InputType.FLOAT == mInputType) {
            startX = (int) (mCurrentValue * 10 * mUnitDistance);
            endX = (int) (offsetValue * 10 * mUnitDistance);
            disX = endX - startX;
        }
        mScroller.startScroll(mScroller.getCurrX(), 0, disX, 0, 300);
        postInvalidate();
    }


    @Override
    public View getView() {
        return this;
    }

    @Override
    public float getCurrentVal() {
        return mCurrentValue;
    }

    @Override
    public void setBackgroudColor(String color) {
        this.mBackgroudColor = color;
    }
}