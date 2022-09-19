package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCOMPLETE
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.IN_PROGRESS
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
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
    val id : String,
    private val onValueChangeValidations : List<Validation> = mutableListOf(),
    private val onBlurValidations : List<Validation> = mutableListOf(),
    private val onFocusValidations : List<Validation> = mutableListOf(),
    private val validationBehavior: FieldValidationBehavior = DefaultFieldValidationBehavior()
) : FieldActions {

    private val _onValueChangeStatus = MutableStateFlow(FieldStatus(
        if (onValueChangeValidations.isEmpty()) UNSET else UNMODIFIED
    ))
    private val _onBlurStatus = MutableStateFlow(FieldStatus(
        if (onBlurValidations.isEmpty()) UNSET else UNMODIFIED
    ))
    private val _onFocusStatus = MutableStateFlow(FieldStatus(
        if (onFocusValidations.isEmpty()) UNSET else UNMODIFIED
    ))

    private var onValueChangeCoroutinesJob : Job? = null
    private var onFocusCoroutinesJob : Job? = null
    private var onBlurCoroutinesJob : Job? = null

    override val status : Flow<FieldStatus> = combine(_onValueChangeStatus, _onBlurStatus, _onFocusStatus) {
            onValueChangeStatus, onBlurStatus, onFocusStatus ->
        when {
            thereAreFailedValidations(onValueChangeStatus, onBlurStatus, onFocusStatus) ->
                getIncorrectFieldStatus(onValueChangeStatus, onBlurStatus, onFocusStatus)
            thereAreValidationsInProgress(onValueChangeStatus, onBlurStatus, onFocusStatus) ->
                FieldStatus(IN_PROGRESS)
            noValidationsWereExecutedAtAll(onValueChangeStatus, onBlurStatus, onFocusStatus) ->
                FieldStatus(UNMODIFIED)
            thereAreSomeValidationsNotExecutedYet(onValueChangeStatus, onBlurStatus, onFocusStatus) ->
                FieldStatus(INCOMPLETE)
            else ->
                FieldStatus(CORRECT)
        }
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
            FieldStatus(INCORRECT, failingFields.map { it.validationResults }.flatten())
        }
    }

    override suspend fun triggerOnValueChangeValidations(asyncCoroutineDispatcher: CoroutineDispatcher?) : Boolean {
        cancelValidationsInProgress(ON_VALUE_CHANGE)
        onValueChangeCoroutinesJob = Job()
        return coroutineScope {
            withContext(onValueChangeCoroutinesJob!!) {
                validationBehavior.triggerValidations(_onValueChangeStatus, onValueChangeValidations, asyncCoroutineDispatcher)
            }
        }
    }

    override suspend fun triggerOnBlurValidations(asyncCoroutineDispatcher: CoroutineDispatcher?) : Boolean {
        cancelValidationsInProgress(ON_BLUR)
        onBlurCoroutinesJob = Job()
        return coroutineScope {
            withContext(onBlurCoroutinesJob!!) {
                validationBehavior.triggerValidations(_onBlurStatus, onBlurValidations, asyncCoroutineDispatcher)
            }
        }
    }

    override suspend fun triggerOnFocusValidations(asyncCoroutineDispatcher: CoroutineDispatcher?) : Boolean {
        cancelValidationsInProgress(ON_FOCUS)
        onFocusCoroutinesJob = Job()
        return coroutineScope {
            withContext(onFocusCoroutinesJob!!) {
                validationBehavior.triggerValidations(_onFocusStatus, onFocusValidations, asyncCoroutineDispatcher)
            }
        }
    }

    private suspend fun cancelValidationsInProgress(validationType : String) {
        val jobToCancel = when (validationType) {
            ON_VALUE_CHANGE -> onValueChangeCoroutinesJob
            ON_FOCUS -> onFocusCoroutinesJob
            else -> onBlurCoroutinesJob
        }
        jobToCancel?.let {
            it.cancel(ValidationsCancelledException(
                "Validations cancelled because they are being triggered again"
            ))
            it.join()
        }
    }

    companion object {
        /**
         * Internal status for the empty [Validation]s' states.
         */
        private const val UNSET = "unset"

        /**
         * Internal key for on value change validations
         */
        private const val ON_VALUE_CHANGE = "on-value-change"

        /**
         * Internal key for on focus validations
         */
        private const val ON_FOCUS = "on-focus"

        /**
         * Internal key for on blur validations
         */
        private const val ON_BLUR = "on-blur"
    }
}
