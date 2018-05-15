package org.beatonma.lib.ui.activity.popup;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.beatonma.lib.ui.activity.R;
import org.beatonma.lib.ui.activity.databinding.ActivityPopupBinding;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * Created by Michael on 22/07/2016.
 */
public abstract class PopupActivity<T extends ViewDataBinding> extends BasePopupActivity<T> {
    protected final static String TAG = "PopupActivity";

//    public final static String EXTRA_TITLE = "title";
//
//    public final static String EXTRA_CALLED_FROM_POPUP = "extra_called_from_popup";
//    public final static String EXTRA_ENABLE_DRAG_DISMISS = "extra_enable_drag_dismiss";

//    private ActivityPopupBinding mBinding;
    private ActivityPopupBinding mBinding;

//    private String mTitle;
//    private boolean mCalledFromPopup;
//
//    public static Bundle getDefaultOptions(final String title) {
//        final Bundle options = new Bundle();
//        options.putString(EXTRA_TITLE, title);
//        return options;
//    }

    @Override
    public int getPopupLayoutId() {
        return R.layout.activity_popup;
    }

    @Override
    public View getOverlay() {
        return mBinding.overlay;
    }

    @Override
    public ViewGroup getCard() {
        return mBinding.card;
    }

    @Override
    public TextView getTitleView() {
        return mBinding.title;
    }

    @Override
    public ViewGroup getCardContentContainer() {
        return mBinding.contentContainer;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.setContentView(this, getPopupLayoutId());
        super.onCreate(savedInstanceState);

//        final ViewDataBinding contentBinding = DataBindingUtil.inflate(
//                LayoutInflater.from(this), getLayoutId(), null, false);
//        mBinding.contentContainer.addView(contentBinding.getRoot());
//
//        setupWindowTransitions();
//
////        setupDragFrame();
//
//        initLayout(contentBinding);
//
//        setTitle(mTitle);
//
//        mBinding.overlay.setBackgroundColor(
//                getResources().getColor(mCalledFromPopup
//                        ? R.color.Transparent
//                        : R.color.DialogOverlay));
//
//        mBinding.overlay.setOnClickListener(view -> PopupActivity.this.close());
//
//        mBinding.contentContainer.animate()
//                .alpha(1)
//                .setStartDelay(Animation.DURATION_THIRD)
//                .setInterpolator(Interpolate.getEnterInterpolator())
//                .start();
    }

//    @Override
//    protected ActivityPopupBinding getBinding() {
//        return mBinding;
//    }

    protected ActivityPopupBinding getParentBinding() {
        return mBinding;
    }
}
//
//    @CallSuper
//    protected void initIntent(final Intent intent) {
//        super.initIntent(intent);
//
//        if (intent != null) {
//            mCalledFromPopup = intent.getBooleanExtra(EXTRA_CALLED_FROM_POPUP, false);
//            initExtras(intent.getExtras());
//        }
//        else {
//            initExtras(null);
//        }
//    }
//
//    @CallSuper
//    protected void initExtras(final Bundle extras) {
//        super.initExtras(extras);
//
//        if (extras == null) {
//            return;
//        }
//
//        final Object titleObj = extras.get(EXTRA_TITLE);
//        if (titleObj != null) {
//            if (titleObj instanceof String) {
//                mTitle = (String) titleObj;
//            }
//            else if (titleObj instanceof Integer) {
//                final int resID = (int) titleObj;
//                if (resID != 0) {
//                    mTitle = getString(resID);
//                }
//            }
//        }

//        mTitle = extras.getString(EXTRA_TITLE, getString(extras.getInt(EXTRA_TITLE, 0)));
//        mEnableDragDismiss = extras.getBoolean(EXTRA_ENABLE_DRAG_DISMISS, false);
//    }

//    protected abstract int getLayoutId();
//    protected abstract void initLayout(final ViewDataBinding binding);

//    protected void setupDragFrame() {
//        if (mEnableDragDismiss) {
//            mBinding.draggableFrame.draggingEnabled(true);
//            mDragDismissCallback = new SimpleDragDismissCallback();
//        }
//        else {
//            mBinding.draggableFrame.draggingEnabled(false);
//        }
//    }

//    /**
//     * Set up any custom window transitions here
//     */
//    @Override
//    protected void setupWindowTransitions() {
//        if (!Sdk.isLollipop()) {
//            return;
//        }
//        if (mCalledFromPopup) {
//            SharedPopupTransform.setup(this, null);
//        }
//        else {
//            CircularTransform.setup(this, mBinding.card);
//            Log.d(TAG, "setupWindowTransitions() CircularTransform");
//        }
//    }
//
//    /**
//     * Animate the container card when contents change bounds
//     */
//    @SuppressWarnings("NewApi")
//    protected void startCardChangeBoundsTransition() {
//        final Context context = mWeakContext.get();
//        if (context == null) {
//            return;
//        }
//
//        final Transition changeBounds = TransitionInflater.from(context)
//                .inflateTransition(R.transition.card_changebounds);
//        TransitionManager.beginDelayedTransition(getParentBinding().card, changeBounds);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                close();
//                break;
//        }
//
//        return false;
//    }
//
//    public void setTitle(final String title) {
//        mTitle = title;
//        mBinding.title.setText(title);
//        Views.hideIfEmpty(mBinding.title);
//    }
//
//    public void setTitle(final int resId) {
//        setTitle(getString(resId));
//    }

//    public void setExtraWide() {
//        mBinding.card.setMaxWidth(getResources().getDimension(R.dimen.max_dialog_width_extra));
//        mBinding.card.wrapContent(true);
//    }

//    /**
//     * Animate the container card when contents change bounds
//     */
//    @SuppressWarnings("NewApi")
//    protected void resize() {
//        final Context context = mWeakContext.get();
//        if (context == null) {
//            return;
//        }
//
//        if (Sdk.isKitkat()) {
//            Transition changeBounds = TransitionInflater.from(context)
//                    .inflateTransition(R.transition.card_changebounds);
//            TransitionManager.beginDelayedTransition(getParentBinding().card, changeBounds);
//        }
//    }
//
//    @Override
//    public androidx.core.util.Pair<View, String>[] getSharedViews() {
//        final Resources res = getResources();
//
//        return new Pair[] {
//                Pair.create(mBinding.card, res.getString(R.string.transition_card))
//        };
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mEnableDragDismiss) {
//            mBinding.draggableFrame.addListener(mDragDismissCallback);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (mEnableDragDismiss) {
//            mBinding.draggableFrame.removeListener(mDragDismissCallback);
//        }
//    }

//    public class SimpleDragDismissCallback extends ElasticDragDismissFrameLayout.ElasticDragDismissCallback {
//        @Override
//        public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {
//            super.onDrag(elasticOffset, elasticOffsetPixels, rawOffset, rawOffsetPixels);
//            Log.d(TAG, String.format("%f, %f, %f, %f",
//                    elasticOffset, elasticOffsetPixels, rawOffset, rawOffsetPixels));
//        }
//
//        @Override
//        public void onDragDismissed() {
//            close();
//        }
//    }
//}
