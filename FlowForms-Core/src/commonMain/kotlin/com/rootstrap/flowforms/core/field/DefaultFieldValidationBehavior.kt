package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.common.StatusCodes
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationResult
import com.rootstrap.flowforms.core.validation.ValidationShortCircuitException
import com.rootstrap.flowforms.util.whenNotEmptyAnd
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
     * Asynchronous validations are triggered using the given asyncCoroutineDispatcher and only
     * after the regular validations. Considering that if there is a failing failFast regular
     * validation then the async validations will not be triggered at all for optimum performance.
     */
    override suspend fun triggerValidations(
        mutableFieldStatus: MutableStateFlow<FieldStatus>,
        validations: List<Validation>,
        asyncCoroutineDispatcher: CoroutineDispatcher?
    ) : Boolean {
        val (asyncValidations, syncValidations) = validations.partition { it.async }
        if (asyncValidations.isNotEmpty() && asyncCoroutineDispatcher == null) {
            throw IllegalStateException("Async coroutine dispatcher could not be null in order to use async validations")
        }

        val validationProcessData = ValidationProcessData(
            asyncValidations = asyncValidations,
            syncValidations = syncValidations,
            asyncCoroutineDispatcher = asyncCoroutineDispatcher,
            validationResults = mutableListOf(),
            failedValResults = mutableListOf(),
            validationsShortCircuited = false
        )

        runSyncValidations(validationProcessData)

        asyncValidations.whenNotEmptyAnd({ !validationProcessData.validationsShortCircuited }) {
            mutableFieldStatus.emit(FieldStatus(StatusCodes.IN_PROGRESS))
            runAsyncValidations(validationProcessData)
        }

        val fieldStatus = when {
            validationProcessData.failedValResults.isEmpty() -> FieldStatus(StatusCodes.CORRECT, validationProcessData.validationResults)
            validationProcessData.failedValResults.size == 1 -> FieldStatus(validationProcessData.failedValResults.first().resultId, validationProcessData.validationResults)
            else -> FieldStatus(StatusCodes.INCORRECT, validationProcessData.validationResults)
        }

        mutableFieldStatus.value = fieldStatus
        return fieldStatus.code == StatusCodes.CORRECT
    }

    private suspend fun runSyncValidations(validationProcessData: ValidationProcessData) {
        validationProcessData.run {
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
        }
    }

    private suspend fun runAsyncValidations(
        validationProcessData: ValidationProcessData
    ) {
        val deferredCalls = mutableListOf<Deferred<ValidationResult>>()
        validationProcessData.run {
            try {
                val res = coroutineScope {
                    asyncValidations.forEach {
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
                validationsShortCircuited = true
            }
        }
    }

    private class ValidationProcessData(
        val syncValidations : List<Validation>,
        val asyncValidations : List<Validation>,
        val asyncCoroutineDispatcher: CoroutineDispatcher?,
        val validationResults: MutableList<ValidationResult>,
        val failedValResults: MutableList<ValidationResult>,
        var validationsShortCircuited : Boolean
    )

}
