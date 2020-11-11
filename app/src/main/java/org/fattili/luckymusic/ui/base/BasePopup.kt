package org.fattili.luckymusic.ui.base

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.PopupWindow
import org.fattili.luckymusic.R

abstract class BasePopup {
    private var pWindow: PopupWindow? = null
    private var window: Window? = null

    var context: Context? = null


    var contentView: View? = null

    fun miss() {
        if (pWindow != null && pWindow!!.isShowing) {
            pWindow!!.dismiss()
        }
    }

    val isShowing: Boolean
        get() = pWindow != null && pWindow!!.isShowing

    fun popupChoose(activity: Activity?,view: View?) {
        context = activity
        this.window = activity?.window
        if (pWindow != null && pWindow!!.isShowing) {
            miss()
        } else {
            contentView = activity?.layoutInflater?.inflate(layoutId, null)
            initView()
            initData()
            pWindow = PopupWindow(contentView, -1, -1)
            pWindow?.animationStyle = R.style.lm_pop_animation
            pWindow?.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
            pWindow?.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            pWindow?.isClippingEnabled = true
            backgroundAlpha(0.5f)
            pWindow?.showAtLocation(view, Gravity.TOP, 0, 0)
            pWindow?.setOnDismissListener { backgroundAlpha(1.0f) }
        }
    }

    fun getView(): View? {
        return contentView
    }

    private fun backgroundAlpha(bgAlpha: Float) {

        val lp = window!!.attributes
        lp.alpha = bgAlpha // 0.0-1.0
        window!!.attributes = lp
    }

    open fun initData() {
    }

    protected abstract val layoutId: Int

    abstract fun initView()

}