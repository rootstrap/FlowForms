package com.rootstrap.flowforms.util

import android.view.View

class OnFieldFocusChangeListener(
    private val onFocusChangeListener : View.OnFocusChangeListener?,
    private val onFieldFocusChangeFun : (view: View?, hasFocus: Boolean) -> Unit
) : View.OnFocusChangeListener {

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        onFocusChangeListener?.onFocusChange(view, hasFocus)
        onFieldFocusChangeFun(view, hasFocus)
    }

}
