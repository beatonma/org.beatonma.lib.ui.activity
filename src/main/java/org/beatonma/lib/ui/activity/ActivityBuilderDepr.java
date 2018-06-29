package org.beatonma.lib.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import org.beatonma.lib.ui.activity.popup.BasePopupActivity;
import org.beatonma.lib.ui.activity.popup.BasePopupActivityKt;
import org.beatonma.lib.ui.activity.transition.BaseTransform;
import org.beatonma.lib.util.Sdk;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

/**
 * Created by Michael on 09/09/2016.
 * 
 * A builder to simplify initiation of non-trivial Activity instances
 * This is intended for subclasses of BaseActivity, but should work for any Activity
 *
 * Rationale: Static methods for simplifying the adding of extras and suchlike are
 * all well and good but can get messy if child classes need more and more specialised versions.
 * Using this builder we can support arbitrary argument sets in a clean manner.
 */


@Deprecated
@SuppressWarnings("unused")
public class ActivityBuilderDepr {
    protected final static String TAG = "ActivityBuilder";

    private WeakReference<Context> mContext;
    private WeakReference<Fragment> mFragment;
    private boolean mForResult = false;
    private int mRequestCode;
    private Intent mIntent;
    private List<Pair<View, String>> mSharedViews;
    private Bundle mExtras;

    private Class<? extends BaseTransform> mTransform;

    /*
     * If this activity is started by user interaction, identify the view which should be used
     * as the source for any animation
     */
    private View mAnimationSource;

    /*
     * If both the calling and target activities are instances of PopupActivity
     * automatically include the card and background overlay in shared view animation.
     */
    private boolean mUsePopupTransition = true;

    /**
     *
     */
    @NonNull
    public static Constructor from(@NonNull final Context context) {
        return new Constructor(context);
    }

    @NonNull
    @Deprecated
    public static ActivityBuilderDepr forActivity(@NonNull final Context context,
                                                  @NonNull final Class cls) {
        return new ActivityBuilderDepr(context, cls);
    }

    private ActivityBuilderDepr(@NonNull final Context context) {
        mContext = new WeakReference<>(context);
        mSharedViews = new ArrayList<>();
        mExtras = new Bundle();
    }

    private ActivityBuilderDepr(@NonNull final Context context, @NonNull final Class cls) {
        this(context);
        mIntent = new Intent(context, cls);
    }

    @NonNull
    public ActivityBuilderDepr setClass(@NonNull final Class cls) {
        mIntent = new Intent(mContext.get(), cls);
        return this;
    }

    @NonNull
    public ActivityBuilderDepr setIntent(@NonNull final Intent intent) {
        mIntent = intent;
        return this;
    }

    @NonNull
    public ActivityBuilderDepr forResult(final int requestCode) {
        mForResult = true;
        mRequestCode = requestCode;
        return this;
    }

    @NonNull
    public ActivityBuilderDepr forResult(@Nullable final Fragment fragment, final int requestCode) {
        mFragment = new WeakReference<>(fragment);
        mForResult = true;
        mRequestCode = requestCode;
        return this;
    }

    @NonNull
    public ActivityBuilderDepr addSharedViews(@Nullable final Pair<View, String>... sharedViews) {
        if (sharedViews != null) {
            Collections.addAll(mSharedViews, sharedViews);
        }
        return this;
    }

    @NonNull
    public ActivityBuilderDepr addSharedViews(@Nullable final SharedView... sharedViews) {
        for (final SharedView sv : sharedViews) {
            mSharedViews.add(Pair.create(sv.getView(), sv.getTransitionName()));
        }
//        if (sharedViews != null) {
//            Collections.addAll(mSharedViews, sharedViews);
//        }
        return this;
    }

    @NonNull
    public ActivityBuilderDepr addSharedView(@NonNull final Pair<View, String> sharedView) {
        mSharedViews.add(sharedView);
        return this;
    }

    @NonNull
    public ActivityBuilderDepr addSharedView(@NonNull final View view, @NonNull final String transitionName) {
        mSharedViews.add(new Pair<> (view, transitionName));
        return this;
    }

    @NonNull
    public ActivityBuilderDepr addSharedView(@NonNull final View view, final int transitionResourceId) {
        return addSharedView(view, mContext.get().getString(transitionResourceId));
    }

    @NonNull
    public ActivityBuilderDepr putExtra(@NonNull final String name, final boolean value) {
        mExtras.putBoolean(name, value);
        return this;
    }

    @NonNull
    public ActivityBuilderDepr putExtra(@NonNull final String name, final int value) {
        mExtras.putInt(name, value);
        return this;
    }

    @NonNull
    public ActivityBuilderDepr putExtra(@NonNull final String name, final float value) {
        mExtras.putFloat(name, value);
        return this;
    }

    @NonNull
    public ActivityBuilderDepr putExtra(@NonNull final String name, @NonNull final String value) {
        mExtras.putString(name, value);
        return this;
    }

    @NonNull
    public ActivityBuilderDepr putExtra(@NonNull final String name, @NonNull final Serializable value) {
        mExtras.putSerializable(name, value);
        return this;
    }

    @NonNull
    public ActivityBuilderDepr putExtra(@NonNull final String name, @NonNull final Bundle value) {
        mExtras.putBundle(name, value);
        return this;
    }

    @NonNull
    public ActivityBuilderDepr putExtraStringArrayList(@NonNull final String name, @NonNull final ArrayList<String> value) {
        mExtras.putStringArrayList(name, value);
        return this;
    }

    @NonNull
    public ActivityBuilderDepr putExtraIntArrayList(@NonNull final String name, @NonNull final ArrayList<Integer> value) {
        mExtras.putIntegerArrayList(name, value);
        return this;
    }

