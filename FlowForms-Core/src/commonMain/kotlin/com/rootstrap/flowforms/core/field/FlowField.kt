package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.validation.Validation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * FlowField : a reactive field of a form, identified by it's ID.
 *
 * @property id field's ID.
 * @property onValueChangeValidations list of validations to trigger when the field's value changes.
 * @property onBlurValidations list of validations to trigger when the field loses the focus.
 * @property onFocusValidations list of validations to trigger when the field gains focus.
 */
@OptIn(ExperimentalCoroutinesApi::class)
open class FlowField(
    val id : String,
    private val onValueChangeValidations : List<Validation> = mutableListOf(),
    private val onBlurValidations : List<Validation> = mutableListOf(),
    private val onFocusValidations : List<Validation> = mutableListOf(),
    private val validationBehavior: FieldValidationBehavior = DefaultFieldValidationBehavior()
) : FieldActions {

    private val _status = MutableStateFlow(FieldStatus())

    /**
     * Flow with the field status. Initially it will be in an [UNMODIFIED] state.
     * As long as the validations are triggered, this flow will be updated based on the validations
     * results and parameters.
     *
     * For more information about the possible statuses check [FieldStatus]
     *
     */
    val status : Flow<FieldStatus> = _status.asStateFlow()

    override suspend fun triggerOnValueChangeValidations(asyncCoroutineDispatcher: CoroutineDispatcher?) : Boolean {
        return validationBehavior.triggerValidations(_status, onValueChangeValidations, asyncCoroutineDispatcher)
    }

    override suspend fun triggerOnBlurValidations(asyncCoroutineDispatcher: CoroutineDispatcher?) : Boolean {
        return validationBehavior.triggerValidations(_status, onBlurValidations, asyncCoroutineDispatcher)
    }

    override suspend fun triggerOnFocusValidations(asyncCoroutineDispatcher: CoroutineDispatcher?) : Boolean {
        return validationBehavior.triggerValidations(_status, onFocusValidations, asyncCoroutineDispatcher)
    }

}
