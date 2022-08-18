package com.rootstrap.flowforms.util

import android.view.View

class OnFocusChangeListenerDecorator(
    private val onFocusChangeListener : View.OnFocusChangeListener?,
    private val onFocusChangePostListenerBehavior : (view: View?, hasFocus: Boolean) -> Unit
) : View.OnFocusChangeListener {

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        onFocusChangeListener?.onFocusChange(view, hasFocus)
        onFocusChangePostListenerBehavior(view, hasFocus)
    }

}
