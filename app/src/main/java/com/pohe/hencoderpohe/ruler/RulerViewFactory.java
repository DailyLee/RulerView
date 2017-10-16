package com.pohe.hencoderpohe.ruler;

import android.content.Context;

/**
 * 类描述：
 * 创建人：dl
 * 创建时间：2017/10/16
 */
public class RulerViewFactory implements IRulerView {
    @Override
    public IRulerActionStandard createRulerView(Context context, MeasureViewHelper.InputType inputType) {
        return new ColorRuler(context,inputType);
    }
}
