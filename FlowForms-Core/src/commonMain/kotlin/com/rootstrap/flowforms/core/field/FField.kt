package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.IN_PROGRESS
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationResult
import com.rootstrap.flowforms.util.whenNotEmptyAnd
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * FlowField : a reactive field of a form, identified by it's ID.
 *
 * @property id field's ID.
 * @property validations list of validations to trigger when needed, like when the field's value
 * changes in the form, or the user takes off the focus of the field.
 */
@OptIn(ExperimentalCoroutinesApi::class)
open class FField(
    val id : String,
    val validations : List<Validation> = mutableListOf()
) {

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

    /**
     * Trigger the validations associated on this Field in the order they were added.
     *
     * Asynchronous validations are triggered after the regular validations in order to optimize resources.
     *
     * If there is a failing failFast regular validation then the async validations will not be triggered.
     */
    suspend fun triggerValidations(asyncCoroutineDispatcher: CoroutineDispatcher ? = null) : Boolean {
        return triggerValidationsInternal(validations, asyncCoroutineDispatcher)
    }

    private suspend fun triggerValidationsInternal(
        validations: List<Validation>,
        asyncCoroutineDispatcher: CoroutineDispatcher ? = null
    ) : Boolean {
        val validationResults = mutableListOf<ValidationResult>()
        val failedValResults = mutableListOf<ValidationResult>()

        val (asyncValidations, syncValidations) = validations.partition { it.async }
        var validationsShortCircuited = false

        if (asyncValidations.isNotEmpty() && asyncCoroutineDispatcher == null) {
            throw IllegalStateException("Async coroutine dispatcher could not be null in order to use async validations")
        }

        try {
            syncValidations.forEach { validation ->
                val res = validation.validate()
                validationResults.add(res)
                if (res.resultId != CORRECT) {
                    failedValResults.add(res)
                    if (validation.failFast) {
                        throw ValidationShortCircuitException(res)
                    }
                }
            }
        } catch (ex : ValidationShortCircuitException) {
            validationsShortCircuited = true
        }

        asyncValidations.whenNotEmptyAnd({ !validationsShortCircuited }) {
            _status.emit(FieldStatus(IN_PROGRESS))
            val deferredCalls = mutableListOf<Deferred<ValidationResult>>()
            try {
                val res = coroutineScope {
                    forEach {
                        deferredCalls.add(async(asyncCoroutineDispatcher!!) {
                            val res = it.validate()
                            if (res.resultId != CORRECT && it.failFast ) {
                                throw ValidationShortCircuitException(res)
                            }
                            res
                        })
                    }
                    deferredCalls.awaitAll()
                }
                validationResults.addAll(res)
                failedValResults.addAll(res.filter { it.resultId != CORRECT })
            } catch (ex : ValidationShortCircuitException) {
                val completedResults = deferredCalls.filter {
                    it.isCompleted && !it.isCancelled
                }.map { it.getCompleted() }

                validationResults.addAll(completedResults)
                failedValResults.add(ex.validationResult) // fail-fast failed result
                validationResults.add(ex.validationResult)
            }
        }

        val fieldStatus = when {
            failedValResults.isEmpty() -> FieldStatus(CORRECT, validationResults)
            failedValResults.size == 1 -> FieldStatus(failedValResults.first().resultId, validationResults)
            else -> FieldStatus(INCORRECT, validationResults)
        }

        _status.value = fieldStatus
        return fieldStatus.code == CORRECT
    }

    class ValidationShortCircuitException(val validationResult : ValidationResult) : Exception()

}
