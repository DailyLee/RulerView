package com.pohe.hencoderpohe.ruler;

import android.view.View;

/**
 * 类描述：
 * 创建人：dl
 * 创建时间：2017/10/16
 */
public interface INumberPickerActionStandard {
    View getView();

    String getCurrentStr();

    void numberChange(MeasureViewHelper.NumberPickerChange numberPickerChange);

    void setCurrentVal(float currentVale);

    void setCurrentColor(int id);

    void setUnit(String unit);

    void setRange(float minValue, float maxValue);

    void setInputType(MeasureViewHelper.InputType inputType);
}
