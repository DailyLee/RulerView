package com.pohe.hencoderpohe.ruler;

/**
 * 类描述：
 * 创建人：dl
 * 创建时间：2017/10/16
 */
public class MeasureThresholdEntity {
    public String thresholdName;
    public int textColor;
    public int textSize;
    public float[] oneWay;
    public float[][] twoWay;
    public int type = -1;
    public boolean canEqualMaxValue = true; //是否可以和阈值右边界相等（即与twoWay[0][1]相等，此时twoWay不能为空）

    public MeasureThresholdEntity(String thresholdName, float[] oneWay) {
        this.thresholdName = thresholdName;
        this.oneWay = oneWay;
    }

    public MeasureThresholdEntity(float[][] twoWay) {
        this.twoWay = twoWay;
    }

    public MeasureThresholdEntity(String thresholdName, float[][] twoWay, int type) {
        this.thresholdName = thresholdName;
        this.twoWay = twoWay;
        this.type = type;
    }

    public MeasureThresholdEntity(float[] oneWay) {
        this.oneWay = oneWay;
    }

    public MeasureThresholdEntity setCanEqualMaxValue(boolean canEqualMaxValue){
        this.canEqualMaxValue = canEqualMaxValue;
        return this;
    }
}
