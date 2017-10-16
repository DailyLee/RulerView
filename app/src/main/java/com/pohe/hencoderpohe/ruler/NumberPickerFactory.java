package com.pohe.hencoderpohe.ruler;

import android.content.Context;

/**
 * 类描述：
 * 创建人：dl
 * 创建时间：2017/10/16
 */
public class NumberPickerFactory implements INumberPicker {
    @Override
    public INumberPickerActionStandard createNumberPickerView(Context context) {
        return new AddAndSubtractEditTextView(context);
    }
}
