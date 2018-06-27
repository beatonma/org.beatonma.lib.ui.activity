package org.beatonma.lib.ui.activity.popup

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
import org.beatonma.lib.core.kotlin.extensions.hideIfEmpty
import org.beatonma.lib.core.util.Sdk
import org.beatonma.lib.ui.activity.BaseActivity
import org.beatonma.lib.ui.activity.BuildConfig
import org.beatonma.lib.ui.activity.R
import org.beatonma.lib.ui.activity.SharedView
import org.beatonma.lib.ui.activity.transition.CircularTransform
import org.beatonma.lib.ui.activity.transition.SharedPopupTransform
import org.beatonma.lib.ui.style.Animation
import org.beatonma.lib.ui.style.Interpolate


const val EXTRA_TITLE = "title"
const val EXTRA_CALLED_FROM_POPUP = "extra_called_from_popup"

abstract class BasePopupActivity : BaseActivity() {
    override val sharedViews: Array<SharedView>?
        get() = arrayOf(SharedView(card, resources.getString(R.string.transition_card)))

    private var title: String? = null
    private var calledFromPopup: Boolean = false

    // Card content layout
    protected abstract val contentLayoutID: Int

    // Parent layout
    override val layoutID: Int = R.layout.activity_popup
    abstract val overlay: View
    abstract val card: ViewGroup
    abstract val titleView: TextView
    abstract val cardContentContainer: ViewGroup
    abstract val positiveButton: AppCompatButton
    abstract val negativeButton: AppCompatButton
    abstract val customActionButton: AppCompatButton

    /**
     * Set up the popup content
     */
    protected abstract fun initContentLayout(binding: ViewDataBinding)

    @CallSuper
    override fun initLayout(binding: ViewDataBinding) {
        val contentBinding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this), contentLayoutID, null, false)
        cardContentContainer.addView(contentBinding.root)

        initContentLayout(contentBinding)

        setTitle(title)

        overlay.setBackgroundColor(
                colorCompat(if (calledFromPopup) R.color.Transparent
                else R.color.DialogOverlay))

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

//    @CallSuper
//    override fun initIntent(intent: Intent?) {
//        super.initIntent(intent)
//
//        if (intent != null) {
//            calledFromPopup = intent.getBooleanExtra(EXTRA_CALLED_FROM_POPUP, false)
//            initExtras(intent.extras)
//        } else {
//            initExtras(null)
//        }
//    }

    @CallSuper
    override fun initExtras(extras: Bundle?) {
        super.initExtras(extras)

        extras ?: return

        calledFromPopup = extras.getBoolean(EXTRA_CALLED_FROM_POPUP, false)

        val t = extras.get(EXTRA_TITLE)
        when (t) {
            is String -> title = t
            is Int -> title = getString(t)
        }
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
        titleView.hideIfEmpty()
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
}
