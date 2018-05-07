package org.beatonma.lib.ui.activity.transition;


import android.view.animation.Interpolator;

/**
 * Created by Michael on 10/04/2017.
 *
 * Pauses at around 10% from t~=0.1 until t~=0.6
 *
 * Primarily used for CircularTransform
 */

public class HesitateInterpolator implements Interpolator {
    @Override
    public float getInterpolation(final float t) {
        final float x = 3.1f * t - 1f;
        return 0.1f * ((float) Math.pow(x, 3) + 0.8f);
    }
}
