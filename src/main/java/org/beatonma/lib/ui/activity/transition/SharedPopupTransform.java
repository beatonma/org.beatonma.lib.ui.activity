package org.beatonma.lib.ui.activity.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.graphics.Rect;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;

import org.beatonma.lib.ui.activity.R;
import org.beatonma.lib.ui.activity.popup.BasePopupActivity;
import org.beatonma.lib.ui.style.Interpolate;
import org.beatonma.lib.util.Sdk;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * Created by Michael on 17/10/2016.
 *
 * A transition to be used when moving between two instances of PopupActivity
 * The transition animates any size change of the card background while replacing card contents
 * Content animation should start at the point the user interacted with to start the new activity
 */
public class SharedPopupTransform extends BaseTransform {
    protected final static String TAG = "SharedPopupTransform";

    private int mStartX = -1;
    private int mStartY = -1;

    private boolean mReturn = false;

    private SharedPopupTransform(int[] start) {
        if (Sdk.INSTANCE.isLollipop()) {
            setPathMotion(new GravityArcMotion());
        }
        if (start != null) {
            mStartX = start[0];
            mStartY = start[1];
        }
    }

    private SharedPopupTransform(int[] start, boolean isReturning) {
        this(start);
        mReturn = isReturning;
    }

    @SuppressWarnings("NewApi")
    public static void setup(@NonNull BasePopupActivity activity, @Nullable View target) {
        if (!Sdk.INSTANCE.isKitkat()) {
            return;
        }

        int[] touchLocation = getTouchLocation(activity);
        final SharedPopupTransform sharedEnter = new SharedPopupTransform(touchLocation);
        final SharedPopupTransform sharedReturn = new SharedPopupTransform(touchLocation, true);

        if (target != null) {
            sharedEnter.addTarget(target);
            sharedReturn.addTarget(target);
        }

        activity.getWindow().setSharedElementEnterTransition(sharedEnter);
        activity.getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @SuppressWarnings("NewApi")
    @Override
    public Animator createAnimator(final ViewGroup sceneRoot,
                                   final TransitionValues startValues,
                                   final TransitionValues endValues) {

        if (!Sdk.INSTANCE.isKitkat()) {
            return null;
        }

        if (startValues == null || endValues == null) {
            return null;
        }

        final AnimatorSet transition = new AnimatorSet();

        final View card = endValues.view.findViewById(R.id.card);

        final Rect startBounds = (Rect) startValues.values.get(PROP_BOUNDS);
        final Rect endBounds = (Rect) endValues.values.get(PROP_BOUNDS);

        ObjectAnimator topLeftAnimator = null;
        ObjectAnimator bottomRightAnimator = null;
        if (Sdk.INSTANCE.isLollipop()) {
            if (startBounds.left != endBounds.left || startBounds.top != endBounds.top) {
                Path topLeftPath = getPathMotion().getPath(startBounds.left, startBounds.top,
                        endBounds.left, endBounds.top);
                topLeftAnimator = ObjectAnimator.ofInt(card, "left", "top", topLeftPath);
            }
            if (startBounds.right != endBounds.right || startBounds.bottom != endBounds.bottom) {
                Path bottomRightPath = getPathMotion().getPath(startBounds.right, startBounds.bottom,
                        endBounds.right, endBounds.bottom);
                bottomRightAnimator = ObjectAnimator.ofInt(card, "right", "bottom", bottomRightPath);
            }
        }

        if (mReturn) {
            card.measure(
                    makeMeasureSpec(startBounds.width(), View.MeasureSpec.EXACTLY),
                    makeMeasureSpec(startBounds.height(), View.MeasureSpec.EXACTLY));
            card.layout(startBounds.left, startBounds.top, startBounds.right, startBounds.bottom);
        }

        final Animator changeBounds = mergeAnimators(topLeftAnimator, bottomRightAnimator);
        Animator fadeContentGroup = null;
        List<Animator> fadeContentViews = null;

        final View newContent = card.findViewById(R.id.content_container);

        if (mReturn) {
            fadeContentGroup = ObjectAnimator.ofFloat(newContent, View.ALPHA, 0f);
        }
        else {
            if (newContent instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) newContent;
                // Find the 'main' ViewGroup in the tree
                while (vg.getChildCount() == 1 || vg.getChildAt(0) instanceof ViewGroup) {
                    try {
                        vg = (ViewGroup) vg.getChildAt(0);
                    }
                    catch (ClassCastException e) {
                        // pass
                        break;
                    }
                }

                fadeContentViews = new ArrayList<>(vg.getChildCount());
                for (int i = 0; i < vg.getChildCount(); i++) {
                    final View child = vg.getChildAt(i);
                    final int startDelay = i * CHILD_ANIMATION_DELAY_MS;

                    child.setAlpha(0f);
                    child.setTranslationY(child.getHeight() / 3);

                    final Animator fade = ObjectAnimator.ofFloat(child, View.ALPHA, 1f);
                    fade.setStartDelay(startDelay);
                    fadeContentViews.add(fade);

                    final Animator slide = ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, 0f);
                    slide.setStartDelay(startDelay);
                    fadeContentViews.add(slide);
                }
            }
        }

        final AnimatorSet contentAnimation = new AnimatorSet();
        contentAnimation.playTogether(fadeContentViews);
        contentAnimation.playTogether(fadeContentGroup);

        changeBounds.setInterpolator(Interpolate.getMotionInterpolator());

        if (mReturn) {
            contentAnimation.setDuration(120);
            changeBounds.setDuration(200);
            contentAnimation.setInterpolator(Interpolate.getExitInterpolator());
            transition.playSequentially(contentAnimation, changeBounds);
        }
        else {
            transition.playSequentially(changeBounds, contentAnimation);
            contentAnimation.setInterpolator(Interpolate.getEnterInterpolator());
        }

        return new NoPauseAnimator(transition);
    }

    @SuppressWarnings("NewApi")
    @Override
    protected void captureValues(TransitionValues transitionValues) {
        if (!Sdk.INSTANCE.isKitkat()) {
            return;
        }
        final View view = transitionValues.view.findViewById(R.id.card);
        if (view == null || view.getWidth() <= 0 || view.getHeight() <= 0) return;

        transitionValues.values.put(PROP_BOUNDS, new Rect(view.getLeft(), view.getTop(),
                view.getRight(), view.getBottom()));
    }
}
