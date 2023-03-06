@file:OptIn(ExperimentalCoroutinesApi::class)

package com.rootstrap.flowforms.core.field

import app.cash.turbine.test
import com.rootstrap.flowforms.core.common.StatusCodes
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.IN_PROGRESS
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.util.assertFieldStatusSequence
import com.rootstrap.flowforms.core.util.asyncValidation
import com.rootstrap.flowforms.core.util.getTestDispatcher
import com.rootstrap.flowforms.core.util.validation
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationResult
import com.rootstrap.flowforms.core.validation.ValidationsCancelledException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FlowFieldTest {

    @Test
    fun `GIVEN a new required field THEN assert its status is UNMODIFIED`() = runTest {
        val field = FlowField("email")
        field.status.test {
            assertEquals(awaitItem().code, UNMODIFIED)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN 1 sync and 1 async validation WHEN the sync val is correct and then incorrect while the async val was in progress THEN assert the field last status is INCORRECT`()
            = runTest {
        val testAsyncCoroutineDispatcher = getTestDispatcher(testScheduler)

        val asyncValidation = asyncValidation(30, ValidationResult.Correct)
        val regularValidation = mockk<Validation> {
            every { async } returns false
            every { this@mockk.failFast } returns true

            coEvery { validate() } coAnswers { ValidationResult.Correct } andThen ValidationResult.Incorrect
        }

        val field = FlowField(
            "email",
            listOf(regularValidation, asyncValidation)
        )

        field.status.test {
            launch {
                try {
                    field.triggerOnValueChangeValidations(testAsyncCoroutineDispatcher)
                } catch (ex : ValidationsCancelledException) {
                    println("First async validation was cancelled : ${ex.message}")
                }
            }
            assertFieldStatusSequence(this, UNMODIFIED, IN_PROGRESS)
            launch {
                field.triggerOnValueChangeValidations(testAsyncCoroutineDispatcher)
            }
            delay(50)

            assertFieldStatusSequence(this, StatusCodes.INCORRECT)
            coVerify(exactly = 2) { regularValidation.validate() }
            coVerify(exactly = 1) { asyncValidation.validate() }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a field WHEN status change from UNMODIFIED to IN_PROGRESS to CORRECT THEN assert getCurrentStatus gives the respective status on each stage`()
        = runTest {
        val fieldValidationBehavior = mockk<FieldValidationBehavior> {}
        val field = FlowField("id", validationBehavior = fieldValidationBehavior)

        coEvery {
            fieldValidationBehavior.triggerValidations(mutableFieldStatus = any(), emptyList())
        } coAnswers {
            val onValueChangeStatusFlow = it.invocation.args[0] as MutableStateFlow<FieldStatus>
            when (onValueChangeStatusFlow.value.code) {
                UNMODIFIED -> {
                    onValueChangeStatusFlow.value = FieldStatus(IN_PROGRESS)
                    false
                }
                IN_PROGRESS -> {
                    onValueChangeStatusFlow.value = FieldStatus(CORRECT)
                    true
                }
                else -> {
                    onValueChangeStatusFlow.value = FieldStatus(UNMODIFIED)
                    false
                }
            }
        }

        field.status.test {
            awaitItem() // ignores first unmodified initial state (because of empty validation list)

            field.triggerOnValueChangeValidations()
            awaitItem()
            assertEquals(UNMODIFIED, field.getCurrentStatus().code)

            field.triggerOnValueChangeValidations()
            awaitItem()
            assertEquals(IN_PROGRESS, field.getCurrentStatus().code)

            field.triggerOnValueChangeValidations()
            awaitItem()
            assertEquals(CORRECT, field.getCurrentStatus().code)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a field with a validation WHEN it is validated with a different validation then assert the fields validations are not used`()
    = runTest {
        val fieldValidations = listOf(
            validation(ValidationResult.Correct, true)
        )
        val customValidations = listOf(
            validation(ValidationResult.Incorrect, false)
        )
        val fieldValidationBehavior = mockk<FieldValidationBehavior> {
            coEvery {
                triggerValidations(mutableFieldStatus = any(), any(), any())
            } coAnswers { true }
        }
        val field = FlowField(
            "id",
            onValueChangeValidations = fieldValidations,
            onFocusValidations = fieldValidations,
            onBlurValidations = fieldValidations,
            validationBehavior = fieldValidationBehavior
        )

        field.triggerOnValueChangeValidations(validations = customValidations)
        field.triggerOnFocusValidations(validations = customValidations)
        field.triggerOnBlurValidations(validations = customValidations)

        coVerify(exactly = 0) {
            fieldValidationBehavior.triggerValidations(any(), fieldValidations, any())
        }
        coVerify(exactly = 3) {
            fieldValidationBehavior.triggerValidations(any(), customValidations, any())
        }
    }

}
