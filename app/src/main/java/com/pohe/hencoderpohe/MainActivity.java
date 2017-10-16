package com.pohe.hencoderpohe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.pohe.hencoderpohe.ruler.MeasureViewHelper;

public class MainActivity extends AppCompatActivity {

    private int mValue;
    private LinearLayout mLlSystolicContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLlSystolicContent = (LinearLayout) findViewById(R.id.ll_measure_systolic_pressure);

        buildMeasureView();
    }

    /**
     * 标尺View
     */
    private void buildMeasureView() {
        MeasureViewHelper systolicViewHelper = new MeasureViewHelper
                .Builder(this)
                .setDefaultVal(50.0f)
                .setThresholds(new float[][]{{40.0f, 50.0f}, {70.0f, 80.0f}})
                .setRange(40.0f, 80.0f)
                .setCanEqualMaxValue(false)
                .setUnit("kg")
                .setInputType(MeasureViewHelper.InputType.FLOAT)
                .setRulerScrollListener(new MeasureViewHelper.RulerScrollListening() {
                    @Override
                    public void getScrollValue(float currentValue) {
                        mValue = (int) currentValue;
                    }
                }).build();

        mLlSystolicContent.removeAllViews();
        mLlSystolicContent.addView(systolicViewHelper.getView());

    }
}