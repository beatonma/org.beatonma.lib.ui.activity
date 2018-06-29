package org.beatonma.lib.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Michael on 05/08/2016.
 */
private const val TAG = "BaseFragment"
abstract class BaseFragment : Fragment() {
    protected abstract val layoutID: Int

    val parentActivity: BaseActivity
        get() = activity as? BaseActivity
                ?: throw ClassCastException("Parent activity does not extend BaseActivity")
    val sharedViewStub: View?
        get() = parentActivity.sharedViewStub

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutID, container, false)

        init(binding)

        return binding.root
    }


    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        (view.parent as? ViewGroup)?.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    protected abstract fun init(binding: ViewDataBinding)

    fun showSnackbar(message: String) {
        (activity as? BaseActivity)?.showSnackbar(message)
                ?: Log.e(TAG, "Cannot show snackbar: activity does not extend BaseActivity")
    }

    fun getSnackbar(message: String): Snackbar? {
        return (activity as? BaseActivity)?.getSnackbar(message)
    }
}
