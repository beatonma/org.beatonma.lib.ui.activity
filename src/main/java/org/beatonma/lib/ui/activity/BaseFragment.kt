package org.beatonma.lib.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Michael on 05/08/2016.
 */
abstract class BaseFragment : Fragment() {
    companion object {
        protected const val TAG = "BaseFragment"
    }

    protected abstract val layoutId: Int

    val parentActivity: BaseActivity by lazy {
        if (activity is BaseActivity) {
            activity as BaseActivity
        } else {
            throw ClassCastException("Parent activity does not extend BaseActivity")
        }
    }
    val sharedViewStub: View? by lazy { parentActivity.sharedViewStub }

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater,
                              @Nullable container: ViewGroup?,
                              @Nullable savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutId, container, false)

        init(binding)

        return binding.root
    }

    protected abstract fun init(binding: ViewDataBinding)

    fun showSnackbar(message: String) {
        val activity = activity
        (activity as? BaseActivity)?.showSnackbar(message)
                ?: Log.e(TAG, "Cannot show snackbar: activity does not extend BaseActivity")
    }

    fun getSnackbar(message: String): Snackbar? {
        val activity = activity
        return if (activity is BaseActivity) {
            activity.getSnackbar(message)
        } else {
            Log.e(TAG, "Cannot show snackbar: activity does not extend BaseActivity")
            null
        }
    }
}
