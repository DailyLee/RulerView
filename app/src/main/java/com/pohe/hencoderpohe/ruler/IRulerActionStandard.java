package com.pohe.hencoderpohe.ruler;

import android.view.View;

/**
 * 类描述：
 * 创建人：dl
 * 创建时间：2017/10/16
 */
public interface IRulerActionStandard {
    View getView();

    /**
     * 获取当前值
     * @return
     */
    float getCurrentVal();

    /**
     * 使ruler滑动到指定位置
     * @param offsetValue
     */
    void rulerScrollTo(float offsetValue);

    /**
     * 设置红色区域 单边
     * @param entity
     */
    void setThreshold(MeasureThresholdEntity entity) ;

    /**
     * 设置背景颜色
     * @param color
     */
    void setBackgroudColor(String color);

    /**
     * 设置当前标尺范围
     * @param minValue
     * @param maxValue
     */
    void setRulerViewRange(float minValue, float maxValue);

    /**
     * 设置回调到rulerView
     * @param listening
     */
    void setScrollListening(MeasureViewHelper.ScrollListening listening);

    void setDefault(float defaultValue) ;
}
