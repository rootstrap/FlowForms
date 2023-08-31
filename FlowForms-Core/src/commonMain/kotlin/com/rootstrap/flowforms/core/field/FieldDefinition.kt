package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationsCancelledException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

/**
 * Represent the necessary field actions, variables, and their definitions
 */
abstract class FieldDefinition {

    /**
     * field unique identifier
     */
    abstract val id : String

    /**
     * field validations that defines this field's behavior when its value changes.
     * Usually used on the [triggerOnValueChangeValidations] method when called by a FlowForm
     */
    abstract val onValueChangeValidations : List<Validation>

    /**
     * field validations that defines this field's behavior when it loses the focus.
     * Usually used on the [triggerOnBlurValidations] method when called by a FlowForm
     */
    abstract val onBlurValidations : List<Validation>

    /**
     * field validations that defines this field's behavior when it gets focus.
     * Usually used on the [triggerOnFocusValidations] method when called by a FlowForm
     */
    abstract val onFocusValidations : List<Validation>

    /**
     * Flow with the field's status. Initially it will be in an [UNMODIFIED] state.
     * As long as the [Validation]s are triggered, this flow will be updated based on the [Validation]s
     * results and the available validations.
     *
     * For more information about the possible statuses check [FieldStatus]
     */
    abstract val status : Flow<FieldStatus>

    /**
     * Triggers the onValueChange validations associated on a [Field][com.rootstrap.flowforms.core.field.FlowField]
     * based on the field's [FieldValidationBehavior]. Unless a different list of validations is
     * specified.
     *
     * When triggered again while there were previous validations in progress, those validations
     * will be cancelled along with the coroutine that triggered this method via a [ValidationsCancelledException],
     * and then the OnValueChange validations will be triggered again from scratch as expected.
     *
     * @param asyncCoroutineDispatcher Optional coroutines dispatcher to use when running
     * asynchronous validations (required for such case). Defaults to null.
     * @param validations list of validations to trigger on this field. The field's validations
     * are used if it is empty. Defaults to empty.
     */
    abstract suspend fun triggerOnValueChangeValidations(
        asyncCoroutineDispatcher: CoroutineDispatcher? = null,
        validations: List<Validation> = emptyList()
    ) : Boolean

    /**
     * Triggers the onBlur validations associated on a [Field][com.rootstrap.flowforms.core.field.FlowField]
     * based on the field's [FieldValidationBehavior]. Unless a different list of validations is
     * specified.
     *
     * When triggered again while there were previous validations in progress, those validations
     * will be cancelled along with the coroutine that triggered this method via a [ValidationsCancelledException],
     * and then the OnBlur validations will be triggered again from scratch as expected.
     *
     * @param asyncCoroutineDispatcher Optional coroutines dispatcher to use when running
     * asynchronous validations (required for such case). Defaults to null.
     * @param validations list of validations to trigger on this field. The field's validations
     * are used if it is empty. Defaults to empty.
     */
    abstract suspend fun triggerOnBlurValidations(
        asyncCoroutineDispatcher: CoroutineDispatcher? = null,
        validations: List<Validation> = emptyList()
    ) : Boolean

    /**
     * Triggers the onFocus validations associated on a [Field][com.rootstrap.flowforms.core.field.FlowField]
     * based on the field's [FieldValidationBehavior].
     *
     * When triggered again while there were previous validations in progress, those validations
     * will be cancelled along with the coroutine that triggered this method via a [ValidationsCancelledException],
     * and then the OnFocus validations will be triggered again from scratch as expected.
     *
     * @param asyncCoroutineDispatcher Optional coroutines dispatcher to use when running
     * asynchronous validations (required for such case). Defaults to null.
     * @param validations list of validations to trigger on this field. The field's validations
     * are used if it is empty. Defaults to empty.
     */
    abstract suspend fun triggerOnFocusValidations(
        asyncCoroutineDispatcher: CoroutineDispatcher? = null,
        validations: List<Validation> = emptyList()
    ) : Boolean

    /**
     * getter method to get the current status of this field without using flows.
     *
     * @return the current status of this field in its raw format
     */
    abstract fun getCurrentStatus() : FieldStatus

    /**
     * Describe the types of validations actually supported.
     */
    enum class ValidationType {
        ON_VALUE_CHANGE, ON_FOCUS, ON_BLUR
    }

}
