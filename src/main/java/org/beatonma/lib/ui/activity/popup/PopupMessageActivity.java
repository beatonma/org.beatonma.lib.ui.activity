package org.beatonma.lib.ui.activity.popup;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import org.beatonma.lib.ui.activity.R;
import org.beatonma.lib.ui.activity.databinding.ActivityPopupMessageBinding;

import androidx.databinding.ViewDataBinding;

/**
 * Created by Michael on 08/05/2017.
 * A simple dialog with a custom message and button text
 */
public class PopupMessageActivity extends PopupActivity<ActivityPopupMessageBinding> {
    protected final static String TAG = "PopupMessageActivity";

    public final static String EXTRA_MESSAGE = "extra_message";
    public final static String EXTRA_POSITIVE_BUTTON = "extra_positive_button";
    public final static String EXTRA_NEGATIVE_BUTTON = "extra_negative_button";
    public final static String EXTRA_SHOW_NEGATIVE = "extra_show_negative";

    private ActivityPopupMessageBinding mBinding;
    private Message mMessage;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_popup_message;
    }

    @Override
    protected void initLayout(final ViewDataBinding binding) {
        mBinding = (ActivityPopupMessageBinding) binding;

        mBinding.setMessage(mMessage);

        mBinding.positiveButton.setOnClickListener(v -> {
            setResult(Activity.RESULT_OK);
            close();
        });

        mBinding.negativeButton.setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            close();
        });
    }

    @Override
    protected void initExtras(final Bundle extras) {
        super.initExtras(extras);

        if (extras != null) {
            final Object messageObj = extras.get(EXTRA_MESSAGE);
            String message = null;
            if (messageObj != null) {
                if (messageObj instanceof String) {
                    message = (String) messageObj;
                }
                else if (messageObj instanceof Integer) {
                    int resID = (int) messageObj;
                    if (resID != 0) {
                        message = getString(resID);
                    }
                }
            }

            mMessage = new Message()
                    .showNegative(extras.getBoolean(EXTRA_SHOW_NEGATIVE, false))
                    .message(message)
                    .positiveButtonText(extras.getString(EXTRA_POSITIVE_BUTTON))
                    .negativeButtonText(extras.getString(EXTRA_NEGATIVE_BUTTON));
        }
    }

    @Override
    protected ActivityPopupMessageBinding getBinding() {
        return mBinding;
    }

    public static class Message {
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private boolean showNegative;

        public String message() {
            return message;
        }

        public Message message(final String message) {
            this.message = message;
            return this;
        }

        public String positiveButtonText() {
            return positiveButtonText;
        }

        public Message positiveButtonText(final String positiveButtonText) {
            this.positiveButtonText = positiveButtonText;
            return this;
        }

        public String negativeButtonText() {
            return negativeButtonText;
        }

        public Message negativeButtonText(final String negativeButtonText) {
            this.negativeButtonText = negativeButtonText;
            if (!TextUtils.isEmpty(negativeButtonText)) {
                this.showNegative(true);
            }
            return this;
        }

        public boolean showNegative() {
            return showNegative;
        }

        public Message showNegative(final boolean showNegative) {
            this.showNegative = showNegative;
            return this;
        }
    }
}
