package org.beatonma.lib.ui.activity.popup

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import org.beatonma.lib.ui.activity.R
import org.beatonma.lib.ui.activity.databinding.ActivityPopupBottomBinding



abstract class BottomPopupActivity: BasePopupActivity() {
    lateinit var binding: ActivityPopupBottomBinding

    override val layoutID: Int = R.layout.activity_popup_bottom
    override val overlay: View = binding.overlay
    override val card: ViewGroup = binding.card
    override val titleView: TextView = binding.title
    override val cardContentContainer: ViewGroup = binding.contentContainer
    override val positiveButton: AppCompatButton = binding.buttonPositive
    override val negativeButton: AppCompatButton = binding.buttonNegative
    override val customActionButton: AppCompatButton = binding.buttonCustomAction

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, layoutID)
        super.onCreate(savedInstanceState)
    }
}
