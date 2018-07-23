package org.beatonma.lib.ui.activity.popup

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.ViewDataBinding
import org.beatonma.lib.ui.activity.R
import org.beatonma.lib.ui.activity.databinding.ActivityPopupBinding

/**
 * Created by Michael on 22/07/2016.
 */
abstract class PopupActivity : BasePopupActivity() {
    protected lateinit var parentBinding: ActivityPopupBinding
        private set

    override val layoutID: Int
        get() = R.layout.activity_popup

    override val overlay: View
        get() = parentBinding.overlay

    override val card: ViewGroup
        get() = parentBinding.card

    override val titleView: TextView
        get() = parentBinding.title

    override val cardContentContainer: ViewGroup
        get() = parentBinding.contentContainer

    override val negativeButton: AppCompatButton
        get() = parentBinding.buttonNegative

    override val positiveButton: AppCompatButton
        get() = parentBinding.buttonPositive

    override val customActionButton: AppCompatButton
        get() = parentBinding.buttonCustomAction

    override val buttonSpacer: View
        get() = parentBinding.buttonSpacer

    override fun initLayout(binding: ViewDataBinding) {
        parentBinding = binding as ActivityPopupBinding
        super.initLayout(binding)
    }
}
