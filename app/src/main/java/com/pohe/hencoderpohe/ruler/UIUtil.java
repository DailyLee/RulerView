package com.pohe.hencoderpohe.ruler;

import android.content.res.Resources;

public class UIUtil {
    public static int dip2px(int dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}