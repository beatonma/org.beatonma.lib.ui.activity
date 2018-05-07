package org.beatonma.lib.ui.activity.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;

import org.beatonma.lib.ui.activity.R;
import org.beatonma.lib.ui.style.Animation;
import org.beatonma.lib.ui.style.Interpolate;
import org.beatonma.lib.ui.style.Views;

import java.util.ArrayList;
import java.util.List;

import static android.animation.ObjectAnimator.ofFloat;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * Created by Michael on 23/07/2016.
 *
 * This transition makes the new scene appear from the touch point which triggered the scene change
 * A bubble appears at the touch point and grows slightly as it moves to the target position
 * of the new scene, then uses a circular animation to reveal the full scene, fading in any
 * children of the main view. When reversing, the animation is mirrored (fade & shrink, then
 * fly back to the touch position).
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CircularTransform extends BaseTransform {
    private final static String TAG = "CircularTransform";

    private final static String EXTRA_COLOR = "extra_transform_color";
    static final String PROP_COLOR = "transform:color";
    static final String[] TRANSITION_PROPERTIES = {
            PROP_BOUNDS,
            PROP_COLOR
    };

    final Point mPoint = new Point();

    private int mStartX = -1;
    private int mStartY = -1;

    private boolean mGrowing = true;

    private Options mOptions;

    public static void addExtras(@NonNull Intent intent, @Nullable View v) {
        if (v == null) {
            return;
        }

        BaseTransform.addExtras(intent, v);
//        int[] loc = new int[2];
//        v.getLocationOnScreen(loc);
//
//        loc[0] += v.getWidth() / 2;
//        loc[1] += v.getHeight() / 2;
//
//        intent.putExtra(EXTRA_TOUCH_LOCATION, loc);

        final Drawable d = v.getBackground();
        if (d instanceof ColorDrawable) {
            intent.putExtra(EXTRA_COLOR, ((ColorDrawable) d).getColor());
        }
        else {
            final ColorStateList colorStateList = v.getBackgroundTintList();
            if (colorStateList != null) {
                intent.putExtra(EXTRA_COLOR, colorStateList.getDefaultColor());
            }
        }
    }

    private CircularTransform(int[] start) {
        mOptions = new Options();
        setPathMotion(new GravityArcMotion());
        if (start != null) {
            mStartX = start[0];
            mStartY = start[1];
        }
    }

    private CircularTransform(int[] start, boolean isGrowing) {
        this(start);
        mGrowing = isGrowing;
    }

    public CircularTransform options(Options options) {
        mOptions = options;
        return this;
    }

    /**
     * Call this in onCreate() of the new activity to receive animation touch coordinates
     * and enable the window transition
     * @param activity
     * @param target      The view that the bubble will grow into
     */
    public static void setup(@NonNull Activity activity, @Nullable View target) {
        setup(activity, target, new Options());
    }

    public static void setup(@NonNull Activity activity, @Nullable View target, final Options options) {
        int[] touchLocation = getTouchLocation(activity);

//        final Intent intent = activity.getIntent();
//        if (intent != null) {
//            int color = intent.getIntExtra(EXTRA_COLOR, 1234567890);
//            if (color != 1234567890) {
//
//            }
//        }

        final CircularTransform sharedEnter =
                new CircularTransform(touchLocation)
                        .options(options);
        final CircularTransform sharedReturn =
                new CircularTransform(touchLocation, false)
                        .options(options);

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
        if (startValues == null || endValues == null) {
            return null;
        }

        if (mGrowing) {
            return createEnterAnimator(sceneRoot, startValues, endValues);
        }
        else {
            return createReturnAnimator(sceneRoot, startValues, endValues);
        }
    }

    private Animator createEnterAnimator(final ViewGroup sceneRoot,
                                         final TransitionValues startValues,
                                         final TransitionValues endValues) {

        final Rect endBounds = (Rect) endValues.values.get(PROP_BOUNDS);

        final View view = endValues.view;

        final int transX = mStartX - endBounds.centerX();
        final int transY = mStartY - endBounds.centerY();

        view.setTranslationX(transX);
        view.setTranslationY(transY);

        final Animator translate = ofFloat(
                view, View.TRANSLATION_X, View.TRANSLATION_Y,
                getPathMotion().getPath(transX, transY, 0, 0));
        translate.setDuration(Animation.DURATION_ENTER);

        final Animator contents = getContentEnterAnimator(view);

        // TODO mOptions.color
//        final Animator color = ObjectAnimator.ofArgb(view, )

        Views.getCenter(view, mPoint);
        final Animator circleAnim = Animation.circularReveal(view, mPoint, 0);
//        final Animator circleAnim =
//                AnimationUtils.getCircularReveal(
//                        view, center.x, center.y, 0, Math.max(view.getWidth(), view.getHeight()));
        setDuration(circleAnim, Animation.DURATION_ENTER);
        setInterpolator(circleAnim, new HesitateInterpolator());

        final AnimatorSet transition = new AnimatorSet();
        transition.playTogether(circleAnim, translate, contents);

        return new NoPauseAnimator(transition);
    }

    private Animator createReturnAnimator(final ViewGroup sceneRoot,
                                          final TransitionValues startValues,
                                          final TransitionValues endValues) {

        final Rect startBounds = (Rect) startValues.values.get(PROP_BOUNDS);

        final View view = endValues.view;

        final int transX = startBounds.centerX() - mStartX;
        final int transY = startBounds.centerY() - mStartY;

        // Force measure / layout the dialog back to it's original bounds
        view.measure(
                makeMeasureSpec(startBounds.width(), View.MeasureSpec.EXACTLY),
                makeMeasureSpec(startBounds.height(), View.MeasureSpec.EXACTLY));
        view.layout(startBounds.left, startBounds.top, startBounds.right, startBounds.bottom);

        final Animator translate = mOptions.returnStyleTranslate
                ? ofFloat(
                        view, View.TRANSLATION_X, View.TRANSLATION_Y,
                        getPathMotion().getPath(0, 0, -transX, -transY))
                : null;
        setInterpolator(Interpolate.getExitInterpolator());

        final Animator alpha = mOptions.returnStyleFade
                ? ObjectAnimator.ofFloat(view, View.ALPHA, 0)
                : null;
        setDuration(alpha, Animation.DURATION_QUARTER);

//        final Point center = AnimationUtils.getCenter(view);
        Views.getCenter(view, mPoint);
        final Animator circle = mOptions.returnStyleCircle
//                ? AnimationUtils.getCircularReveal(view, center.x, center.y, Math.max(view.getWidth(), view.getHeight()), 0)
                ? Animation.circularHide(view, mPoint, Math.max(view.getWidth(), view.getHeight()))
                : null;
        setInterpolator(circle, Interpolate.getExitInterpolator());
        setStartDelay(circle, 10);

        final AnimatorSet transition = Animation.playTogether(translate, alpha, circle);
        setDuration(transition, Animation.DURATION_EXIT);

        return new NoPauseAnimator(transition);
    }

    /**
     * Return an Animator that handles any child Views of the given parent
     */
    public Animator getContentEnterAnimator(final View parent) {
        final TimeInterpolator enterInterpolator = Interpolate.getEnterInterpolator();
        final AnimatorSet contents = new AnimatorSet();
        List<Animator> contentList = null;
        if (parent instanceof ViewGroup) {
            ViewGroup vg = parent.findViewById(R.id.content_group);
            if (vg == null) {
                vg = parent.findViewById(R.id.content_container);
                if (vg != null && vg.getChildCount() > 0) {
                    final View firstChild = vg.getChildAt(0);
                    if (firstChild instanceof ViewGroup) {
                        vg = (ViewGroup) firstChild;
                    }
                }
            }
            if (vg == null) {
                vg = (ViewGroup) parent;
            }

            contentList = new ArrayList<>(vg.getChildCount());
            final int childCount = vg.getChildCount();
            final int childAnimationDelay = Math.max(
                    0,
                    Math.min(CHILD_ANIMATION_DELAY_MS,
                            Animation.DURATION / childCount)); // Restrict the child animation delay to 0 < value < CHILD_ANIMATION_DELAY_MS
            for (int i = 0; i < childCount; i++) {
                final View child = vg.getChildAt(i);
                final int startDelay = i * childAnimationDelay;

                if (mOptions.contentFade) {
                    child.setAlpha(0f);

                    final Animator fade = ObjectAnimator.ofFloat(child, View.ALPHA, 1f);
                    fade.setStartDelay(startDelay);
                    fade.setInterpolator(enterInterpolator);

                    contentList.add(fade);
                }

                if (mOptions.contentTranslate) {
                    child.setTranslationY(child.getHeight() / 3);
                    final Animator slide = ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, 0f);
                    slide.setStartDelay(startDelay);
                    slide.setInterpolator(enterInterpolator);
                    contentList.add(slide);
                }
            }
        }
        contents.playTogether(contentList);
        contents.setStartDelay(Animation.DURATION_HALF);
        contents.setDuration(Animation.DURATION);

        return contents;
    }

    /**
     * A container for animation options
     */
    public static class Options {
        boolean changeColor = false;
        boolean returnStyleCircle = true;
        boolean returnStyleTranslate = false;
        boolean returnStyleFade = true;
        boolean contentTranslate = false;
        boolean contentFade = true;

        int color;

        public Options() {

        }

        public Options returnStyleCircle(final boolean returnStyleCircle) {
            this.returnStyleCircle = returnStyleCircle;
            return this;
        }

        public Options returnStyleTranslate(final boolean returnStyleTranslate) {
            this.returnStyleTranslate = returnStyleTranslate;
            return this;
        }

        public Options returnStyleFade(final boolean returnStyleFade) {
            this.returnStyleFade = returnStyleFade;
            return this;
        }

        public Options changeColor(final boolean changeColor) {
            this.changeColor = changeColor;
            return this;
        }

        public Options color(final int color) {
            this.color = color;
            return this;
        }

        public Options contentTranslate(final boolean contentTranslate) {
            this.contentTranslate = contentTranslate;
            return this;
        }

        public Options contentFade(final boolean contentFade) {
            this.contentFade = contentFade;
            return this;
        }
    }
}
