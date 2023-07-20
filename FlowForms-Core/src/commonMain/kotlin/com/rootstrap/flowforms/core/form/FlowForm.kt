package com.rootstrap.flowforms.core.form

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.field.FieldDefinition
import com.rootstrap.flowforms.core.field.FieldDefinition.ValidationType
import com.rootstrap.flowforms.core.field.FieldDefinition.ValidationType.ON_BLUR
import com.rootstrap.flowforms.core.field.FieldDefinition.ValidationType.ON_FOCUS
import com.rootstrap.flowforms.core.field.FieldDefinition.ValidationType.ON_VALUE_CHANGE
import com.rootstrap.flowforms.core.field.FlowField
import com.rootstrap.flowforms.core.validation.CrossFieldValidation
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
     */
    @NativeCoroutines
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
    @NativeCoroutines
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
     * Returns the field with the given ID, or null if it doesn't exist on this form
     * at this specific moment.
     *
     * @param id the ID of the field to retrieve.
     * @return a FieldDefinition instance for the given ID, or null.
     */
    fun field(id: String) = fields.value[id]

    /**
     * Convenient way to get all the fields' status flows on this form.
     *
     * Basically provides a list of [Flow]s where each [Flow] represent one of
     * this form's field's status.
     *
     */
    val fieldStatusFlows
        get() = fields.value.values.map { it.status }

    /**
     * Trigger onValueChange validations on the specified [FlowField] (if it exists in this form).
     *
     * For additional information please refer to [DOC_FIELD_VALIDATION_BEHAVIOR]
     *
     * @param fieldId id of the field to validate.
     */
    @NativeCoroutines
    suspend fun validateOnValueChange(fieldId : String) : Boolean {
        return try {
            val field = _fields.value[fieldId] ?: return false

            val success = field.triggerOnValueChangeValidations(coroutineDispatcher)
            if (success) {
                getAndTriggerCrossFieldValidations(field, ON_VALUE_CHANGE)
            }
            success
        } catch (ex : ValidationsCancelledException) {
            false
        }
    }

    /**
     * Trigger onBlur validations on the specified field (if it exists in this form).
     *
     * For additional information please refer to [DOC_FIELD_VALIDATION_BEHAVIOR]
     *
     * @param fieldId id of the field to validate.
     */
    @NativeCoroutines
    suspend fun validateOnBlur(fieldId : String) : Boolean {
        return try {
            val field = this._fields.value[fieldId] ?: return false
            val success = field.triggerOnBlurValidations(this.coroutineDispatcher)
            if (success) {
                getAndTriggerCrossFieldValidations(field, ON_BLUR)
            }
            success
        } catch (ex : ValidationsCancelledException) {
            false
        }
    }

    /**
     * Trigger onFocus validations on the specified field (if it exists in this form).
     *
     * For additional information please refer to [DOC_FIELD_VALIDATION_BEHAVIOR]
     *
     * @param fieldId id of the field to validate.
     */
    @NativeCoroutines
    suspend fun validateOnFocus(fieldId : String) : Boolean {
        return try {
            val field = this._fields.value[fieldId] ?: return false
            val success = field.triggerOnFocusValidations(this.coroutineDispatcher)
            if (success) {
                getAndTriggerCrossFieldValidations(field, ON_FOCUS)
            }
            success
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
     * It is important to note that the field order is not guaranteed during the validation process,
     * but all fields are validated, no matter the result of the previous one.
     *
     * @return true if all the validations were completed successfully, false otherwise.
     */
    @NativeCoroutines
    suspend fun validateAllFields() : Boolean {
        val validatedFieldsMap = mutableMapOf<String, Boolean>()
        this._fields.value.keys.forEach { id ->
            try {
                validatedFieldsMap[id] = validateOnValueChange(id)
                        && validateOnFocus(id)
                        && validateOnBlur(id)
            } catch (ignored: ValidationsCancelledException) {
                validatedFieldsMap[id] = false
            }
        }
        return validatedFieldsMap.values.all { it }
    }

    private suspend fun getAndTriggerCrossFieldValidations(
        field : FieldDefinition,
        validationType: ValidationType
    ) {
        val fieldValidations = when (validationType) {
            ON_VALUE_CHANGE -> field.onValueChangeValidations
            ON_FOCUS -> field.onFocusValidations
            ON_BLUR -> field.onBlurValidations
        }

        val crossFieldValidations = fieldValidations
            .filterIsInstance<CrossFieldValidation>()
            .groupBy { it.targetFieldId }

        for (validationsPerField in crossFieldValidations) {
            val targetField = _fields.value[validationsPerField.key] ?: continue
            val validations = validationsPerField.value.map { it.validation }
            if (targetField.getCurrentStatus().code != UNMODIFIED) {
                when (validationType) {
                    ON_VALUE_CHANGE ->
                        targetField.triggerOnValueChangeValidations(coroutineDispatcher, validations)
                    ON_FOCUS ->
                        targetField.triggerOnFocusValidations(coroutineDispatcher, validations)
                    ON_BLUR ->
                        targetField.triggerOnBlurValidations(coroutineDispatcher, validations)
                }
            }
        }
    }

}

/**
 * Returns the result of the validations or false if the field does not exist or is not valid.
 *
 * If this method is called again while still being processing the validations it will return false
 * in the first call, as the validations for such call were cancelled. However, this does not
 * affect the returned result of the newest call, which will begin to process the validations
 * from scratch.
 *
 * If the field has crossField validations, those will not be executed on this field but on their
 * target fields. Such cross-field validations are only ran if this field (whose ID was given
 * by parameter) is valid.
 * In addition to that condition, the cross-field validations are executed only if the target
 * field's status is [CORRECT]
 *
 */
private const val DOC_FIELD_VALIDATION_BEHAVIOR = true
