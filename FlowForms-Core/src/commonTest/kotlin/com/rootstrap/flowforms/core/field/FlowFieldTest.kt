@file:OptIn(ExperimentalCoroutinesApi::class)

package com.rootstrap.flowforms.core.field

import app.cash.turbine.test
import com.rootstrap.flowforms.core.common.StatusCodes
import com.rootstrap.flowforms.core.util.assertFieldStatusSequence
import com.rootstrap.flowforms.core.util.asyncValidation
import com.rootstrap.flowforms.core.util.getTestDispatcher
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationResult
import com.rootstrap.flowforms.core.validation.ValidationsCancelledException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FlowFieldTest {

    @Test
    fun `GIVEN a new required field THEN assert its status is UNMODIFIED`() = runTest {
        val field = FlowField("email")
        field.status.test {
            assertEquals(awaitItem().code, StatusCodes.UNMODIFIED)
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
            listOf(regularValidation, asyncValidation),
            validationBehavior = DefaultFieldValidationBehavior()
        )

        field.status.test {
            launch {
                try {
                    field.triggerOnValueChangeValidations(testAsyncCoroutineDispatcher)
                } catch (ex : ValidationsCancelledException) {
                    println("First async validation was cancelled : ${ex.message}")
                }
            }
            assertFieldStatusSequence(this, StatusCodes.UNMODIFIED, StatusCodes.IN_PROGRESS)
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

}
