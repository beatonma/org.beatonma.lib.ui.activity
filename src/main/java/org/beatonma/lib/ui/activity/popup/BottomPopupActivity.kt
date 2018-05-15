package org.beatonma.lib.ui.activity.popup

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import org.beatonma.lib.ui.activity.R
import org.beatonma.lib.ui.activity.databinding.ActivityPopupBottomBinding



abstract class BottomPopupActivity<T : ViewDataBinding>: BasePopupActivity<T>() {
    var mBinding: ActivityPopupBottomBinding? = null

    override fun getPopupLayoutId(): Int {
        return R.layout.activity_popup_bottom
    }

    override fun getOverlay(): View? {
        return mBinding!!.overlay
    }

    override fun getCard(): ViewGroup {
        return mBinding!!.card
    }

    override fun getTitleView(): TextView{
        return mBinding!!.title
    }

    override fun getCardContentContainer(): ViewGroup {
        return mBinding!!.contentContainer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this, popupLayoutId)
        super.onCreate(savedInstanceState)
    }

//    override fun getBinding(): ActivityPopupBottomBinding? {
//        return mBinding
//    }
}
