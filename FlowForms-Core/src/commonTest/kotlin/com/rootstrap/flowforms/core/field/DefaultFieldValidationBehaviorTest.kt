package com.rootstrap.flowforms.core.field

import app.cash.turbine.test
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCOMPLETE
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.IN_PROGRESS
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.util.assertFieldStatusSequence
import com.rootstrap.flowforms.core.util.asyncValidation
import com.rootstrap.flowforms.core.util.getTestDispatcher
import com.rootstrap.flowforms.core.util.validation
import com.rootstrap.flowforms.core.validation.ValidationResult
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs

/**
 * Test the DefaultFieldValidationBehavior using a FField.
 */
@ExperimentalCoroutinesApi
class DefaultFieldValidationBehaviorTest {

    @Test
    fun `GIVEN a validation WHEN it is correct THEN assert the field status is CORRECT`()
    = runTest {
        val correctValidation = validation(ValidationResult.Correct)
        val field = FlowField(id = FIELD_ID, listOf(correctValidation))

        field.status.test {
            awaitItem() // UNMODIFIED status
            field.triggerOnValueChangeValidations()
            awaitItem().run {
                assertEquals(FIELD_ID, fieldId)
                assertEquals(CORRECT, code)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a validation with a custom error code WHEN it fails THEN assert the field status is the custom error code`()
    = runTest {
        val customErrorCode = "custom-code"
        val failingValidation = validation(ValidationResult(customErrorCode))
        val field = FlowField(id = FIELD_ID, listOf( failingValidation ) )

        field.status.test {
            awaitItem() // UNMODIFIED status
            field.triggerOnValueChangeValidations()
            awaitItem().run {
                assertEquals(FIELD_ID, fieldId)
                assertEquals(customErrorCode, code)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN two validations WHEN one is correct and the other is incorrect but with a custom error code THEN assert the field status is that errorCode`()
    = runTest {
        val customErrorCode = "Custom-code"
        val correctValidation = validation(ValidationResult.Correct)
        val failingValidation = validation(ValidationResult(customErrorCode))
        val field = FlowField(id = FIELD_ID, listOf(correctValidation, failingValidation))

        field.status.test {
            awaitItem() // UNMODIFIED status
            field.triggerOnValueChangeValidations()
            awaitItem().run {
                assertEquals(FIELD_ID, fieldId)
                assertEquals(customErrorCode, code)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a failing and a correct validations THEN assert the field status is INCORRECT`()
    = runTest {
        val correctValidation = validation(ValidationResult.Correct)
        val failingValidation = validation(ValidationResult.Incorrect)
        val field = FlowField(id = FIELD_ID, listOf(failingValidation, correctValidation))

        field.status.test {
            awaitItem() // UNMODIFIED status
            field.triggerOnValueChangeValidations()
            awaitItem().run {
                assertEquals(FIELD_ID, fieldId)
                assertEquals(INCORRECT, code)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a failing failFast and a correct validations THEN assert the correct validation is not executed`()
    = runTest {
        val failingValidation = validation(ValidationResult.Incorrect, true)
        val correctValidation = validation(ValidationResult.Correct)
        val field = FlowField(id = FIELD_ID, listOf(failingValidation, correctValidation))

        field.status.test {
            field.triggerOnValueChangeValidations()
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
        val field = FlowField(id = FIELD_ID, listOf(asyncValidation))

        val exception = assertFails { field.triggerOnValueChangeValidations() }
        assertIs<IllegalStateException>(exception)
    }

    @Test
    fun `GIVEN a correct async validation THEN assert the field status CHANGES from unmodified to in progress to correct`()
    = runTest {
        val testAsyncCoroutineDispatcher = getTestDispatcher(testScheduler)
        val correctAsyncValidation = asyncValidation(0, ValidationResult.Correct)
        val field = FlowField(id = FIELD_ID, listOf(correctAsyncValidation))

        field.status.test {
            field.triggerOnValueChangeValidations(testAsyncCoroutineDispatcher)
            assertFieldStatusSequence(fieldId = FIELD_ID, this, UNMODIFIED, IN_PROGRESS, CORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a incorrect async validation THEN assert the field status CHANGES from unmodified to in progress to Incorrect`()
    = runTest {
        val testAsyncCoroutineDispatcher = getTestDispatcher(testScheduler)
        val incorrectAsyncValidation = asyncValidation(0, ValidationResult.Incorrect)
        val field = FlowField(id = FIELD_ID, listOf(incorrectAsyncValidation))

        field.status.test {
            field.triggerOnValueChangeValidations(testAsyncCoroutineDispatcher)
            assertFieldStatusSequence(fieldId = FIELD_ID, this, UNMODIFIED, IN_PROGRESS, INCORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN two async validations WHEN the first is correct but the second is incorrect with a custom error code THEN assert the field status CHANGES from unmodified to in progress to that custom error code`()
    = runTest {
        val customErrorCode = "custom-code"
        val testAsyncCoroutineDispatcher = getTestDispatcher(testScheduler)
        val correctAsyncValidation = asyncValidation(0, ValidationResult.Correct)
        val incorrectAsyncValidation = asyncValidation(0, ValidationResult(customErrorCode))
        val field = FlowField(id = FIELD_ID, listOf(correctAsyncValidation, incorrectAsyncValidation))

        field.status.test {
            field.triggerOnValueChangeValidations(testAsyncCoroutineDispatcher)
            assertFieldStatusSequence(fieldId = FIELD_ID, this, UNMODIFIED, IN_PROGRESS, customErrorCode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN 3 async validations with different duration WHEN the middle one is failFast and fails THEN assert the last one was cancelled`()
    = runTest {
        val testAsyncCoroutineDispatcher = getTestDispatcher(testScheduler)
        val fastestVal = asyncValidation(10, ValidationResult.Correct)
        val middleIncorrectVal = asyncValidation(20, ValidationResult.Incorrect, true)
        val slowerVal = asyncValidation(30, ValidationResult.Correct)
        val field = FlowField(id = FIELD_ID, listOf(slowerVal, fastestVal, middleIncorrectVal))

        field.status.test {
            field.triggerOnValueChangeValidations(testAsyncCoroutineDispatcher)

            val lastStatus = assertFieldStatusSequence(fieldId = FIELD_ID, this, UNMODIFIED, IN_PROGRESS, INCORRECT)
            // checks that the slower validation was not added to the results (because it was cancelled before)
            assertEquals(2, lastStatus.validationResults.size)
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `GIVEN 1 sync and 1 async validation WHEN both are correct THEN assert both was executed and the field status changes from UNMODIFIED to INPROGRESS to CORRECT`()
    = runTest {
        val testAsyncCoroutineDispatcher = getTestDispatcher(testScheduler)
        val regularValidation = validation(ValidationResult.Correct)
        val asyncValidation = asyncValidation(10, ValidationResult.Correct)
        val field = FlowField(id = FIELD_ID, listOf(regularValidation, asyncValidation))

        field.status.test {
            field.triggerOnValueChangeValidations(testAsyncCoroutineDispatcher)
            assertFieldStatusSequence(FIELD_ID, this, UNMODIFIED, IN_PROGRESS, CORRECT)

            coVerify(exactly = 1) { regularValidation.validate() }
            coVerify(exactly = 1) { asyncValidation.validate() }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN 1 failFast sync and 1 async validation WHEN the sync validation fails THEN assert the async validation was not executed and the field status changes from UNMODIFIED to INCORRECT`()
    = runTest {
        val testAsyncCoroutineDispatcher = getTestDispatcher(testScheduler)
        val regularValidation = validation(ValidationResult.Incorrect, true)
        val asyncValidation = asyncValidation(10, ValidationResult.Correct)
        val field = FlowField(FIELD_ID, listOf(regularValidation, asyncValidation))

        field.status.test {
            field.triggerOnValueChangeValidations(testAsyncCoroutineDispatcher)
            assertFieldStatusSequence(FIELD_ID, this, UNMODIFIED, INCORRECT)

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
        val testAsyncCoroutineDispatcher = getTestDispatcher(testScheduler)
        val asyncValidation = asyncValidation(5, ValidationResult(customErrorCode1))
        val asyncValidation2 = asyncValidation(5, ValidationResult(customErrorCode2))
        val field = FlowField(FIELD_ID, listOf(asyncValidation, asyncValidation2))

        field.status.test {
            field.triggerOnValueChangeValidations(testAsyncCoroutineDispatcher)
            val lastStatusResults = assertFieldStatusSequence(FIELD_ID, this, UNMODIFIED, IN_PROGRESS, INCORRECT)
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

    @Test
    fun `GIVEN the same validation in OnValueChange, OnBlur, and OnFocus Validations, THEN assert it was called 3 times`()
            = runTest {
        val validation = validation(ValidationResult.Correct)
        val field = FlowField(FIELD_ID,
            onValueChangeValidations = listOf(validation),
            onBlurValidations = listOf(validation),
            onFocusValidations = listOf(validation)
        )

        field.status.test {
            field.triggerOnFocusValidations()
            field.triggerOnValueChangeValidations()
            field.triggerOnBlurValidations()

            coVerify(exactly = 3) { validation.validate() }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN the same correct validation in OnValueChange, OnBlur, and OnFocus Validations, THEN assert the field status changes from UNMODIFIED to INCOMPLETE to CORRECT`()
            = runTest {
        val validation = validation(ValidationResult.Correct)
        val field = FlowField(FIELD_ID,
            onValueChangeValidations = listOf(validation),
            onBlurValidations = listOf(validation),
            onFocusValidations = listOf(validation)
        )

        field.status.test {
            // UNMODIFIED status
            awaitItem()

            field.triggerOnFocusValidations()
            // INCOMPLETE status, after OnFocus (field took focus)
            assertEquals(INCOMPLETE, awaitItem().code)

            field.triggerOnValueChangeValidations()
            // still INCOMPLETE status after OnValue changed (field value changed).
            assertEquals(INCOMPLETE, awaitItem().code)

            field.triggerOnBlurValidations()
            // All validation lists on the field were executed. So we expect a CORRECT status after OnBlur (aka field lost focus)
            assertEquals(CORRECT, awaitItem().code)

            cancelAndIgnoreRemainingEvents()
        }
    }

    companion object {
        private const val FIELD_ID = "fieldId"
    }

}
