package com.rootstrap.flowforms.core.field

import app.cash.turbine.FlowTurbine
import app.cash.turbine.test
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.IN_PROGRESS
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class TriggerValidationsTest {

    @Test
    fun `GIVEN a validation WHEN it is correct THEN assert the field status is CORRECT`()
    = runTest {
        val correctValidation = validation(ValidationResult.Correct)
        val field = FField("email", listOf(correctValidation))

        field.status.test {
            awaitItem() // UNMODIFIED status
            field.triggerValidations()
            assertEquals(CORRECT, awaitItem().code)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a validation with a custom error code WHEN it fails THEN assert the field status is the custom error code`()
    = runTest {
        val customErrorCode = "custom-code"
        val failingValidation = validation(ValidationResult(customErrorCode))
        val field = FField("email", listOf( failingValidation ) )

        field.status.test {
            awaitItem() // UNMODIFIED status
            field.triggerValidations()
            assertEquals(customErrorCode, awaitItem().code)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN two validations WHEN one is correct and the other is incorrect but with a custom error code THEN assert the field status is that errorCode`()
    = runTest {
        val customErrorCode = "Custom-code"
        val correctValidation = validation(ValidationResult.Correct)
        val failingValidation = validation(ValidationResult(customErrorCode))
        val field = FField("email", listOf(correctValidation, failingValidation))

        field.status.test {
            awaitItem() // UNMODIFIED status
            field.triggerValidations()
            assertEquals(customErrorCode, awaitItem().code)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a failing and a correct validations THEN assert the field status is INCORRECT`()
    = runTest {
        val correctValidation = validation(ValidationResult.Correct)
        val failingValidation = validation(ValidationResult.Incorrect)
        val field = FField("email", listOf(failingValidation, correctValidation))

        field.status.test {
            awaitItem() // UNMODIFIED status
            field.triggerValidations()
            assertEquals(INCORRECT, awaitItem().code)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a failing failFast and a correct validations THEN assert the correct validation is not executed`()
    = runTest {
        val failingValidation = validation(ValidationResult.Incorrect, true)
        val correctValidation = validation(ValidationResult.Correct)
        val field = FField("email", listOf(failingValidation, correctValidation))

        field.status.test {
            field.triggerValidations()
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { failingValidation.validate() }
        coVerify(exactly = 0) { correctValidation.validate() }
    }

    // Async validations test

    @Test
    fun `GIVEN a field with an async validation but without a coroutine dispatcher THEN assert it throws IllegalStateException`()
    = runTest {
        val asyncValidation = asyncValidation(0, ValidationResult.Correct)
        val field = FField("email", listOf(asyncValidation))

        val exception = assertFails { field.triggerValidations() }
        assertIs<IllegalStateException>(exception)
    }

    @Test
    fun `GIVEN a correct async validation THEN assert the field status CHANGES from unmodified to in progress to correct`()
    = runTest {
        val testAsyncCoroutineDispatcher = StandardTestDispatcher(testScheduler, name = "IO dispatcher")
        val correctAsyncValidation = asyncValidation(0, ValidationResult.Correct)
        val field = FField("email", listOf(correctAsyncValidation))

        field.status.test {
            field.triggerValidations(testAsyncCoroutineDispatcher)
            assertFieldStatusSequence(this, UNMODIFIED, IN_PROGRESS, CORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a incorrect async validation THEN assert the field status CHANGES from unmodified to in progress to Incorrect`()
    = runTest {
        val testAsyncCoroutineDispatcher = StandardTestDispatcher(testScheduler, name = "IO dispatcher")
        val incorrectAsyncValidation = asyncValidation(0, ValidationResult.Incorrect)
        val field = FField("email", listOf(incorrectAsyncValidation))

        field.status.test {
            field.triggerValidations(testAsyncCoroutineDispatcher)
            assertFieldStatusSequence(this, UNMODIFIED, IN_PROGRESS, INCORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN two async validations WHEN the first is correct but the second is incorrect with a custom error code THEN assert the field status CHANGES from unmodified to in progress to that custom error code`()
    = runTest {
        val customErrorCode = "custom-code"
        val testAsyncCoroutineDispatcher = StandardTestDispatcher(testScheduler, name = "IO dispatcher")
        val correctAsyncValidation = asyncValidation(0, ValidationResult.Correct)
        val incorrectAsyncValidation = asyncValidation(0, ValidationResult(customErrorCode))
        val field = FField("email", listOf(correctAsyncValidation, incorrectAsyncValidation))

        field.status.test {
            field.triggerValidations(testAsyncCoroutineDispatcher)
            assertFieldStatusSequence(this, UNMODIFIED, IN_PROGRESS, customErrorCode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN 3 async validations with different duration WHEN the middle one is failFast and fails THEN assert the last one was cancelled`()
    = runTest {
        val testAsyncCoroutineDispatcher = StandardTestDispatcher(testScheduler, name = "IO dispatcher")
        val fastestVal = asyncValidation(10, ValidationResult.Correct)
        val middleIncorrectVal = asyncValidation(20, ValidationResult.Incorrect, true)
        val slowerVal = asyncValidation(30, ValidationResult.Correct)
        val field = FField("email", listOf(slowerVal, fastestVal, middleIncorrectVal))

        field.status.test {
            field.triggerValidations(testAsyncCoroutineDispatcher)

            val lastStatus = assertFieldStatusSequence(this, UNMODIFIED, IN_PROGRESS, INCORRECT)
            // checks that the slower validation was not added to the results (because it was cancelled before)
            assertEquals(2, lastStatus.validationResults.size)
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `GIVEN 1 sync and 1 async validation WHEN both are correct THEN assert both was executed and the field status changes from UNMODIFIED to INPROGRESS to CORRECT`()
    = runTest {
        val testAsyncCoroutineDispatcher = StandardTestDispatcher(testScheduler, name = "IO dispatcher")
        val regularValidation = validation(ValidationResult.Correct)
        val asyncValidation = asyncValidation(10, ValidationResult.Correct)
        val field = FField("email", listOf(regularValidation, asyncValidation))

        field.status.test {
            field.triggerValidations(testAsyncCoroutineDispatcher)
            assertFieldStatusSequence(this, UNMODIFIED, IN_PROGRESS, CORRECT)

            coVerify(exactly = 1) { regularValidation.validate() }
            coVerify(exactly = 1) { asyncValidation.validate() }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN 1 failFast sync and 1 async validation WHEN the sync validation fails THEN assert the async validation was not executed and the field status changes from UNMODIFIED to INCORRECT`()
    = runTest {
        val testAsyncCoroutineDispatcher = StandardTestDispatcher(testScheduler, name = "IO dispatcher")
        val regularValidation = validation(ValidationResult.Incorrect, true)
        val asyncValidation = asyncValidation(10, ValidationResult.Correct)
        val field = FField("email", listOf(regularValidation, asyncValidation))

        field.status.test {
            field.triggerValidations(testAsyncCoroutineDispatcher)
            assertFieldStatusSequence(this, UNMODIFIED, INCORRECT)

            coVerify(exactly = 1) { regularValidation.validate() }
            coVerify(exactly = 0) { asyncValidation.validate() }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN 2 async validations with custom error codes WHEN both fails THEN assert the field status changes from UNMODIFIED to IN_PROGRESS to INCORRECT and the validation results contains the custom error codes`()
    = runTest {
        val customErrorCode1 = "custom-error-code-1"
        val customErrorCode2 = "custom-error-code-2"
        val testAsyncCoroutineDispatcher = StandardTestDispatcher(testScheduler, name = "IO dispatcher")
        val asyncValidation = asyncValidation(5, ValidationResult(customErrorCode1))
        val asyncValidation2 = asyncValidation(5, ValidationResult(customErrorCode2))
        val field = FField("email", listOf(asyncValidation, asyncValidation2))

        field.status.test {
            field.triggerValidations(testAsyncCoroutineDispatcher)
            val lastStatusResults = assertFieldStatusSequence(this, UNMODIFIED, IN_PROGRESS, INCORRECT)
                .validationResults

            coVerify(exactly = 1) { asyncValidation.validate() }
            coVerify(exactly = 1) { asyncValidation2.validate() }

            assertEquals(2, lastStatusResults.filter {
                it.resultId == customErrorCode1 || it.resultId == customErrorCode2
            }.size)
            assertEquals(customErrorCode1, lastStatusResults.find { it.resultId == customErrorCode1 }?.resultId)
            assertEquals(customErrorCode2, lastStatusResults.find { it.resultId == customErrorCode2 }?.resultId)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // Helper functions

    private fun validation(result : ValidationResult, failFast : Boolean = false)
            = mockk<Validation> {
        every { async } returns false
        every { this@mockk.failFast } returns failFast
        coEvery { validate() } coAnswers { result }
    }

    private fun asyncValidation(delayInMillis : Long, result : ValidationResult, failFast : Boolean = false)
    = mockk<Validation> {
        every { async } returns true
        every { this@mockk.failFast } returns failFast
        coEvery { validate() } coAnswers {
            delay(delayInMillis)
            result
        }
    }

    private suspend fun assertFieldStatusSequence(flowTurbine: FlowTurbine<FieldStatus>, vararg statuses: String): FieldStatus {
        var lastValue :FieldStatus? = null
        statuses.forEach {
            lastValue = flowTurbine.awaitItem()
            assertEquals(it, lastValue?.code)
        }
        return lastValue!!
    }

}
