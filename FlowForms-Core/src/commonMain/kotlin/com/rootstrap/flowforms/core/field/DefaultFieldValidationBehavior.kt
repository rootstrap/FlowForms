package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.common.StatusCodes
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationResult
import com.rootstrap.flowforms.core.validation.ValidationShortCircuitException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.yield

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
     * validation then the async validations will not be triggered at all for optimum performance,
     * additionally, if there are various async validations in progress and one fails, then all the
     * other validations will be cancelled.
     *
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
            mutableFieldStatus = mutableFieldStatus,
            asyncValidations = asyncValidations,
            syncValidations = syncValidations,
            asyncCoroutineDispatcher = asyncCoroutineDispatcher,
            validationResults = mutableListOf(),
            failedValResults = mutableListOf(),
            validationsShortCircuited = false
        )

        runSyncValidations(validationProcessData)
        return if (asyncValidations.isNotEmpty() && !validationProcessData.validationsShortCircuited) {
            startAsyncValidationProcessWithResult(validationProcessData)
        } else {
            updateFieldStatusWithFinalResult(validationProcessData)
        }
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

    private suspend fun startAsyncValidationProcessWithResult(data: ValidationProcessData): Boolean {
        data.mutableFieldStatus.emit(FieldStatus(StatusCodes.IN_PROGRESS))
        runAsyncValidations(data)
        yield()
        return updateFieldStatusWithFinalResult(data)
    }

    private suspend fun runAsyncValidations(
        data: ValidationProcessData
    ) {
        data.run {
            try {
                executeValidationsAsDeferred(this)
            } catch (ex : ValidationShortCircuitException) {
                val completedResults = deferredAsyncValidations.filter {
                    it.isCompleted && !it.isCancelled
                }.map { it.getCompleted() }
                validationResults.addAll(completedResults)
                failedValResults.add(ex.validationResult) // fail-fast failed result
                validationResults.add(ex.validationResult)
                validationsShortCircuited = true
            }
        }
    }

    private suspend fun executeValidationsAsDeferred(data: ValidationProcessData) {
        val res = coroutineScope {
            data.asyncValidations.forEach {
                data.deferredAsyncValidations.add(async(data.asyncCoroutineDispatcher!!) {
                    val res = it.validate()
                    if (res.resultId != StatusCodes.CORRECT && it.failFast ) {
                        throw ValidationShortCircuitException(res)
                    }
                    res
                })
            }
            data.deferredAsyncValidations.awaitAll()
        }
        data.validationResults.addAll(res)
        data.failedValResults.addAll(res.filter { it.resultId != StatusCodes.CORRECT })
    }

    private fun updateFieldStatusWithFinalResult(data: ValidationProcessData) : Boolean {
        val fieldStatus = when {
            data.failedValResults.isEmpty() -> FieldStatus(StatusCodes.CORRECT, data.validationResults)
            data.failedValResults.size == 1 -> FieldStatus(data.failedValResults.first().resultId, data.validationResults)
            else -> FieldStatus(StatusCodes.INCORRECT, data.validationResults)
        }

        data.mutableFieldStatus.value = fieldStatus
        return fieldStatus.code == StatusCodes.CORRECT
    }

    private class ValidationProcessData(
        val mutableFieldStatus: MutableStateFlow<FieldStatus>,
        val syncValidations : List<Validation>,
        val asyncValidations : List<Validation>,
        val asyncCoroutineDispatcher: CoroutineDispatcher?,
        val validationResults: MutableList<ValidationResult>,
        val failedValResults: MutableList<ValidationResult>,
        var validationsShortCircuited : Boolean,
        var deferredAsyncValidations: MutableList<Deferred<ValidationResult>> = mutableListOf()
    )

}
