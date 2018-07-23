@file:JvmName("PopupTestActivity")

package org.beatonma.lib.ui.activity.popup

import android.view.View
import androidx.databinding.ViewDataBinding
import org.beatonma.lib.ui.activity.R
import org.beatonma.lib.ui.activity.startActivity
import org.beatonma.lib.util.kotlin.extensions.pxToDp
import org.beatonma.lib.util.kotlin.extensions.toast

class SmallPopupTestActivity : PopupActivity() {
    override val contentLayoutID: Int
        get() = R.layout.popup_small

    override fun initContentLayout(binding: ViewDataBinding) {
        setTitle("Small text")
        onPositiveClick("+", listener = View.OnClickListener { view ->
            view.startActivity(MediumPopupTestActivity::class)
        })
        onNegativeClick("-", listener = View.OnClickListener { onBackPressed() })
        onCustomActionClick("#", listener = View.OnClickListener {  })

        binding.root.post {
            toast("scrollview height: ${pxToDp(findViewById<View>(R.id.content_scroll_wrapper).measuredHeight)}dp")
        }
    }
}

class MediumPopupTestActivity : PopupActivity() {
    override val contentLayoutID: Int
        get() = R.layout.popup_medium

    override fun initContentLayout(binding: ViewDataBinding) {
        setTitle("Medium text")
        onPositiveClick("+", listener = View.OnClickListener {view ->
            view.startActivity(LargePopupTestActivity::class)
        })
        onNegativeClick("-", listener = View.OnClickListener { onBackPressed() })
        onCustomActionClick("#", listener = View.OnClickListener {  })

        binding.root.post {
            toast("scrollview height: ${pxToDp(findViewById<View>(R.id.content_scroll_wrapper).measuredHeight)}dp")
        }
    }


}

class LargePopupTestActivity : PopupActivity() {
    override val contentLayoutID: Int
        get() = R.layout.popup_large

    override fun initContentLayout(binding: ViewDataBinding) {
        setTitle("Long text")
        onPositiveClick("+", listener = View.OnClickListener { view ->
            view.startActivity(SmallPopupTestActivity::class)
        })
        onNegativeClick("-", listener = View.OnClickListener { onBackPressed() })
        onCustomActionClick("#", listener = View.OnClickListener {  })

        binding.root.post {
            toast("scrollview height: ${pxToDp(findViewById<View>(R.id.content_scroll_wrapper).measuredHeight)}dp")
        }
    }
}
