package com.pohe.hencoderpohe.ruler;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pohe.hencoderpohe.R;

/**
 * 类描述：
 * 创建人：dl
 * 创建时间：2017/10/16
 */
public class AddAndSubtractEditTextView extends LinearLayout implements INumberPickerActionStandard {
    private EditText mEditText;
    private TextView unitTV;
    private ImageView subtract, add;
    private MeasureViewHelper.InputType mInputType;


    private float mMinVal, mMaxVal;
    private MeasureViewHelper.NumberPickerChange mNumberPickerChangeListener;

    public AddAndSubtractEditTextView(Context context) {
        super(context);
        float textSize = 30f;
        LayoutInflater.from(getContext()).inflate(R.layout.layout_add_and_subtract, this);
        unitTV = (TextView) findViewById(R.id.unit);
        add = (ImageView) findViewById(R.id.number_picket_add);
        subtract = (ImageView) findViewById(R.id.number_picket_subtract);
        mEditText = (EditText) findViewById(R.id.input);
        mEditText.setEnabled(false);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    mEditText.setSelection(s.toString().length());
                }
            }
        });
        // FIXME: 16/8/18 输入框直接跳转还没实现
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = mEditText.getText().toString();
                    if (!TextUtils.isEmpty(text) && !".".equals(text)) {
                        if (mInputType == MeasureViewHelper.InputType.INT) {
                            int intNum;
                            if (text.contains(".")) {
                                intNum = (int) Float.valueOf(text).floatValue();
                            } else {
                                intNum = Integer.valueOf(text);
                            }
                            setCurrentVal(intNum);
                        } else {
                            float floatNum = Float.valueOf(String.format("%.1f", Float.valueOf(text)));
                            if (floatNum > mMinVal && floatNum < mMaxVal) {
                                setCurrentVal(floatNum);
                            } else {
                                Toast.makeText(getContext(),"输入有误",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    hideSoftKeyBoard();
                    return true;
                }
                return false;
            }
        });
        mEditText.setSelection(mEditText.getText().toString().length());
        mEditText.setTextSize(textSize);
        hideSoftKeyBoard();
    }

    public void setTextColor(int textColor) {
        mEditText.setTextColor(textColor);
    }


    public void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            if (activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void registerNumberUnitListener() {
        // FIXME: 16/8/18 加减号有bug
        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEditText.getText().toString();
                int num;
                if (text.contains(".")) {
                    num = (int) Float.valueOf(text).floatValue();
                } else {
                    num = Integer.valueOf(text);
                }
                num++;
//                mEditText.setText(Integer.toString(num));
                if (mNumberPickerChangeListener != null && num <= mMaxVal)
                    mNumberPickerChangeListener.additive(num);
            }
        });
        subtract.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEditText.getText().toString();
                int num;
                if (text.contains(".")) {
                    num = (int) Float.valueOf(text).floatValue();
                } else {
                    num = Integer.valueOf(text);
                }
                if (num > 0) {
                    num--;
                }
//                mEditText.setText(Integer.toString(num));
                if (mNumberPickerChangeListener != null && num >= mMinVal)
                    mNumberPickerChangeListener.subtraction(num);
            }
        });
    }

    private void registerNumberDecimalUnitListener() {
        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String formatSize = String.format("%.1f", Float.valueOf(mEditText.getText().toString()));
                float size_onetench = Float.valueOf(formatSize);

                size_onetench += 0.1f;
//                mEditText.setText(String.format("%.1f", size_onetench));
                if (mNumberPickerChangeListener != null && size_onetench < mMaxVal)
                    mNumberPickerChangeListener.additive(size_onetench);

            }
        });
        subtract.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String formatSize = String.format("%.1f", Float.valueOf(mEditText.getText().toString()));
                float size_onetench = Float.valueOf(formatSize);
                if (size_onetench > 0.0f) {
                    size_onetench -= 0.1f;
                }
//                mEditText.setText(String.format("%.1f", size_onetench));
                if (mNumberPickerChangeListener != null && size_onetench > mMinVal)
                    mNumberPickerChangeListener.subtraction(size_onetench);
            }
        });
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public String getCurrentStr() {
        return mEditText.getText().toString();
    }

    @Override
    public void numberChange(MeasureViewHelper.NumberPickerChange numberPickerChange) {
        mNumberPickerChangeListener = numberPickerChange;
    }

    @Override
    public void setCurrentVal(float currentVale) {
        if (mInputType == MeasureViewHelper.InputType.INT) {
            mEditText.setText(String.valueOf(new Float(currentVale).intValue()));
            return;
        }
        mEditText.setText(String.valueOf(currentVale));
    }

    @Override
    public void setCurrentColor(int id){
        mEditText.setTextColor(getResources().getColor(id));
    }

    @Override
    public void setUnit(String unit) {
        unitTV.setText(unit);
    }

    @Override
    public void setRange(float minValue, float maxValue) {
        this.mMinVal = minValue;
        this.mMaxVal = maxValue;
    }

    @Override
    public void setInputType(MeasureViewHelper.InputType inputType) {
        this.mInputType = inputType;
        if (mInputType == MeasureViewHelper.InputType.INT) {
            registerNumberUnitListener();
        } else if (mInputType == MeasureViewHelper.InputType.FLOAT) {
            registerNumberDecimalUnitListener();
        }
    }
}
