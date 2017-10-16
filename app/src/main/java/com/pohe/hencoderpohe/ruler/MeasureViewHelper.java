package com.pohe.hencoderpohe.ruler;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pohe.hencoderpohe.R;

/**
 * 类描述：
 * 创建人：dl
 * 创建时间：2017/10/16
 */
public class MeasureViewHelper {
    private static final int DEFAULT_HEIGHT = UIUtil.dip2px(70); //默认标尺高度
    private static final int DEFAULT_VIEW_PADDING = UIUtil.dip2px(13); //默认边距

    private Builder mBuilder;
    private LinearLayout mContentView;
    private TextView proNameTV;
    private View inflateView;
    private INumberPickerActionStandard mNumberPickerStandard;
    private IRulerActionStandard mRulerStandard;

    public MeasureViewHelper(Builder builder) {
        this.mBuilder = builder;
        inflate();
        initView();
        if (mBuilder.isShowPick) {
            mContentView.addView(obtainNumberPicker());
        } else {
            mNumberPickerStandard = mBuilder.assemblyNumberPicker();
        }
        mContentView.addView(obtainRulerView());
    }

    private void initView() {
        if (mBuilder.isShowPick) {
            String proName = mBuilder.getProName();
            if (!TextUtils.isEmpty(proName)) {
                proNameTV.setText(proName);
            }
        } else {
            proNameTV.setVisibility(View.GONE);
        }

    }

    public View getView() {
        return inflateView;
    }

    private void inflate() {
        inflateView = LayoutInflater.from(mBuilder.mContext).inflate(R.layout.measure_group, null);
        proNameTV = (TextView) inflateView.findViewById(R.id.measure_proName);
        mContentView = (LinearLayout) inflateView.findViewById(R.id.measure_content_view);
    }

    private View obtainRulerView() {
        mRulerStandard = mBuilder.assemblyRuler();
        View measureView = mRulerStandard.getView();
        measureView.post(new Runnable() {
            @Override
            public void run() {
                mRulerStandard.setRulerViewRange(mBuilder.mMinValue, mBuilder.mMaxValue);
                mRulerStandard.setDefault(mBuilder.mDefaultVal);
                if (mBuilder.mThreshold != null) {
                    mRulerStandard.setThreshold(new MeasureThresholdEntity(mBuilder.mThreshold).setCanEqualMaxValue(mBuilder.getCanEqualMAxValue()));
                } else if (mBuilder.mThresholds != null) {
                    mRulerStandard.setThreshold(new MeasureThresholdEntity(mBuilder.mThresholds).setCanEqualMaxValue(mBuilder.getCanEqualMAxValue()));
                }
            }
        });
        if (mBuilder.mScrollListener != null) {
            mRulerStandard.setScrollListening(mBuilder.mScrollListener);
        } else {
            mRulerStandard.setScrollListening(new ScrollListening() {
                @Override
                public void getScrollValue(float currentValue) {
                    mNumberPickerStandard.setCurrentVal(currentValue);
                    if (mBuilder.mThresholds != null) {
                        //右边边界值为正常值，其他类型右边界值为超标值
                        Boolean max = mBuilder.getCanEqualMAxValue() ? currentValue > mBuilder.mThresholds[1][0] : currentValue >= mBuilder.mThresholds[1][0];
                        if (currentValue < mBuilder.mThresholds[0][1] || max) {
                            mNumberPickerStandard.setCurrentColor(R.color.measure_abnormal_value);
                        } else {
                            mNumberPickerStandard.setCurrentColor(R.color.measure_normal_value);
                        }
                    }
                    if (mBuilder.mRulerListener != null) {
                        mBuilder.mRulerListener.getScrollValue(currentValue);
                    }
                }
            });
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mBuilder.mRuleViewSize[0], mBuilder.mRuleViewSize[1]);
        params.setMargins(mBuilder.mViewPadding[0], mBuilder.mViewPadding[1], mBuilder.mViewPadding[2], mBuilder.mViewPadding[3]);
        measureView.setLayoutParams(params);
        return measureView;
    }

