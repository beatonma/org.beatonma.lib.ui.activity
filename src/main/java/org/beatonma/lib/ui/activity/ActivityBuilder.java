package org.beatonma.lib.ui.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Pair;
import android.view.View;

import org.beatonma.lib.core.util.Sdk;
import org.beatonma.lib.log.Log;
import org.beatonma.lib.ui.activity.transition.BaseTransform;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

@SuppressWarnings("unused")
public class ActivityBuilder {
    protected final static String TAG = "ActivityBuilder";

    private Context mContext;
    private androidx.fragment.app.Fragment mFragment;
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
    public static Constructor with(@NonNull final Context context) {
        return new Constructor(context);
    }

    public static ActivityBuilder forActivity(@NonNull final Context context,
                                              @NonNull final Class cls) {
        return new ActivityBuilder(context, cls);
    }

    public static ActivityBuilder forActivity(@NonNull final Context context,
                                              @NonNull final Intent intent) {
        return new ActivityBuilder(context, intent);
    }

    private ActivityBuilder(@NonNull final Context context) {
        mContext = context;
        mSharedViews = new ArrayList<>();
        mExtras = new Bundle();
    }

    public ActivityBuilder(@NonNull final Context context, @NonNull final Class cls) {
        this(context, new Intent(context, cls));
    }

    public ActivityBuilder(@NonNull final Context context, @NonNull final Intent intent) {
        this(context);
        mIntent = intent;
    }

    public ActivityBuilder setClass(final Class cls) {
        mIntent = new Intent(mContext, cls);
        return this;
    }

    public ActivityBuilder setIntent(final Intent intent) {
        mIntent = intent;
        return this;
    }

    public ActivityBuilder forResult(final int requestCode) {
        mForResult = true;
        mRequestCode = requestCode;
        return this;
    }

    public ActivityBuilder forResult(final Fragment fragment, final int requestCode) {
        mFragment = fragment;
        mForResult = true;
        mRequestCode = requestCode;
        return this;
    }

    public ActivityBuilder addSharedViews(final Pair<View, String>... sharedViews) {
        Collections.addAll(mSharedViews, sharedViews);
        return this;
    }

    public ActivityBuilder addSharedView(final Pair<View, String> sharedView) {
        mSharedViews.add(sharedView);
        return this;
    }

    public ActivityBuilder addSharedView(final View view, final String transitionName) {
        mSharedViews.add(new Pair<> (view, transitionName));
        return this;
    }

    public ActivityBuilder addSharedView(final View view, final int transitionResourceId) {
        return addSharedView(view, mContext.getString(transitionResourceId));
    }

    public ActivityBuilder putExtra(final String name, final boolean value) {
        mExtras.putBoolean(name, value);
        return this;
    }

    public ActivityBuilder putExtra(final String name, final int value) {
        mExtras.putInt(name, value);
        return this;
    }

    public ActivityBuilder putExtra(final String name, final float value) {
        mExtras.putFloat(name, value);
        return this;
    }

    public ActivityBuilder putExtra(final String name, final String value) {
        mExtras.putString(name, value);
        return this;
    }

    public ActivityBuilder putExtra(final String name, final Serializable value) {
        mExtras.putSerializable(name, value);
        return this;
    }

    public ActivityBuilder putExtra(final String name, final Bundle value) {
        mExtras.putBundle(name, value);
        return this;
    }

    public ActivityBuilder putExtraStringArrayList(final String name, final ArrayList<String> value) {
        mExtras.putStringArrayList(name, value);
        return this;
    }

    public ActivityBuilder putExtraIntArrayList(final String name, final ArrayList<Integer> value) {
        mExtras.putIntegerArrayList(name, value);
        return this;
    }

    public ActivityBuilder putExtraParcelableArrayList(final String name, final ArrayList<? extends Parcelable> value) {
        mExtras.putParcelableArrayList(name, value);
        return this;
    }

