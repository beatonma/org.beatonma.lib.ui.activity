package org.beatonma.lib.ui.activity.transition;

/**
 * Created by Michael on 15/06/2017.
 */

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;

import org.beatonma.lib.ui.style.Interpolate;

/**
 * An extension to {@link ChangeBounds} that also morphs the views background (color & corner
 * radius).
 *
 * Adapted from Plaid by Nick Butcher: https://github.com/nickbutcher/plaid
 */

/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MorphTransform extends ChangeBounds {

    private  static final String EXTRA_SHARED_ELEMENT_START_COLOR =
            "EXTRA_SHARED_ELEMENT_START_COLOR";
    private static final String EXTRA_SHARED_ELEMENT_START_CORNER_RADIUS =
            "EXTRA_SHARED_ELEMENT_START_CORNER_RADIUS";
    private static final long DEFAULT_DURATION = 300L;

    private final int startColor;
    private final int endColor;
    private final int startCornerRadius;
    private final int endCornerRadius;

    public MorphTransform(@ColorInt int startColor, @ColorInt int endColor,
                          int startCornerRadius, int endCornerRadius) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.startCornerRadius = startCornerRadius;
        this.endCornerRadius = endCornerRadius;
        setDuration(DEFAULT_DURATION);
        setPathMotion(new GravityArcMotion());
    }

    /**
     * Configure {@code intent} with the extras needed to initialize this transition.
     */
    public static void addExtras(@NonNull Intent intent,
                                 @ColorInt int startColor,
                                 int startCornerRadius) {
        intent.putExtra(EXTRA_SHARED_ELEMENT_START_COLOR, startColor);
        intent.putExtra(EXTRA_SHARED_ELEMENT_START_CORNER_RADIUS, startCornerRadius);
    }

    /**
     * Configure {@link MorphTransform}s & set as {@code activity}'s shared element enter and return
     * transitions.
     */
    public static void setup(@NonNull Activity activity,
                             @Nullable View target,
                             @ColorInt int endColor,
                             int endCornerRadius) {
        final Intent intent = activity.getIntent();
        if (intent == null
                || !intent.hasExtra(EXTRA_SHARED_ELEMENT_START_COLOR)
                || !intent.hasExtra(EXTRA_SHARED_ELEMENT_START_CORNER_RADIUS)) return;

        final int startColor = activity.getIntent().
                getIntExtra(EXTRA_SHARED_ELEMENT_START_COLOR, Color.TRANSPARENT);
        final int startCornerRadius =
                intent.getIntExtra(EXTRA_SHARED_ELEMENT_START_CORNER_RADIUS, 0);

        final MorphTransform sharedEnter =
                new MorphTransform(startColor, endColor, startCornerRadius, endCornerRadius);
        // Reverse the start/end params for the return transition
        final MorphTransform sharedReturn =
                new MorphTransform(endColor, startColor, endCornerRadius, startCornerRadius);
        if (target != null) {
            sharedEnter.addTarget(target);
            sharedReturn.addTarget(target);
        }
        activity.getWindow().setSharedElementEnterTransition(sharedEnter);
        activity.getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @Override
    public Animator createAnimator(final ViewGroup sceneRoot,
                                   final TransitionValues startValues,
                                   final TransitionValues endValues) {
        final Animator changeBounds = super.createAnimator(sceneRoot, startValues, endValues);
        if (changeBounds == null) return null;

        TimeInterpolator interpolator = getInterpolator();
        if (interpolator == null) {
            interpolator = Interpolate.getExitInterpolator();
        }

        final MorphDrawable background = new MorphDrawable(startColor, startCornerRadius);
        endValues.view.setBackground(background);

        final Animator color = ObjectAnimator.ofArgb(background, MorphDrawable.COLOR, endColor);
        final Animator corners =
                ObjectAnimator.ofFloat(background, MorphDrawable.CORNER_RADIUS, endCornerRadius);

        // ease in the dialog's child views (fade in & staggered slide up)
        if (endValues.view instanceof ViewGroup) {
            final ViewGroup vg = (ViewGroup) endValues.view;
            final long duration = getDuration() / 2;
            float offset = vg.getHeight() / 3;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View v = vg.getChildAt(i);
                v.setTranslationY(offset);
                v.setAlpha(0f);
                v.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(duration)
                        .setStartDelay(duration)
                        .setInterpolator(interpolator);
                offset *= 1.8f;
            }
        }

        final AnimatorSet transition = new AnimatorSet();
        transition.playTogether(changeBounds, corners, color);
        transition.setDuration(getDuration());
        transition.setInterpolator(interpolator);
        return transition;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static class MorphDrawable extends Drawable {
        /**
         * A drawable that can morph size, shape (via it's corner radius) and color.  Specifically this is
         * useful for animating between a FAB and a dialog.
         */

        public static final Property<MorphDrawable, Float> CORNER_RADIUS =
                new FloatProperty<MorphDrawable>("cornerRadius") {

                    @Override
                    public void setValue(MorphDrawable morphDrawable, float value) {
                        morphDrawable.setCornerRadius(value);
                    }

                    @Override
                    public Float get(MorphDrawable morphDrawable) {
                        return morphDrawable.getCornerRadius();
                    }

                };

        public static final Property<MorphDrawable, Integer> COLOR =
                new IntProperty<MorphDrawable>("color") {

                    @Override
                    public void setValue(MorphDrawable morphDrawable, int value) {
                        morphDrawable.setColor(value);
                    }

                    @Override
                    public Integer get(MorphDrawable morphDrawable) {
                        return morphDrawable.getColor();
                    }

                };

        private final Paint paint;
        private float cornerRadius;

        public MorphDrawable(@ColorInt int color, float cornerRadius) {
            this.cornerRadius = cornerRadius;
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(color);
        }

        public float getCornerRadius() {
            return cornerRadius;
        }

        public void setCornerRadius(float cornerRadius) {
            this.cornerRadius = cornerRadius;
            invalidateSelf();
        }

        public int getColor() {
            return paint.getColor();
        }

        public void setColor(@ColorInt int color) {
            paint.setColor(color);
            invalidateSelf();
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawRoundRect(
                    getBounds().left,
                    getBounds().top,
                    getBounds().right,
                    getBounds().bottom,
                    cornerRadius,
                    cornerRadius,
                    paint);
        }

        @Override
        public void getOutline(Outline outline) {
            outline.setRoundRect(getBounds(), cornerRadius);
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            paint.setColorFilter(cf);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return paint.getAlpha();
        }
    }


    /**
     *
     * Borrowed from Plaid by Nick Butcher: https://github.com/nickbutcher/plaid
     *
     * An implementation of {@link Property} to be used specifically with fields of
     * type
     * <code>float</code>. This type-specific subclass enables performance benefit by allowing
     * calls to a {@link #set(Object, Float) set()} function that takes the primitive
     * <code>float</code> type and avoids autoboxing and other overhead associated with the
     * <code>Float</code> class.
     *
     * @param <T> The class on which the Property is declared.
     **/
    public static abstract class FloatProperty<T> extends Property<T, Float> {
        public FloatProperty(String name) {
            super(Float.class, name);
        }

        /**
         * A type-specific override of the {@link #set(Object, Float)} that is faster when dealing
         * with fields of type <code>float</code>.
         */
        public abstract void setValue(T object, float value);

        @Override
        final public void set(T object, Float value) {
            setValue(object, value);
        }
    }

    /**
     *
     * Borrowed from Plaid by Nick Butcher: https://github.com/nickbutcher/plaid
     *
     * An implementation of {@link Property} to be used specifically with fields of
     * type
     * <code>int</code>. This type-specific subclass enables performance benefit by allowing
     * calls to a {@link #set(Object, Integer) set()} function that takes the primitive
     * <code>int</code> type and avoids autoboxing and other overhead associated with the
     * <code>Integer</code> class.
     *
     * @param <T> The class on which the Property is declared.
     */
    public static abstract class IntProperty<T> extends Property<T, Integer> {

        public IntProperty(String name) {
            super(Integer.class, name);
        }

        /**
         * A type-specific override of the {@link #set(Object, Integer)} that is faster when dealing
         * with fields of type <code>int</code>.
         */
        public abstract void setValue(T object, int value);

        @Override
        final public void set(T object, Integer value) {
            setValue(object, value.intValue());
        }
    }
}
