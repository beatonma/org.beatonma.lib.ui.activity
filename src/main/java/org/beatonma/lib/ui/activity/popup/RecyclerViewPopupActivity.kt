@file:JvmName("RecyclerViewPopupActivity")

package org.beatonma.lib.ui.activity.popup

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import org.beatonma.lib.ui.activity.BuildConfig
import org.beatonma.lib.ui.activity.R
import org.beatonma.lib.ui.activity.databinding.ActivityPopupRecyclerviewBinding
import org.beatonma.lib.util.kotlin.extensions.colorCompat

/**
 * Reimplementation of PopupActivity to simplify setup of popups that only require a [RecyclerView]
 * (and the default popup action buttons) to display their content.
 */
abstract class RecyclerViewPopupActivity: BasePopupActivity() {
    protected lateinit var binding: ActivityPopupRecyclerviewBinding
        private set

    override val layoutID: Int
        get() = R.layout.activity_popup_recyclerview

    @Deprecated("RecyclerViewPopupActivity content should be accessed with RecyclerView",
            ReplaceWith("Implement setup(recyclerView)"))
    final override val contentLayoutID: Int
        get() = 0

    override val overlay: View
        get() = binding.overlay
    override val card: ViewGroup
        get() = binding.card
    override val titleView: TextView
        get() = binding.title

    @Deprecated("RecyclerViewPopupActivity content should be accessed with RecyclerView",
            ReplaceWith("binding.recyclerview"))
    final override val cardContentContainer: ViewGroup
        get() = binding.recyclerview

    override val positiveButton: AppCompatButton
        get() = binding.buttonPositive
    override val negativeButton: AppCompatButton
        get() = binding.buttonNegative
    override val customActionButton: AppCompatButton
        get() = binding.buttonCustomAction
    override val buttonSpacer: View
        get() = binding.buttonSpacer
    val recyclerView: RecyclerView
        get() = binding.recyclerview

    @SuppressLint("MissingSuperCall")
    @CallSuper
    override fun initLayout(binding: ViewDataBinding) {
        this.binding = binding as ActivityPopupRecyclerviewBinding
        setTitle(cardTitle)

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

        setup(recyclerView)
    }

    final override fun initContentLayout(binding: ViewDataBinding) {}

    abstract fun setup(recyclerView: RecyclerView)
}

/**
 * [RecyclerView] that wraps its children up to a maximum height of popup_content_max_height.
 */
class PopupRecyclerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    private val maxHeight: Int
        get() = resources.getDimensionPixelSize(R.dimen.popup_content_max_height)

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(
                widthSpec,
                MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST))
    }
}
