package org.beatonma.lib.ui.activity.popup

import android.content.Intent
import android.net.http.SslCertificate.restoreState
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import org.beatonma.lib.core.kotlin.extensions.colorCompat
import org.beatonma.lib.core.util.Sdk
import org.beatonma.lib.ui.activity.BaseActivity
import org.beatonma.lib.ui.activity.BuildConfig
import org.beatonma.lib.ui.activity.R
import org.beatonma.lib.ui.activity.SharedView
import org.beatonma.lib.ui.activity.transition.CircularTransform
import org.beatonma.lib.ui.activity.transition.SharedPopupTransform
import org.beatonma.lib.ui.style.Animation
import org.beatonma.lib.ui.style.Interpolate
import org.beatonma.lib.ui.style.Views

abstract class BasePopupActivity : BaseActivity() {

    companion object {
        protected val TAG = "PopupActivity"

        val EXTRA_TITLE = "title"

        val EXTRA_CALLED_FROM_POPUP = "extra_called_from_popup"

        fun getDefaultOptions(title: String): Bundle {
            val options = Bundle()
            options.putString(EXTRA_TITLE, title)
            return options
        }
    }

    private var title: String? = null
    private var calledFromPopup: Boolean = false

    // Card content layout
    protected abstract val layoutId: Int

    // Parent layout
    abstract val popupLayoutId: Int
    abstract val overlay: View
    abstract val card: ViewGroup
    abstract val titleView: TextView
    abstract val cardContentContainer: ViewGroup
    abstract val positiveButton: AppCompatButton
    abstract val negativeButton: AppCompatButton
    abstract val customActionButton: AppCompatButton

    protected abstract fun initLayout(binding: ViewDataBinding)

    override fun onCreate(savedInstanceState: Bundle?) {
        onPreCreate()

        super.onCreate(savedInstanceState)

        val cardContentContainer = cardContentContainer
        val overlay = overlay

        val contentBinding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this), layoutId, null, false)
        cardContentContainer.addView(contentBinding.root)

        setupWindowTransitions()
        initLayout(contentBinding)
        restoreState(savedInstanceState)

        setTitle(title)

        overlay.setBackgroundColor(
                colorCompat(if (calledFromPopup) R.color.Transparent else R.color.DialogOverlay))

        overlay.setOnClickListener { close() }
        if (BuildConfig.DEBUG) {
            overlay.setOnLongClickListener {
                toggleForceNight()
                true
            }
        }

        cardContentContainer.animate()
                .alpha(1f)
                .setStartDelay(Animation.DURATION_THIRD.toLong())
                .setInterpolator(Interpolate.getEnterInterpolator())
                .start()
    }

    /**
     * Called at the beginning of onCreate(), before any layout, etc has been set up
     */
    @CallSuper
    protected open fun onPreCreate() {

    }

    @CallSuper
    override fun initIntent(intent: Intent?) {
        super.initIntent(intent)

        if (intent != null) {
            calledFromPopup = intent.getBooleanExtra(EXTRA_CALLED_FROM_POPUP, false)
            initExtras(intent.extras)
        } else {
            initExtras(null)
        }
    }

    @CallSuper
    override fun initExtras(extras: Bundle?) {
        super.initExtras(extras)

        if (extras == null) {
            return
        }

        val titleObj = extras.get(EXTRA_TITLE)
        if (titleObj != null) {
            if (titleObj is String) {
                title = titleObj
            } else if (titleObj is Int) {
                if (titleObj != 0) {
                    title = getString(titleObj)
                }
            }
        }
    }

    @CallSuper
    open fun setState(savedState: Bundle?) {

    }

    /**
     * Set up any custom window transitions here
     */
    override fun setupWindowTransitions() {
        if (!Sdk.isLollipop) {
            return
        }
        if (calledFromPopup) {
            SharedPopupTransform.setup(this, null)
        } else {
            CircularTransform.setup(this, card)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> close()
        }

        return false
    }

    fun setTitle(title: String?) {
        this.title = title
        titleView.text = title
        Views.hideIfEmpty(titleView)
    }

    override fun setTitle(resId: Int) {
        setTitle(getString(resId))
    }

    fun onPositiveClick(buttonText: Any? = null, listener: View.OnClickListener? = null) {
        positiveButton.apply {
            TransitionManager.beginDelayedTransition(parent as ViewGroup)
            text = when(buttonText) {
                is Int -> getString(buttonText)
                is String -> buttonText
                else -> null
            }
            setOnClickListener(listener)
            visibility = if (listener == null) View.GONE else View.VISIBLE
        }
    }

    fun onNegativeClick(buttonText: Any? = null, listener: View.OnClickListener? = null) {
        negativeButton.apply {
            TransitionManager.beginDelayedTransition(parent as ViewGroup)
            text = when(buttonText) {
                is Int -> getString(buttonText)
                is String -> buttonText
                else -> null
            }
            setOnClickListener(listener)
            visibility = if (listener == null) View.GONE else View.VISIBLE
        }
    }

    fun onCustomActionClick(buttonText: Any? = null, listener: View.OnClickListener? = null) {
        customActionButton.apply {
            TransitionManager.beginDelayedTransition(parent as ViewGroup)
            text = when(buttonText) {
                is Int -> getString(buttonText)
                is String -> buttonText
                else -> null
            }
            setOnClickListener(listener)
            visibility = if (listener == null) View.GONE else View.VISIBLE
        }
    }

    /**
     * Animate the container card when contents change bounds
     */
    protected fun prepareResize() {
        if (Sdk.isKitkat) {
            val changeBounds = TransitionInflater.from(this)
                    .inflateTransition(R.transition.card_changebounds)
            TransitionManager.beginDelayedTransition(card, changeBounds)
        }
    }


    override fun getSharedViews(): Array<SharedView>? {
        return arrayOf(SharedView(card, resources.getString(R.string.transition_card)))
    }
}
