package com.rootstrap.flowforms.util

import com.rootstrap.flowforms.core.field.FieldDefinition
import com.rootstrap.flowforms.core.form.FlowForm
import com.rootstrap.flowforms.core.form.FormStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun FlowForm.validateOnValueChange(
    fieldId : String,
    onCompletion: (Boolean) -> Unit,
) {
    MainScope().launch {
        withContext(Dispatchers.Default) {
            val isValid = validateOnValueChange(fieldId)
            onCompletion(isValid)
        }
    }
}

fun FlowForm.validateOnFocus(
    fieldId : String,
    onCompletion: (Boolean) -> Unit,
) {
    MainScope().launch {
        withContext(Dispatchers.Default) {
            val isValid = validateOnFocus(fieldId)
            onCompletion(isValid)
        }
    }
}

fun FlowForm.validateOnBlur(
    fieldId : String,
    onCompletion: (Boolean) -> Unit,
) {
    MainScope().launch {
        withContext(Dispatchers.Default) {
            val isValid = validateOnBlur(fieldId)
            onCompletion(isValid)
        }
    }
}

fun FlowForm.validateAllFields(
    onCompletion: (Boolean) -> Unit,
) {
    MainScope().launch {
        withContext(Dispatchers.Default) {
            val isValid = validateAllFields()
            onCompletion(isValid)
        }
    }
}

fun FlowForm.onStatusChange(
    onStatusChange: (FormStatus) -> Unit,
) {
    MainScope().launch {
        status.collect { formStatus ->
            onStatusChange(formStatus)
        }
    }
}

fun FlowForm.onFieldsChange(
    onStatusChange: (Map<String, FieldDefinition>) -> Unit,
) {
    MainScope().launch {
        fields.collect { fields ->
            onStatusChange(fields)
        }
    }
}
