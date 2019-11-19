package com.officework.customViews;

import android.content.res.Resources;

/**
 * Created by Girish on 10/3/2016.
 */
public class ArcUtils {
    /**
     * Created by bruce on 14-11-6.
     */

    private ArcUtils() {
    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
