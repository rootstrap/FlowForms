package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.validation.Validation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Common interface used by the fields to trigger their validations.
 * Implement this to customize how the validations should be executed and behave within a field.
 *
 */
interface FieldValidationBehavior {

    /**
     * Trigger the given validations firing updates to the mutable field status.
     * How they are triggered and behave depends on the implementation.
     *
     * Must return a boolean indicating if all the validations were successful or not.
     *
     * @param fieldId The ID of the field being validated.
     * @param mutableFieldStatus a mutable stateFlow to notify the status updates of the field during the validations.
     * @param validations The validations to trigger.
     * @param asyncCoroutineDispatcher The coroutine dispatcher to use for asynchronous validations.
     */
    suspend fun triggerValidations(
        fieldId : String,
        mutableFieldStatus: MutableSharedFlow<FieldStatus>,
        validations: List<Validation>,
        asyncCoroutineDispatcher: CoroutineDispatcher? = null
    ) : Boolean

    /**
     * Trigger the given validations firing updates to the mutable field status.
     * How they are triggered and behave depends on the implementation.
     *
     * Must return a boolean indicating if all the validations were successful or not.
     * @param mutableFieldStatus a mutable stateFlow to notify the status updates of the field during the validations.
     * @param validations The validations to trigger.
     * @param asyncCoroutineDispatcher The coroutine dispatcher to use for asynchronous validations.
     */
    @Deprecated(
        message = "Deprecated since it does not use the field ID. Will be removed on future versions",
        replaceWith = ReplaceWith("triggerValidations(fieldId, mutableFieldStatus, validations, asyncCoroutineDispatcher)")
    )
    suspend fun triggerValidations(
        mutableFieldStatus: MutableStateFlow<FieldStatus>,
        validations: List<Validation>,
        asyncCoroutineDispatcher: CoroutineDispatcher? = null
    ) : Boolean { return true }

}