    private ActivityBuilder putExtra(final String name, final Object value) throws ClassCastException {
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
    public ActivityBuilder putExtras(final Intent intent) {
        mIntent.putExtras(intent);
        return this;
    }

    public ActivityBuilder putExtras(final Bundle bundle) {
        mIntent.putExtras(bundle);
        return this;
    }

    public View animationSource() {
        return mAnimationSource;
    }

    public ActivityBuilder animationSource(final View animationSource) {
        mAnimationSource = animationSource;
        return this;
    }

    public boolean usePopupTransition() {
        return mUsePopupTransition;
    }

    public ActivityBuilder usePopupTransition(final boolean popupTransition) {
        mUsePopupTransition = popupTransition;
        return this;
    }

    public ActivityBuilder transform(final Class<? extends BaseTransform> transform) {
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
                Method addExtras = mTransform.getMethod("addExtras", Intent.class, View.class);
                addExtras.invoke(null, mIntent, mAnimationSource);
                return;
            }
            catch (NoSuchMethodException e) {
                Log.d(TAG, "addTransitionExtras(): Method not found: %s", e.toString());
            }
            catch (InvocationTargetException e) {
                Log.d(TAG, "addTransitionExtras(): Invocation error: %s %s",
                        e.getCause().toString(), e.getTargetException().toString());
            }
            catch (IllegalAccessException e) {
                Log.d(TAG, "addTransitionExtras(): Illegal access error: %s", e.toString());
            }
            catch (Exception e) {
                Log.d(TAG, "addTransitionExtras(): %s", e.toString());
            }
        }
        BaseTransform.addExtras(mIntent, mAnimationSource);
    }

    public void start() {
        if (mIntent == null) {
            throw new NullPointerException("Intent is missing. A target Class or Intent must be provided.");
        }

        mIntent.putExtras(mExtras);

        if (mContext instanceof BaseActivity) {
            startWithActivityContext();
        }
        else {
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(mIntent);
        }
    }

    @SuppressWarnings("NewApi")
    private void startWithActivityContext() {
        final Activity activity = (Activity) mContext;

        if (mUsePopupTransition) {
            if (mContext instanceof BaseActivity) {
                addSharedViews(((BaseActivity) mContext).getSharedViews());
                if (mContext instanceof PopupActivity) {
                    mIntent.putExtra(PopupActivity.EXTRA_CALLED_FROM_POPUP, true);
                }
            }
        }

        if (mAnimationSource != null) {
            if (Sdk.isKitkat()) {
//                BaseTransform.addExtras(mIntent, mAnimationSource);
                addTransitionExtras();
            }
            if (mSharedViews == null) {
                Log.w(TAG, "An animation source has been defined but no shared views. A shared view must be defined for this type of transition to work.");
            }
        }

        if (Sdk.isLollipop() && mSharedViews != null) {
            final Pair<View, String>[] sharedViews = new Pair[mSharedViews.size()];
            for (int i = 0; i < mSharedViews.size(); i++) {
                sharedViews[i] = mSharedViews.get(i);
            }
            Log.d(TAG, "shared views: %d", mSharedViews.size());
            final ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, sharedViews);
            if (mForResult) {
                if (mFragment != null) {
                    mFragment.startActivityForResult(mIntent, mRequestCode, options.toBundle());
                }
                else {
                    Log.d(TAG, "Starting activity with animation");
                    activity.startActivityForResult(mIntent, mRequestCode, options.toBundle());
                }
            }
            else {
                Log.d(TAG, "Starting activity with animation");
                activity.startActivity(mIntent, options.toBundle());
            }
        }
        else {
            if (mForResult) {
                if (mFragment != null) {
                    mFragment.startActivityForResult(mIntent, mRequestCode);
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
        private Context context;

        public Constructor(Context context) {
            this.context = context;
        }

        public ActivityBuilder target(Class cls) {
            return new ActivityBuilder(this.context, cls);
        }

        public ActivityBuilder target(Intent intent) {
            return new ActivityBuilder(this.context, intent);
        }
    }
}