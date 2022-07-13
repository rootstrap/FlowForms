package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.common.StatusCodes
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationResult
import com.rootstrap.flowforms.core.validation.ValidationShortCircuitException
import com.rootstrap.flowforms.util.whenNotEmptyAnd
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Default field validation behavior used in fields.
 * To know how the validations behave by default see [triggerValidations]
 */
@ExperimentalCoroutinesApi
class DefaultFieldValidationBehavior : FieldValidationBehavior {

    /**
     * Trigger the given validations in the order they were added to the list,
     * firing different statuses to the mutableFieldStatus flow given as 1st parameter during execution.
     *
     * Asynchronous validations are triggered after the regular validations and if there is a
     * failing failFast regular validation then the async validations will not be triggered at all
     * to optimize resources.
     */
    override suspend fun triggerValidations(
        mutableFieldStatus: MutableStateFlow<FieldStatus>,
        validations: List<Validation>,
        asyncCoroutineDispatcher: CoroutineDispatcher?
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
                if (res.resultId != StatusCodes.CORRECT) {
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
            mutableFieldStatus.emit(FieldStatus(StatusCodes.IN_PROGRESS))
            val deferredCalls = mutableListOf<Deferred<ValidationResult>>()
            try {
                val res = coroutineScope {
                    forEach {
                        deferredCalls.add(async(asyncCoroutineDispatcher!!) {
                            val res = it.validate()
                            if (res.resultId != StatusCodes.CORRECT && it.failFast ) {
                                throw ValidationShortCircuitException(res)
                            }
                            res
                        })
                    }
                    deferredCalls.awaitAll()
                }
                validationResults.addAll(res)
                failedValResults.addAll(res.filter { it.resultId != StatusCodes.CORRECT })
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
            failedValResults.isEmpty() -> FieldStatus(StatusCodes.CORRECT, validationResults)
            failedValResults.size == 1 -> FieldStatus(failedValResults.first().resultId, validationResults)
            else -> FieldStatus(StatusCodes.INCORRECT, validationResults)
        }

        mutableFieldStatus.value = fieldStatus
        return fieldStatus.code == StatusCodes.CORRECT
    }

}