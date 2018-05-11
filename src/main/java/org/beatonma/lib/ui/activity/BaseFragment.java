package org.beatonma.lib.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

/**
 * Created by Michael on 05/08/2016.
 */
@SuppressWarnings("unused")
public abstract class BaseFragment extends Fragment {
    protected final static String TAG = "BaseFragment";
    protected WeakReference<Context> mWeakContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeakContext = new WeakReference<>(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        final ViewDataBinding binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);

        init(binding);
        postInit();

        return binding.getRoot();
    }

    protected abstract void init(ViewDataBinding binding);
    protected abstract int getLayoutId();

    @CallSuper
    public void postInit() {

    }

    public BaseActivity getParentActivity() {
        final Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            return (BaseActivity) activity;
        }
        throw new ClassCastException("Parent activity does not extend BaseActivity");
    }

    public void showSnackbar(final String message) {
        final Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).showSnackbar(message);
        }
        else {
            Log.e(TAG, "Cannot show snackbar: activity does not extend BaseActivity");
        }
    }

    public Snackbar getSnackbar(final String message) {
        final Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            return ((BaseActivity) activity).getSnackbar(message);
        }
        else {
            Log.e(TAG, "Cannot show snackbar: activity does not extend BaseActivity");
            return null;
        }
    }

    public View getSharedViewStub() {
        return getParentActivity().getSharedViewStub();
    }
}
