package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationsCancelledException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

/**
 * Represent the necessary field actions, variables, and their definitions
 */
interface FieldDefinition {

    /**
     * field unique identifier
     */
    val id : String

    // TODO Kdoc
    val onValueChangeValidations : List<Validation>
    val onBlurValidations : List<Validation>
    val onFocusValidations : List<Validation>

    /**
     * Flow with the field's status. Initially it will be in an [UNMODIFIED] state.
     * As long as the [Validation]s are triggered, this flow will be updated based on the [Validation]s
     * results and the available validations.
     *
     * For more information about the possible statuses check [FieldStatus]
     */
    val status : Flow<FieldStatus>

    /**
     * Triggers the onValueChange validations associated on a [Field][com.rootstrap.flowforms.core.field.FlowField]
     * based on the field's [FieldValidationBehavior].
     *
     * When triggered again while there were previous validations in progress, those validations
     * will be cancelled along with the coroutine that triggered this method via a [ValidationsCancelledException],
     * and then the OnValueChange validations will be triggered again from scratch as expected.
     */ // TODO Kdoc additional param
    suspend fun triggerOnValueChangeValidations(
        asyncCoroutineDispatcher: CoroutineDispatcher? = null,
        validations: List<Validation> = emptyList()
    ) : Boolean

    /**
     * Triggers the onBlur validations associated on a [Field][com.rootstrap.flowforms.core.field.FlowField]
     * based on the field's [FieldValidationBehavior].
     *
     * When triggered again while there were previous validations in progress, those validations
     * will be cancelled along with the coroutine that triggered this method via a [ValidationsCancelledException],
     * and then the OnBlur validations will be triggered again from scratch as expected.
     */
    suspend fun triggerOnBlurValidations(
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
     */
    suspend fun triggerOnFocusValidations(
        asyncCoroutineDispatcher: CoroutineDispatcher? = null,
        validations: List<Validation> = emptyList()
    ) : Boolean

    fun getCurrentStatus() : FieldStatus

}