    private View obtainNumberPicker() {
        mNumberPickerStandard = mBuilder.assemblyNumberPicker();
        mNumberPickerStandard.setInputType(mBuilder.mInputType);
        mNumberPickerStandard.setUnit(mBuilder.mUnit);
        mNumberPickerStandard.setRange(mBuilder.mMinValue, mBuilder.mMaxValue);
        if (mBuilder.mNumberPickerCallBack != null) {
            mNumberPickerStandard.numberChange(mBuilder.mNumberPickerCallBack);
        } else {
            mNumberPickerStandard.numberChange(new NumberPickerChange() {
                @Override
                public void additive(float val) {
                    if (mRulerStandard != null) {
                        mRulerStandard.rulerScrollTo(val);
                    }
                }

                @Override
                public void subtraction(float val) {
                    if (mRulerStandard != null) {
                        mRulerStandard.rulerScrollTo(val);
                    }
                }

                @Override
                public void numberChange(float val) {
                    if (mRulerStandard != null) {
                        mRulerStandard.rulerScrollTo(val);
                    }
                }
            });
        }
        View numberPickView = mNumberPickerStandard.getView();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mBuilder.mNumberPickSize[0], mBuilder.mNumberPickSize[1]);
//        params.setMargins(mBuilder.mViewPadding[0], mBuilder.mViewPadding[1], mBuilder.mViewPadding[2], mBuilder.mViewPadding[3]);
        numberPickView.setLayoutParams(params);
        return numberPickView;
    }

    public INumberPickerActionStandard getNumberPickerStandard() {
        return mNumberPickerStandard;
    }

    public IRulerActionStandard getRulerStandard() {
        return mRulerStandard;
    }

    public void setNumberPickerValueColor(int colorId) {
        mNumberPickerStandard.setCurrentColor(colorId);
    }

    public static class Builder {
        private Context mContext;
        private String mProName;
        private float mDefaultVal;
        private INumberPicker mPicker;
        private IRulerView mRulerView; //标尺构造器
        private ScrollListening mScrollListener;
        private RulerScrollListening mRulerListener;
        private NumberPickerChange mNumberPickerCallBack;
        private float mMinValue = 0;
        private float mMaxValue = 100;
        private String mUnit = "kg";
        private InputType mInputType = InputType.INT;
        private boolean keepCreate;
        private float[] mThreshold;
        private float[][] mThresholds;
        private boolean mCanEqualMaxValue = true; //最大值边界值是否可相等
        int[] mRuleViewSize = new int[]{LinearLayout.LayoutParams.MATCH_PARENT, DEFAULT_HEIGHT};
        int[] mNumberPickSize = new int[]{LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT};
        int[] mViewPadding = new int[]{0, DEFAULT_VIEW_PADDING, 0, 0};

        private boolean isShowPick = true;

        public String getProName() {
            return mProName;
        }

        public Context getContext() {
            return mContext;
        }

        private IRulerActionStandard assemblyRuler() {
            if (mRulerView != null) {
                return mRulerView.createRulerView(mContext, mInputType);
            }
            return new RulerViewFactory().createRulerView(mContext, mInputType);
        }

        public Builder setThreshold(float[] threshold) {
            this.mThreshold = threshold;
            return this;
        }

        public Builder setCanEqualMaxValue(boolean canEqual) {
            this.mCanEqualMaxValue = canEqual;
            return this;
        }

        public Builder setThresholds(float[][] thresholds) {
            this.mThresholds = thresholds;
            try {
                if (thresholds[1] != null && mInputType != InputType.FLOAT) {
//                    thresholds[1][0] += .1f;
//                    thresholds[1][1] += .1f;
                }
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            } finally {
                return this;
            }
        }

        private INumberPickerActionStandard assemblyNumberPicker() {
            if (mPicker != null) {
                return mPicker.createNumberPickerView(mContext);
            }
            return new NumberPickerFactory().createNumberPickerView(mContext);
        }

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public Builder setInputType(InputType inputType) {
            this.mInputType = inputType;
            return this;
        }

        public Builder setProName(String name) {
            this.mProName = name;
            return this;
        }

        public Builder setScrollListener(ScrollListening listener) {
            this.mScrollListener = listener;
            return this;
        }

        public Builder setRulerScrollListener(RulerScrollListening rulerListener) {
            this.mRulerListener = rulerListener;
            return this;
        }

        public Builder setUnit(String unit) {
            this.mUnit = unit;
            return this;
        }

        public Builder setNumberPickerCallBack(NumberPickerChange numberPickerCallBack) {
            this.mNumberPickerCallBack = numberPickerCallBack;
            return this;
        }

        public Builder setDefaultVal(float val) {
            this.mDefaultVal = val;
            return this;
        }

        public Builder setIsShowPick(boolean isShowPick) {
            this.isShowPick = isShowPick;
            return this;
        }

        public Builder pickFoctory(INumberPicker picker) {
            this.mPicker = picker;
            return this;
        }

        public Builder rulerFoctory(IRulerView rulerView) {
            this.mRulerView = rulerView;
            return this;
        }

        public Builder setRuleViewSize(int[] size) {
            if (size != null && size.length == 2) {
                this.mRuleViewSize = size;
            }
            return this;
        }

        public Builder setNumberPickerSize(int[] size) {
            if (size != null && size.length == 2) {
                this.mNumberPickSize = size;
            }
            return this;
        }

        public Builder setPaddingSize(int[] size) {
            if (size != null && size.length <= 4) {
                for (int i = 0; i < size.length; i++) {
                    this.mViewPadding[i] = size[i];
                }
            }
            return this;
        }

        public Builder setRange(float minValue, float maxValue) {
            this.mMinValue = minValue;
            this.mMaxValue = maxValue;
            return this;
        }

        public boolean getCanEqualMAxValue() {
            return mCanEqualMaxValue;
        }

        public float getMaxValue() {
            return mMaxValue;
        }

        public float getMinValue() {
            return mMinValue;
        }

        public float getDefaultVal() {
            return mDefaultVal;
        }

        public Builder keepCreate(boolean isKeep) {
            this.keepCreate = isKeep;
            return this;
        }

        public boolean isKeepCreate() {
            return keepCreate;
        }

        public MeasureViewHelper build() {
            return new MeasureViewHelper(this);
        }
    }

    /**
     * rulerView标尺滑动的回调,调用在ColorRuler
     */
    public interface ScrollListening {
        void getScrollValue(float currentValue);
    }

    /**
     * 用于ScrollListening回调中回调
     * 为了抽象,rulerView和NumberPicker实现绑定,只关心变更结果
     */
    public interface RulerScrollListening {
        void getScrollValue(float currentValue);
    }

    /**
     * 文本框 加减事件
     */
    public interface NumberPickerChange {
        void additive(float val);

        void subtraction(float val);

        void numberChange(float val);
    }

    public enum InputType {
        INT, FLOAT
    }
}
