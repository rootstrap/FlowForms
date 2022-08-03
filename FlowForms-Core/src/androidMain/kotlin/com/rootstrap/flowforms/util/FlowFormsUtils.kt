package com.rootstrap.flowforms.util

import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rootstrap.flowforms.core.form.FlowForm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

inline fun AppCompatActivity.collectOnLifeCycle(
    vararg blocks : suspend () -> Unit,
    lifecycleState : Lifecycle.State = Lifecycle.State.STARTED
) {
    lifecycleScope.launch {
        repeatOnLifecycle(lifecycleState) {
            blocks.forEach {
                launch {
                    it()
                }
            }
        }
    }
}

fun FlowForm.bind(coroutineScope : CoroutineScope, vararg bindPairs : Pair<View, String>) {
    bindPairs.forEach {
        when {
            it.first is EditText -> bind(coroutineScope, it.first as EditText, it.second)
        }
    }
}

fun FlowForm.bind(coroutineScope : CoroutineScope, editText : EditText, fieldId: String) {
    editText.doAfterTextChanged {
        coroutineScope.launch {
            validateOnValueChange(fieldId)
        }
    }
}
