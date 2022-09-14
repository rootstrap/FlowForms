package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.validation.ValidationsCancelledException
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Represent the available field actions and their definitions
 */
interface FieldActions {

    /**
     * Triggers the onValueChange validations associated on a [Field][com.rootstrap.flowforms.core.field.FlowField]
     * based on the field's [FieldValidationBehavior].
     *
     * When triggered again while there were previous validations in progress, those validations
     * will be cancelled along with the coroutine that triggered this method via a [ValidationsCancelledException],
     * and then the OnValueChange validations will be triggered again from scratch as expected.
     */
    suspend fun triggerOnValueChangeValidations(asyncCoroutineDispatcher: CoroutineDispatcher? = null) : Boolean

    /**
     * Triggers the onBlur validations associated on a [Field][com.rootstrap.flowforms.core.field.FlowField]
     * based on the field's [FieldValidationBehavior].
     *
     * When triggered again while there were previous validations in progress, those validations
     * will be cancelled along with the coroutine that triggered this method via a [ValidationsCancelledException],
     * and then the OnBlur validations will be triggered again from scratch as expected.
     */
    suspend fun triggerOnBlurValidations(asyncCoroutineDispatcher: CoroutineDispatcher? = null) : Boolean

    /**
     * Triggers the onFocus validations associated on a [Field][com.rootstrap.flowforms.core.field.FlowField]
     * based on the field's [FieldValidationBehavior].
     *
     * When triggered again while there were previous validations in progress, those validations
     * will be cancelled along with the coroutine that triggered this method via a [ValidationsCancelledException],
     * and then the OnFocus validations will be triggered again from scratch as expected.
     */
    suspend fun triggerOnFocusValidations(asyncCoroutineDispatcher: CoroutineDispatcher? = null) : Boolean
}