    @NonNull
    public ActivityBuilderDepr putExtraParcelableArrayList(@NonNull final String name, @NonNull final ArrayList<? extends Parcelable> value) {
        mExtras.putParcelableArrayList(name, value);
        return this;
    }

    @NonNull
    private ActivityBuilderDepr putExtra(@NonNull final String name, @NonNull final Object value) throws ClassCastException {
        if (value instanceof Integer) {
            return putExtra(name, (Integer) value);
        }
        else if (value instanceof String) {
            return putExtra(name, (String) value);
        }
        else if (value instanceof Boolean) {
            return putExtra(name, (Boolean) value);
        }
        else if (value instanceof Float) {
            return putExtra(name, (Float) value);
        }
        else if (value instanceof ArrayList) {
            return putExtra(name, (ArrayList<String>) value);
        }
        else if (value instanceof Bundle) {
            return putExtra(name, (Bundle) value);
        }
        else if (value instanceof Serializable) {
            return putExtra(name, (Serializable) value);
        }
        return this;
    }

    /**
     * Copy the extras from another intent
     * @param intent
     * @return
     */
    @NonNull
    public ActivityBuilderDepr putExtras(@NonNull final Intent intent) {
        mIntent.putExtras(intent);
        return this;
    }

    @NonNull
    public ActivityBuilderDepr putExtras(@NonNull final Bundle bundle) {
        mIntent.putExtras(bundle);
        return this;
    }

    @Nullable
    public View animationSource() {
        return mAnimationSource;
    }

    @NonNull
    public ActivityBuilderDepr animationSource(@Nullable final View animationSource) {
        mAnimationSource = animationSource;
        return this;
    }

    @NonNull
    public boolean usePopupTransition() {
        return mUsePopupTransition;
    }

    @NonNull
    public ActivityBuilderDepr usePopupTransition(final boolean popupTransition) {
        mUsePopupTransition = popupTransition;
        return this;
    }

    @NonNull
    public ActivityBuilderDepr transform(@Nullable final Class<? extends BaseTransform> transform) {
        mTransform = transform;
        return this;
    }

    /**
     * Add extras to mIntent for the given transformation.
     * mTransform must implement the same addExtras(Intent, View) static method
     * as seen in BaseTransform.
     */
    private void addTransitionExtras() {
        if (mTransform != null) {
            try {
                final Method addExtras = mTransform.getMethod("addExtras", Intent.class, View.class);
                addExtras.invoke(null, mIntent, mAnimationSource);
                return;
            }
//            catch (NoSuchMethodException e) {
//                Log.d(TAG, "addTransitionExtras(): Method not found: %s", e.toString());
//            }
//            catch (InvocationTargetException e) {
//                Log.d(TAG, "addTransitionExtras(): Invocation error: %s %s");
//            }
//            catch (IllegalAccessException e) {
//                Log.d(TAG, "addTransitionExtras(): Illegal access error: %s", e.toString());
//            }
            catch (Exception e) {
                Log.d(TAG, "addTransitionExtras(): " + e.toString());
            }
        }
        BaseTransform.addExtras(mIntent, mAnimationSource);
    }

    public void start() {
        if (mIntent == null) {
            throw new NullPointerException("Intent is missing. A target Class or Intent must be provided.");
        }

        final Context context = mContext.get();

        mIntent.putExtras(mExtras);

        if (context instanceof BaseActivity) {
            startWithActivityContext();
        }
        else {
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        }
    }

    @SuppressWarnings("NewApi")
    private void startWithActivityContext() {
        final Activity activity = (Activity) mContext.get();

        if (mUsePopupTransition) {
            if (activity instanceof BaseActivity) {
                addSharedViews(((BaseActivity) activity).getSharedViews());
                if (activity instanceof BasePopupActivity) {
                    mIntent.putExtra(BasePopupActivityKt.EXTRA_CALLED_FROM_POPUP, true);
                }
            }
        }

        if (mAnimationSource != null) {
            if (Sdk.INSTANCE.isKitkat()) {
//                BaseTransform.addExtras(mIntent, mAnimationSource);
                addTransitionExtras();
            }
            if (mSharedViews == null) {
                Log.w(TAG, "An animation source has been defined but no shared views. A shared view must be defined for this type of transition to work.");
            }
        }

        if (Sdk.INSTANCE.isLollipop() && mSharedViews != null) {
            final Pair<View, String>[] sharedViews = new Pair[mSharedViews.size()];
            for (int i = 0; i < mSharedViews.size(); i++) {
                sharedViews[i] = mSharedViews.get(i);
            }
            final ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedViews);

            if (mForResult) {
                if (mFragment != null) {
                    mFragment.get().startActivityForResult(mIntent, mRequestCode, options.toBundle());
                }
                else {
                    activity.startActivityForResult(mIntent, mRequestCode, options.toBundle());
                }
            }
            else {
                activity.startActivity(mIntent, options.toBundle());
            }
        }
        else {
            if (mForResult) {
                if (mFragment != null) {
                    mFragment.get().startActivityForResult(mIntent, mRequestCode);
                }
                else {
                    activity.startActivityForResult(mIntent, mRequestCode);
                }
            }
            else {
                activity.startActivity(mIntent);
            }
        }
    }


    public static class Constructor {
        private WeakReference<Context> context;

        Constructor(@NonNull final Context context) {
            this.context = new WeakReference<>(context);
        }

        public ActivityBuilderDepr to(@NonNull final Class cls) {
            return new ActivityBuilderDepr(context.get(), cls);
        }
    }
}