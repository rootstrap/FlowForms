package com.rootstrap.flowforms.core.field

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
    fun `GIVEN a correct validation THEN assert the field status is CORRECT`()
    = runTest {
        val correctValidation = validation(ValidationResult.Correct)
        val field = FField("email", listOf(correctValidation))

        field.status.test {
            awaitItem() // UNMODIFIED status
            field.triggerValidations()
            assertEquals(awaitItem().code, CORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a failing validation THEN assert the field status is INCORRECT`()
    = runTest {
        val failingValidation = validation(ValidationResult.Incorrect)
        val field = FField("email", listOf( failingValidation ) )

        field.status.test {
            awaitItem() // UNMODIFIED status
            field.triggerValidations()
            assertEquals(awaitItem().code, INCORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a correct and a failing validations THEN assert the field status is INCORRECT`()
    = runTest {
        val correctValidation = validation(ValidationResult.Correct)
        val failingValidation = validation(ValidationResult.Incorrect)
        val field = FField("email", listOf(correctValidation, failingValidation))

        field.status.test {
            awaitItem() // UNMODIFIED status
            field.triggerValidations()
            assertEquals(awaitItem().code, INCORRECT)
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
            assertEquals(awaitItem().code, INCORRECT)
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

            assertEquals(awaitItem().code, UNMODIFIED)
            assertEquals(awaitItem().code, IN_PROGRESS)
            assertEquals(awaitItem().code, CORRECT)
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

            assertEquals(awaitItem().code, UNMODIFIED)
            assertEquals(awaitItem().code, IN_PROGRESS)
            assertEquals(awaitItem().code, INCORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a correct and then an incorrect async validations THEN assert the field status CHANGES from unmodified to in progress to Incorrect`()
            = runTest {
        val testAsyncCoroutineDispatcher = StandardTestDispatcher(testScheduler, name = "IO dispatcher")
        val correctAsyncValidation = asyncValidation(0, ValidationResult.Correct)
        val incorrectAsyncValidation = asyncValidation(0, ValidationResult.Incorrect)
        val field = FField("email", listOf(correctAsyncValidation, incorrectAsyncValidation))

        field.status.test {
            field.triggerValidations(testAsyncCoroutineDispatcher)

            assertEquals(awaitItem().code, UNMODIFIED)
            assertEquals(awaitItem().code, IN_PROGRESS)
            assertEquals(awaitItem().code, INCORRECT)
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

            assertEquals(awaitItem().code, UNMODIFIED)
            assertEquals(awaitItem().code, IN_PROGRESS)
            val lastStatus = awaitItem()
            assertEquals(lastStatus.code, INCORRECT)
            // checks that the slower validation was not added to the results (because it was cancelled before)
            assertEquals(2, lastStatus.validationResults.size)
            cancelAndIgnoreRemainingEvents()
        }

    }

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

}
