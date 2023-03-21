package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCOMPLETE
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.IN_PROGRESS
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.field.FieldDefinition.ValidationType
import com.rootstrap.flowforms.core.field.FieldDefinition.ValidationType.ON_BLUR
import com.rootstrap.flowforms.core.field.FieldDefinition.ValidationType.ON_FOCUS
import com.rootstrap.flowforms.core.field.FieldDefinition.ValidationType.ON_VALUE_CHANGE
import com.rootstrap.flowforms.core.validation.CrossFieldValidation
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationsCancelledException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

/**
 * FlowField : a reactive field of a form, identified by it's ID.
 *
 * @property id field's ID.
 * @property onValueChangeValidations list of validations to trigger when the field's value changes.
 * @property onBlurValidations list of validations to trigger when the field loses the focus.
 * @property onFocusValidations list of validations to trigger when the field gains focus.
 */
class FlowField(
    override val id : String,
    override val onValueChangeValidations : List<Validation> = emptyList(),
    override val onBlurValidations : List<Validation> = emptyList(),
    override val onFocusValidations : List<Validation> = emptyList(),
    private val validationBehavior: FieldValidationBehavior = DefaultFieldValidationBehavior(),
) : FieldDefinition {

    private val filteredOnValueChangeValidations = onValueChangeValidations
        .filter { it !is CrossFieldValidation }
    private val filteredOnFocusValidations = onFocusValidations
        .filter { it !is CrossFieldValidation }
    private val filteredOnBlurValidations = onBlurValidations
        .filter { it !is CrossFieldValidation }

    private val _onValueChangeStatus = MutableStateFlow(FieldStatus(
        fieldId = id,
        code = if (onValueChangeValidations.isEmpty()) UNSET else UNMODIFIED
    ))
    private val _onBlurStatus = MutableStateFlow(FieldStatus(
        fieldId = id,
        code = if (onBlurValidations.isEmpty()) UNSET else UNMODIFIED
    ))
    private val _onFocusStatus = MutableStateFlow(FieldStatus(
        fieldId = id,
        code = if (onFocusValidations.isEmpty()) UNSET else UNMODIFIED
    ))

    private var onValueChangeCoroutinesJob : Job? = null
    private var onFocusCoroutinesJob : Job? = null
    private var onBlurCoroutinesJob : Job? = null

    private lateinit var currentStatus : FieldStatus

    override val status : Flow<FieldStatus> = combine(_onValueChangeStatus, _onBlurStatus, _onFocusStatus) {
            onValueChangeStatus, onBlurStatus, onFocusStatus ->
        currentStatus = when {
            thereAreFailedValidations(onValueChangeStatus, onBlurStatus, onFocusStatus) ->
                getIncorrectFieldStatus(onValueChangeStatus, onBlurStatus, onFocusStatus)
            thereAreValidationsInProgress(onValueChangeStatus, onBlurStatus, onFocusStatus) ->
                FieldStatus(fieldId = id, code = IN_PROGRESS)
            noValidationsWereExecutedAtAll(onValueChangeStatus, onBlurStatus, onFocusStatus) ->
                FieldStatus(fieldId = id, code = UNMODIFIED)
            thereAreSomeValidationsNotExecutedYet(onValueChangeStatus, onBlurStatus, onFocusStatus) ->
                FieldStatus(fieldId = id, code = INCOMPLETE)
            else ->
                FieldStatus(fieldId = id, code = CORRECT)
        }
        currentStatus
    }

    private fun thereAreFailedValidations(vararg fieldStatuses : FieldStatus) =
        fieldStatuses.any { it.code != IN_PROGRESS && it.code != UNMODIFIED && it.code != CORRECT && it.code != UNSET }

    private fun thereAreValidationsInProgress(vararg fieldStatuses : FieldStatus) =
        fieldStatuses.any { it.code == IN_PROGRESS }

    private fun thereAreSomeValidationsNotExecutedYet(vararg fieldStatuses : FieldStatus) =
        fieldStatuses.any { it.code == UNMODIFIED }

    private fun noValidationsWereExecutedAtAll(vararg fieldStatuses : FieldStatus) =
        fieldStatuses.all { it.code == UNMODIFIED || it.code == UNSET }

    private fun getIncorrectFieldStatus(vararg fieldStatuses : FieldStatus) : FieldStatus {
        val failingFields = fieldStatuses.filter { thereAreFailedValidations(it) }
        return if (failingFields.size == 1) {
            failingFields.first()
        } else {
            FieldStatus(
                fieldId = id,
                code = INCORRECT,
                validationResults = failingFields.map { it.validationResults }.flatten()
            )
        }
    }

    override suspend fun triggerOnValueChangeValidations(
        asyncCoroutineDispatcher: CoroutineDispatcher?,
        validations: List<Validation>
    ) = triggerValidations(ON_VALUE_CHANGE, validations, asyncCoroutineDispatcher)

    override suspend fun triggerOnBlurValidations(
        asyncCoroutineDispatcher: CoroutineDispatcher?,
        validations: List<Validation>
    ) = triggerValidations(ON_BLUR, validations, asyncCoroutineDispatcher)

    override suspend fun triggerOnFocusValidations(
        asyncCoroutineDispatcher: CoroutineDispatcher?,
        validations: List<Validation>
    ) = triggerValidations(ON_FOCUS, validations, asyncCoroutineDispatcher)

    private suspend fun triggerValidations(
        validationType: ValidationType,
        validations: List<Validation>,
        asyncCoroutineDispatcher: CoroutineDispatcher?,
    ) : Boolean {
        val coroutinesJob = restartJob(validationType)
        val (fieldValidations, statusFlow) = when(validationType) {
            ON_VALUE_CHANGE -> filteredOnValueChangeValidations to _onValueChangeStatus
            ON_FOCUS -> filteredOnFocusValidations to _onFocusStatus
            else -> filteredOnBlurValidations to _onBlurStatus
        }

        return coroutineScope {
            withContext(coroutinesJob) {
                val finalValidations = validations.ifEmpty { fieldValidations }
                validationBehavior.triggerValidations(
                    fieldId = id,
                    mutableFieldStatus = statusFlow,
                    validations = finalValidations,
                    asyncCoroutineDispatcher = asyncCoroutineDispatcher
                )
            }
        }
    }

    private suspend fun restartJob(validationType : ValidationType) : Job {
        return when(validationType) {
            ON_VALUE_CHANGE -> {
                cancelJob(onValueChangeCoroutinesJob)
                 Job().also { onValueChangeCoroutinesJob = it }
            }
            ON_FOCUS -> {
                cancelJob(onFocusCoroutinesJob)
                Job().also { onFocusCoroutinesJob = it }
            }
            else -> {
                cancelJob(onBlurCoroutinesJob)
                Job().also { onBlurCoroutinesJob = it }
            }
        }
    }

    private suspend fun cancelJob(jobToCancel: Job?) {
        jobToCancel?.let {
            it.cancel(ValidationsCancelledException(CANCEL_MESSAGE))
            it.join()
        }
    }

    override fun getCurrentStatus() = currentStatus

    companion object {
        /**
         * Internal status for the empty [Validation]s' states.
         */
        private const val UNSET = "unset"

        private const val CANCEL_MESSAGE =
            "Validations cancelled because they are being triggered again"
    }

}
