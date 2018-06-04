package org.beatonma.lib.ui.activity.popup;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.beatonma.lib.ui.activity.R;
import org.beatonma.lib.ui.activity.databinding.ActivityPopupBinding;
import org.jetbrains.annotations.NotNull;

import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;

/**
 * Created by Michael on 22/07/2016.
 */
public abstract class PopupActivity extends BasePopupActivity {
    protected final static String TAG = "PopupActivity";

    private ActivityPopupBinding mBinding;

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
    }

    protected ActivityPopupBinding getParentBinding() {
        return mBinding;
    }

    @NotNull
    @Override
    public AppCompatButton getNegativeButton() {
        return mBinding.buttonNegative;
    }

    @NotNull
    @Override
    public AppCompatButton getPositiveButton() {
        return mBinding.buttonPositive;
    }

    @NotNull
    @Override
    public AppCompatButton getCustomActionButton() {
        return mBinding.buttonCustomAction;
    }
}