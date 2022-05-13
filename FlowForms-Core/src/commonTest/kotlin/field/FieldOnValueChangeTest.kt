package field

import app.cash.turbine.test
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.field.FField
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FieldOnValueChangeTest {

    @Test
    fun `GIVEN a correct onValueChange validation THEN assert the field status is CORRECT`()
    = runTest {
        val correctValidation = mockk<Validation>()

        every { correctValidation.validate() } returns ValidationResult.Correct

        val field = FField("email", listOf( correctValidation ) )

        field.status.test {
            awaitItem() // UNMODIFIED status
            field.triggerOnValueChangeValidations()
            assertEquals(awaitItem().code, CORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a failing onValueChange validation THEN assert the field status is INCORRECT`()
    = runTest {
        val failingValidation = mockk<Validation>()

        every { failingValidation.validate() } returns ValidationResult(INCORRECT)
        every { failingValidation.failFast } returns false

        val field = FField("email", listOf( failingValidation ) )

        field.status.test {
            awaitItem() // UNMODIFIED status
            field.triggerOnValueChangeValidations()
            assertEquals(awaitItem().code, INCORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a correct and a failing onValueChange validations THEN assert the field status is INCORRECT`()
    = runTest {
        val correctValidation = mockk<Validation>()
        val failingValidation = mockk<Validation>()

        every { correctValidation.validate() } returns ValidationResult.Correct
        every { correctValidation.failFast } returns false
        every { failingValidation.validate() } returns ValidationResult(INCORRECT)
        every { failingValidation.failFast } returns false

        val field = FField("email", listOf( correctValidation, failingValidation ) )

        field.status.test {
            awaitItem() // UNMODIFIED status
            field.triggerOnValueChangeValidations()
            assertEquals(awaitItem().code, INCORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a failing and a correct onValueChange validations THEN assert the field status is INCORRECT`()
    = runTest {
        val correctValidation = mockk<Validation>()
        val failingValidation = mockk<Validation>()

        every { failingValidation.validate() } returns ValidationResult(INCORRECT)
        every { failingValidation.failFast } returns false
        every { correctValidation.validate() } returns ValidationResult.Correct
        every { correctValidation.failFast } returns false

        val field = FField("email", listOf( failingValidation, correctValidation ) )

        field.status.test {
            awaitItem() // UNMODIFIED status
            field.triggerOnValueChangeValidations()
            assertEquals(awaitItem().code, INCORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a failing failFast and a correct onValueChange validations THEN assert the correct validation is not executed`()
    = runTest {
        val correctValidation = mockk<Validation>()
        val failingValidation = mockk<Validation>()

        every { failingValidation.validate() } returns ValidationResult(INCORRECT)
        every { failingValidation.failFast } returns true
        every { correctValidation.validate() } returns ValidationResult(CORRECT)
        every { correctValidation.failFast } returns true

        val field = FField("email", listOf( failingValidation, correctValidation ) )

        field.status.test {
            field.triggerOnValueChangeValidations()
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) { failingValidation.validate() }
        verify(exactly = 0) { correctValidation.validate() }
    }

}
