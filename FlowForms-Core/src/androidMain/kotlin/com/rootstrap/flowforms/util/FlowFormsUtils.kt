package com.rootstrap.flowforms.util

import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.*
import com.rootstrap.flowforms.core.field.FlowField
import com.rootstrap.flowforms.core.form.FlowForm
import com.rootstrap.flowforms.core.validation.Validation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Defines a vararg of functions which will be executed repeating on this [AppCompatActivity]'s lifecycle
 * (via repeatOnLifecycle) based on the given lifecycle state, inside this [AppCompatActivity]'s lifecycleScope.
 *
 * Intended to eliminate all the boilerplate code needed to collect an undetermined amount of flows.
 *
 * @param blocks suspend functions which will be executed inside this activity's lifecycleScope.
 * @param lifecycleState lifecycle phase used by the [repeatOnLifecycle] call. By default it is [Lifecycle.State.STARTED]
 */
fun AppCompatActivity.repeatOnLifeCycleScope(
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

/**
 * Bind an undetermined number of [View]s [Pair]ed to [FlowField] IDs. Automatically triggering
 * the corresponding validations (onValueChanged, onBlur, onFocus, etc) when needed.
 *
 * You can bind any kind of [View], but only those in the following list will be accepted.
 * * EditText
 *
 * @param coroutineScope used to trigger the [FlowField]'s [Validation]s
 * @param bindPairs a Pair of [View]s and [String]s, each View belonging to each Field in the form
 * by the paired ID.
 * @throws [IllegalArgumentException] where there are non-supported views in the pairs.
 */
fun FlowForm.bind(coroutineScope : CoroutineScope, vararg bindPairs : Pair<View, String>) {
    bindPairs.forEach {
        when (it.first) {
            is EditText -> bind(coroutineScope, it.first as EditText, it.second)
            else -> throw IllegalArgumentException("View for fieldID ${it.second} is not supported on FlowForms")
        }
    }
}

/**
 * Bind an [EditText] to a [FlowField] ID. Automatically triggering
 * the corresponding validations (onValueChanged, onBlur, onFocus, etc) when needed.
 *
 * @param coroutineScope used to trigger the [FlowField]'s [Validation]s
 */
fun FlowForm.bind(coroutineScope : CoroutineScope, editText : EditText, fieldId: String) {
    editText.doAfterTextChanged {
        coroutineScope.launch {
            validateOnValueChange(fieldId)
        }
    }
    editText.onFocusChangeListener = OnFieldFocusChangeListener(editText.onFocusChangeListener) { _, hasFocus ->
        coroutineScope.launch {
            if (hasFocus) validateOnFocus(fieldId) else validateOnBlur(fieldId)
        }
    }
}

/**
 * Bind an undetermined number of [LiveData]s [Pair]ed to [FlowField] IDs. Automatically
 * observing and triggering the corresponding onValueChanged [Validation]s when the LiveData emits
 * a value.
 *
 * It only triggers onValueChanged [Validation]s. The [LiveData]'s value is ignored, as the validations
 * use the field's current value.
 *
 * @param lifeCycleOwner used to observe the [LiveData]s
 * @param coroutineScope used to trigger the [FlowField]'s [Validation]s
 * @param bindPairs [Pair]s of [LiveData] to [String], each LiveData belonging to each field in the
 * form by the paired ID
 */
fun FlowForm.bind(lifeCycleOwner : LifecycleOwner, coroutineScope : CoroutineScope, vararg bindPairs : Pair<LiveData<*>, String>) {
    bindPairs.forEach {
        it.first.observe(lifeCycleOwner) { _ ->
            coroutineScope.launch {
                validateOnValueChange(it.second)
            }
        }
    }
}
