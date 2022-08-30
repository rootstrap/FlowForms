package com.rootstrap.flowforms.core.form

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.field.FlowField
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * FlowForm: Reactive declarative form intended to reduce the boilerplate code required to manage form
 * and field status changes.
 *
 * Based on flows, its status is updated automatically when any of the field's inner status changes.
 */
open class FlowForm {

    private val _fields = MutableStateFlow(mapOf<String, FlowField>())
    private var coroutineDispatcher: CoroutineDispatcher? = null

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
     * Defines the map of fields contained in this form, associating them by their ids.
     *
     * @return this form to allow method chaining and declarative construction.
     */
    fun setFields(vararg fields: FlowField) : FlowForm {
        val fieldsMap = fields.associateBy { it.id }
        this._fields.value = fieldsMap
        return this
    }

    /**
     * Sets a coroutineDispatcher that will be used when triggering the [FlowField] validations,
     * by default it is used to run asynchronous validations in the
     * [DefaultFieldValidationBehavior][com.rootstrap.flowforms.core.field.DefaultFieldValidationBehavior].
     *
     * @return this form to allow method chaining and declarative construction.
     */
    fun setDispatcher(coroutineDispatcher: CoroutineDispatcher?) : FlowForm {
        this.coroutineDispatcher = coroutineDispatcher
        return this
    }

    /**
     * Trigger onValueChange validations on the specified field (if it exists in this form).
     * Returns the result of the validations or false if the field does not exist.
     */
    suspend fun validateOnValueChange(fieldId : String) : Boolean {
        return this._fields.value[fieldId]?.triggerOnValueChangeValidations(this.coroutineDispatcher) ?: false
    }

    /**
     * Trigger onBlur validations on the specified field (if it exists in this form).
     * Returns the result of the validations or false if the field does not exist.
     */
    suspend fun validateOnBlur(fieldId : String) : Boolean {
        return this._fields.value[fieldId]?.triggerOnBlurValidations(this.coroutineDispatcher) ?: false
    }

    /**
     * Trigger onFocus validations on the specified field (if it exists in this form).
     * Returns the result of the validations or false if the field does not exist.
     */
    suspend fun validateOnFocus(fieldId : String) : Boolean {
        return this._fields.value[fieldId]?.triggerOnFocusValidations(this.coroutineDispatcher) ?: false
    }

    /**
     * Trigger all the validations on all the fields in this form.
     *
     * First it will trigger onValueChange validations. If the field is correct, it will continue
     * with the onFocus validations and if it is stll correct then it will trigger onBlur validations.
     */
    suspend fun validateAllFields() {
        this._fields.value.forEach {
            var fieldIsValid = validateOnValueChange(it.key)
            fieldIsValid = if (fieldIsValid) validateOnFocus(it.key) else false
            if (fieldIsValid) validateOnBlur(it.key)
        }
    }

}
