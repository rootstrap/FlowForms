package com.rootstrap.flowforms.core.form

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.field.FieldDefinition
import com.rootstrap.flowforms.core.field.FlowField
import com.rootstrap.flowforms.core.validation.ValidationsCancelledException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

/**
 * FlowForm: Reactive declarative form intended to reduce the boilerplate code required to manage form
 * and field status changes.
 *
 * Based on flows, its status is updated automatically when any of the field's inner status changes.
 */
class FlowForm internal constructor(
    private val _fields : MutableStateFlow<Map<String, FieldDefinition>> = MutableStateFlow(emptyMap()),
    private var coroutineDispatcher: CoroutineDispatcher? = null
) {

    /**
     * Flow with the map of fields contained in this form.
     * Initially it is an empty map until [setFields] is called with some fields in it.
     */
    val fields = _fields.asStateFlow()

    /**
     * flow with the status of the form. Every value emitted by this form represents it's current
     * status, and values are emitted when this form's fields changes their inner status.
     *
     * The form status is initially [FormStatus.Unmodified], and becomes [FormStatus.Incomplete] as
     * its fields begin to change their inner status. It becomes [CORRECT] when
     * all the fields status are [CORRECT].
     *
     * If only one field is [INCORRECT] then the form status becomes [FormStatus.Incorrect] even if
     * the other fields are [UNMODIFIED]
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val status: Flow<FormStatus> = _fields.flatMapLatest { fieldsMap ->
        combine(fieldsMap.values.map { it.status }) { fieldStatuses ->
            var unmodifiedFieldStatuses = 0
            var correctFieldStatuses = 0
            var failedFieldStatus = 0

            for (fieldStatus in fieldStatuses) {
                when (fieldStatus.code) {
                    UNMODIFIED -> unmodifiedFieldStatuses++
                    CORRECT -> correctFieldStatuses++
                    else -> {
                        failedFieldStatus++
                        // fail fast. It is not required to check the other fields at the moment. Update if needed.
                        break
                    }
                }
            }

            when {
                unmodifiedFieldStatuses == fieldStatuses.size -> FormStatus.Unmodified
                correctFieldStatuses == fieldStatuses.size -> FormStatus.Correct
                failedFieldStatus > 0 -> FormStatus.Incorrect
                else -> FormStatus.Incomplete
            }
        }
    }

    /**
     * Trigger onValueChange validations on the specified [FlowField] (if it exists in this form).
     * Returns the result of the validations or false if the field does not exist.
     *
     * If this method is called again while still being processing the validations it will return false
     * in the first call, as the validations for such call were cancelled, however, this does not
     * affect the returned result of the newest call.
     *
     * _for more information on this behavior please refer to [FlowField.triggerOnValueChangeValidations]._
     */
    suspend fun validateOnValueChange(fieldId : String) : Boolean {
        return try {
            this._fields.value[fieldId]?.triggerOnValueChangeValidations(this.coroutineDispatcher) ?: false
        } catch (ex : ValidationsCancelledException) {
            false
        }
    }

    /**
     * Trigger onBlur validations on the specified field (if it exists in this form).
     * Returns the result of the validations or false if the field does not exist.
     *
     * If this method is called again while still being processing the validations it will return false
     * in the first call, as the validations for such call were cancelled, however, this does not
     * affect the returned result of the newest call.
     *
     * _for more information on this behavior please refer to [FlowField.triggerOnBlurValidations]._
     */
    suspend fun validateOnBlur(fieldId : String) : Boolean {
        return try {
            this._fields.value[fieldId]?.triggerOnBlurValidations(this.coroutineDispatcher) ?: false
        } catch (ex : ValidationsCancelledException) {
            false
        }
    }

    /**
     * Trigger onFocus validations on the specified field (if it exists in this form).
     * Returns the result of the validations or false if the field does not exist.
     *
     * If this method is called again while still being processing the validations it will return false
     * in the first call, as the validations for such call were cancelled, however, this does not
     * affect the returned result of the newest call.
     *
     * _for more information on this behavior please refer to [FlowField.triggerOnFocusValidations]._
     */
    suspend fun validateOnFocus(fieldId : String) : Boolean {
        return try {
            this._fields.value[fieldId]?.triggerOnFocusValidations(this.coroutineDispatcher) ?: false
        } catch (ex : ValidationsCancelledException) {
            false
        }
    }

    /**
     * Trigger all the validations on all the fields in this form.
     *
     * For every field, it will first trigger onValueChange validations. If the field is correct after
     * all those validations were completed (including async validations), it will continue
     * with the onFocus validations and if it is still correct then it will trigger onBlur validations.
     *
     */
    suspend fun validateAllFields() {
        try {
            this._fields.value.forEach {
                var fieldIsValid = validateOnValueChange(it.key)
                fieldIsValid = if (fieldIsValid) validateOnFocus(it.key) else false
                if (fieldIsValid) validateOnBlur(it.key)
            }
        } catch (ignored : ValidationsCancelledException) { }
    }

}
