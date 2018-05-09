package org.beatonma.lib.ui.activity.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.DisplayMetrics;
import android.view.View;

import org.beatonma.lib.core.util.Sdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Michael on 17/10/2016.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public abstract class BaseTransform extends Transition {
    protected final static String TAG = "BaseTransform";

    static final String EXTRA_TOUCH_LOCATION = "extra_touch_location";
    static final String PROP_BOUNDS = "transform:bounds";
    static final String[] TRANSITION_PROPERTIES = {
            PROP_BOUNDS
    };

    static final int CHILD_ANIMATION_DELAY_MS = 15;

    public static void addExtras(@NonNull Intent intent, @Nullable View v) {
        if (v == null) {
            return;
        }
        int[] loc = new int[2];
        v.getLocationOnScreen(loc);

        loc[0] += v.getWidth() / 2;
        loc[1] += v.getHeight() / 2;

        intent.putExtra(EXTRA_TOUCH_LOCATION, loc);
    }

    static int[] getTouchLocation(@NonNull Activity activity) {
        final Intent intent = activity.getIntent();
        final DisplayMetrics dm = activity.getResources().getDisplayMetrics();

        int[] touchLocation;
        if (intent == null) {
            touchLocation = new int[] { dm.widthPixels / 2, dm.heightPixels / 2};
        }
        else {
            touchLocation = intent.getIntArrayExtra(EXTRA_TOUCH_LOCATION);
            if (touchLocation == null) {
                touchLocation = new int[] { dm.widthPixels / 2, dm.heightPixels / 2};
            }
        }

        return touchLocation;
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @SuppressWarnings("NewApi")
    protected void captureValues(TransitionValues transitionValues) {
        if (!Sdk.isKitkat()) {
            return;
        }
        final View view = transitionValues.view;
        if (view == null || view.getWidth() <= 0 || view.getHeight() <= 0) return;

        transitionValues.values.put(PROP_BOUNDS, new Rect(view.getLeft(), view.getTop(),
                view.getRight(), view.getBottom()));
    }

    @Override
    public String[] getTransitionProperties() {
        return TRANSITION_PROPERTIES;
    }

    static Animator mergeAnimators(final Animator animator1, final Animator animator2) {
        if (animator1 == null) {
            return animator2;
        } else if (animator2 == null) {
            return animator1;
        } else {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animator1, animator2);
            return animatorSet;
        }
    }

    static void setDuration(Animator animator, long duration) {
        if (animator != null) {
            animator.setDuration(duration);
        }
    }

    static void setInterpolator(Animator animator, TimeInterpolator interpolator) {
        if (animator != null) {
            animator.setInterpolator(interpolator);
        }
    }

    static void setStartDelay(Animator animator, long startDelay) {
        if (animator != null) {
            animator.setStartDelay(startDelay);
        }
    }
}
