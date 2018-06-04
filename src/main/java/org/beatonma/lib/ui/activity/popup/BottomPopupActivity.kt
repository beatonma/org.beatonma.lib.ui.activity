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
    var mBinding: ActivityPopupBottomBinding? = null

    override val popupLayoutId: Int = R.layout.activity_popup_bottom
    override val overlay: View = mBinding!!.overlay
    override val card: ViewGroup = mBinding!!.card
    override val titleView: TextView = mBinding!!.title
    override val cardContentContainer: ViewGroup = mBinding!!.contentContainer
    override val positiveButton: AppCompatButton = mBinding!!.buttonPositive
    override val negativeButton: AppCompatButton = mBinding!!.buttonPositive
    override val customActionButton: AppCompatButton = mBinding!!.buttonPositive


    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this, popupLayoutId)
        super.onCreate(savedInstanceState)
    }

//    override fun getBinding(): ActivityPopupBottomBinding? {
//        return mBinding
//    }
}
