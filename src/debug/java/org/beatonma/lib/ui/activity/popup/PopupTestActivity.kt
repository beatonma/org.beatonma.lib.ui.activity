@file:JvmName("PopupTestActivity")

package org.beatonma.lib.ui.activity.popup

import android.view.View
import androidx.annotation.UiThread
import androidx.databinding.ViewDataBinding
import org.beatonma.lib.ui.activity.R
import org.beatonma.lib.ui.activity.startActivity


abstract class BaseTestPopup: PopupActivity() {
    override fun initContentLayout(binding: ViewDataBinding) {
        showButtons()
        showTitle()
    }

    fun showButtons(show: Boolean = true) {
        if (show) {
            showPositiveButton()
            showNegativeButton()
            showCustomActionButton()
        } else hideButtons()
    }
    abstract fun showTitle(show: Boolean = true)

    @UiThread abstract fun showPositiveButton()
    @UiThread abstract fun showNegativeButton()
    @UiThread abstract fun showCustomActionButton()

    fun hideButtons() {
        runOnUiThread {
            onPositiveClick()
            onNegativeClick()
            onCustomActionClick()
        }
    }
}

class SmallPopupTestActivity : BaseTestPopup() {
    override val contentLayoutID: Int
        get() = R.layout.popup_small


    override fun showPositiveButton() {
        runOnUiThread {
            onPositiveClick("+", listener = View.OnClickListener { view ->
                view.startActivity(MediumPopupTestActivity::class)
            }, animate = false)
        }
    }

    override fun showNegativeButton() {
        runOnUiThread {
            onNegativeClick("-", listener = View.OnClickListener { onBackPressed() }, animate = false)
        }
    }

    override fun showCustomActionButton() {
        runOnUiThread {
            onCustomActionClick("#", listener = View.OnClickListener { }, animate = false)
        }
    }

    override fun showTitle(show: Boolean) {
        runOnUiThread {
            setTitle(if (show) "Small text" else null)
        }
    }
}

class MediumPopupTestActivity : BaseTestPopup() {
    override val contentLayoutID: Int
        get() = R.layout.popup_medium

    override fun showPositiveButton() {
        runOnUiThread {
            onPositiveClick("+", listener = View.OnClickListener { view ->
                view.startActivity(LargePopupTestActivity::class)
            }, animate = false)
        }
    }

    override fun showNegativeButton() {
        runOnUiThread {
            onNegativeClick("-", listener = View.OnClickListener { onBackPressed() }, animate = false)
        }
    }

    override fun showCustomActionButton() {
        runOnUiThread {
            onCustomActionClick("#", listener = View.OnClickListener { }, animate = false)
        }
    }

    override fun showTitle(show: Boolean) {
        runOnUiThread {
            setTitle(if (show) "Medium text" else null)
        }
    }
}

class LargePopupTestActivity : BaseTestPopup() {
    override val contentLayoutID: Int
        get() = R.layout.popup_large

    override fun showPositiveButton() {
        runOnUiThread {
            onPositiveClick("+", listener = View.OnClickListener { view ->
                view.startActivity(SmallPopupTestActivity::class)
            }, animate = false)
        }
    }

    override fun showNegativeButton() {
        runOnUiThread {
            onNegativeClick("-", listener = View.OnClickListener { onBackPressed() }, animate = false)
        }
    }

    override fun showCustomActionButton() {
        runOnUiThread {
            onCustomActionClick("#", listener = View.OnClickListener { }, animate = false)
        }
    }

    override fun showTitle(show: Boolean) {
        runOnUiThread {
            setTitle(if (show) "Large text" else null)
        }
    }
}
