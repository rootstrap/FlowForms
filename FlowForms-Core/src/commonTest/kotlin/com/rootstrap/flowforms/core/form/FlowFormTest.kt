package com.rootstrap.flowforms.core.form

import app.cash.turbine.test
import com.rootstrap.flowforms.core.TEST_IO_DISPATCHER_NAME
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCOMPLETE
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.dsl.flowForm
import com.rootstrap.flowforms.core.field.FieldStatus
import com.rootstrap.flowforms.core.field.FlowField
import com.rootstrap.flowforms.core.util.validation
import com.rootstrap.flowforms.core.validation.CrossFieldValidation
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationResult
import com.rootstrap.flowforms.core.validation.ValidationsCancelledException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

@OptIn(ExperimentalCoroutinesApi::class)
class FlowFormTest {

    @Test
    fun `GIVEN a form WHEN created with 2 fields THEN assert it has 2 fields`()
    = runTest {
        val form = flowForm {
            fields(
                FlowField("testField1", emptyList()),
                FlowField("testField2", onValueChangeValidations = emptyList())
            )
        }

        form.fields.test {
            assertEquals(2, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form WHEN created with 2 fields THEN assert the form status is UNMODIFIED`()
    = runTest {
        val field1 = mockk<FlowField>()
        val field2 = mockk<FlowField>()

        every { field1.id } returns FIELD_ID_1
        every { field1.status } returns flowOf(FieldStatus(fieldId = FIELD_ID_1))
        every { field2.id } returns FIELD_ID_2
        every { field2.status } returns flowOf(FieldStatus(fieldId = FIELD_ID_2))

        flowForm {
            fields(field1, field2)
        }.status.test {
            assertEquals(UNMODIFIED, awaitItem().code)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form with 2 fields WHEN one field become incorrect THEN assert the form status changes from UNMODIFIED to INCORRECT`()
    = runTest {
        val field1 = mockk<FlowField>()
        val field2 = mockk<FlowField>()

        every { field1.id } returns FIELD_ID_1
        every { field1.status } returns flowOf(
            FieldStatus(fieldId = FIELD_ID_1),
            FieldStatus(fieldId = FIELD_ID_1, code = INCORRECT)
        )
        every { field2.id } returns FIELD_ID_2
        every { field2.status } returns flowOf(FieldStatus(fieldId = FIELD_ID_2))

        flowForm {
            fields(field1, field2)
        }.status.test {
            assertEquals(UNMODIFIED, awaitItem().code)
            assertEquals(INCORRECT, awaitItem().code)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form with 2 fields WHEN one field become correct THEN assert the form status changes from UNMODIFIED to INCOMPLETE`()
    = runTest {
        val field1 = mockk<FlowField>()
        val field2 = mockk<FlowField>()

        every { field1.id } returns FIELD_ID_1
        every { field1.status } returns flowOf(FieldStatus(fieldId = FIELD_ID_1))
        every { field2.id } returns FIELD_ID_2
        every { field2.status } returns flowOf(
            FieldStatus(fieldId = FIELD_ID_2),
            FieldStatus(fieldId = FIELD_ID_2, code = CORRECT)
        )

        flowForm {
            fields(field1, field2)
        }.status.test {
            assertEquals(UNMODIFIED, awaitItem().code)
            assertEquals(INCOMPLETE, awaitItem().code)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form with 2 fields WHEN both fields become correct at the same time THEN assert the form status changes from UNMODIFIED to CORRECT`()
    = runTest {
        val field1 = mockk<FlowField>()
        val field2 = mockk<FlowField>()

        every { field1.id } returns FIELD_ID_1
        every { field1.status } returns flowOf(
            FieldStatus(fieldId = FIELD_ID_1),
            FieldStatus(fieldId = FIELD_ID_1, code = CORRECT)
        )
        every { field2.id } returns FIELD_ID_2
        every { field2.status } returns flowOf(
            FieldStatus(fieldId = FIELD_ID_2),
            FieldStatus(fieldId = FIELD_ID_1, code = CORRECT)
        )

        flowForm {
            fields(field1, field2)
        }.status.test {
            assertEquals(UNMODIFIED, awaitItem().code)
            assertEquals(CORRECT, awaitItem().code)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form with 3 fields WHEN only two fields become correct at different times THEN assert the form status changes from UNMODIFIED to INCOMPLETE`()
    = runTest {
        val field1 = mockk<FlowField>()
        val field2 = mockk<FlowField>()
        val field3 = mockk<FlowField>()

        every { field1.id } returns FIELD_ID_1
        every { field1.status } returns flowOf(
            FieldStatus(fieldId = FIELD_ID_1),
            FieldStatus(fieldId = FIELD_ID_1),
            FieldStatus(fieldId = FIELD_ID_1, code = CORRECT)
        )

        every { field2.id } returns FIELD_ID_2
        every { field2.status } returns flowOf(
            FieldStatus(fieldId = FIELD_ID_2),
            FieldStatus(fieldId = FIELD_ID_2, code = CORRECT)
        )

        every { field3.id } returns FIELD_ID_3
        every { field3.status } returns flowOf(FieldStatus(fieldId = FIELD_ID_3))

        flowForm {
            fields(field1, field2, field3)
        }.status.test {
            assertEquals(awaitItem().code, UNMODIFIED)
            assertEquals(awaitItem().code, INCOMPLETE)
            assertEquals(awaitItem().code, INCOMPLETE)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form with 3 fields WHEN one field become correct and one become incorrect THEN assert the form status changes from UNMODIFIED to INCOMPLETE TO INCORRECT`()
    = runTest {
        val field1 = mockk<FlowField>()
        val field2 = mockk<FlowField>()
        val field3 = mockk<FlowField>()

        every { field1.id } returns FIELD_ID_1
        every { field1.status } returns flowOf(
            FieldStatus(fieldId = FIELD_ID_1),
            FieldStatus(fieldId = FIELD_ID_1),
            FieldStatus(fieldId = FIELD_ID_1, code = INCORRECT)
        )

        every { field2.id } returns FIELD_ID_2
        every { field2.status } returns flowOf(
            FieldStatus(fieldId = FIELD_ID_2),
            FieldStatus(fieldId = FIELD_ID_2, code = CORRECT)
        )

        every { field3.id } returns FIELD_ID_3
        every { field3.status } returns flowOf(FieldStatus(fieldId = FIELD_ID_3))

        flowForm {
            fields(field1, field2, field3)
        }.status.test {
            assertEquals(awaitItem().code, UNMODIFIED)
            assertEquals(awaitItem().code, INCOMPLETE)
            assertEquals(awaitItem().code, INCORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form with 3 fields WHEN they become correct at different times THEN assert the form status changes from UNMODIFIED to INCOMPLETE x2 to CORRECT`()
    = runTest {
        val field1 = mockk<FlowField>()
        val field2 = mockk<FlowField>()
        val field3 = mockk<FlowField>()

        every { field1.id } returns FIELD_ID_1
        every { field1.status } returns flowOf(
            FieldStatus(fieldId = FIELD_ID_1),
            FieldStatus(fieldId = FIELD_ID_1, code = CORRECT)
        )

        every { field2.id } returns FIELD_ID_2
        every { field2.status } returns flowOf(
            FieldStatus(fieldId = FIELD_ID_2),
            FieldStatus(fieldId = FIELD_ID_2),
            FieldStatus(fieldId = FIELD_ID_2, code = CORRECT)
        )

        every { field3.id } returns FIELD_ID_3
        every { field3.status } returns flowOf(
            FieldStatus(fieldId = FIELD_ID_3),
            FieldStatus(fieldId = FIELD_ID_3),
            FieldStatus(fieldId = FIELD_ID_3),
            FieldStatus(fieldId = FIELD_ID_3, code = CORRECT)
        )

        flowForm {
            fields(field1, field2, field3)
        }.status.test {
            assertEquals(awaitItem().code, UNMODIFIED)
            assertEquals(awaitItem().code, INCOMPLETE)
            assertEquals(awaitItem().code, INCOMPLETE)
            assertEquals(awaitItem().code, CORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form with a field for each validation option WHEN calling the form's validation methods THEN assert the fields corresponding validations are triggered exactly once`()
    = runTest {
        val coroutineDispatcher = StandardTestDispatcher(testScheduler, name = TEST_IO_DISPATCHER_NAME)
        val field1 = mockkFlowField(id = FIELD_ID_1)
        val field2 = mockkFlowField(id = FIELD_ID_2)
        val field3 = mockkFlowField(id = FIELD_ID_3)

        coEvery { field1.triggerOnValueChangeValidations(coroutineDispatcher) } returns true
        coEvery { field2.triggerOnBlurValidations(coroutineDispatcher) } returns true
        coEvery { field3.triggerOnFocusValidations(coroutineDispatcher) } returns true

        val form = flowForm {
            fields(field1, field2, field3)
            dispatcher = coroutineDispatcher
        }

        form.status.test {
            form.validateOnValueChange(FIELD_ID_1)
            form.validateOnBlur(FIELD_ID_2)
            form.validateOnFocus(FIELD_ID_3)

            coVerify(exactly = 1) { field1.triggerOnValueChangeValidations(coroutineDispatcher) }
            coVerify(exactly = 1) { field2.triggerOnBlurValidations(coroutineDispatcher) }
            coVerify(exactly = 1) { field3.triggerOnFocusValidations(coroutineDispatcher) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form without fields WHEN the validate methods are called THEN assert the app does not crashes`()
    = runTest {
        val form = flowForm { }
        val nonExistentFieldId = "fieldNonExistent"

        try {
            form.validateOnValueChange(nonExistentFieldId)
            form.validateOnBlur(nonExistentFieldId)
            form.validateOnFocus(nonExistentFieldId)
            form.validateAllFields()
        } catch (t : Throwable) {
            fail("form's validate methods crashed after calling them : ${t.message}", t)
        }
    }

    @Test
    fun `GIVEN a form without fields WHEN validateAll is called THEN assert the form is valid`()
            = runTest {
        val form = flowForm { }
        assertTrue(form.validateAllFields())
    }

    @Test
    fun `GIVEN a form without fields WHEN calling the form's validate functions THEN assert the results are all false `()
    = runTest {
        val form = flowForm { }
        val nonExistentFieldId = ""

        form.status.test {
            assertFalse { form.validateOnValueChange(nonExistentFieldId) }
            assertFalse { form.validateOnFocus(nonExistentFieldId) }
            assertFalse { form.validateOnBlur(nonExistentFieldId) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form with two fields with each kind of validation WHEN calling the form's validateAll method and the validations are correct THEN assert all the fields validations are triggered`()
    = runTest {
        val coroutineDispatcher = StandardTestDispatcher(testScheduler, name = TEST_IO_DISPATCHER_NAME)
        val field1 = mockkFlowField(FIELD_ID_1)
        val field2 = mockkFlowField(FIELD_ID_2)

        coEvery { field1.triggerOnValueChangeValidations(coroutineDispatcher) } returns true
        coEvery { field1.triggerOnFocusValidations(coroutineDispatcher) } returns true
        coEvery { field1.triggerOnBlurValidations(coroutineDispatcher) } returns true

        coEvery { field2.triggerOnValueChangeValidations(coroutineDispatcher) } returns true
        coEvery { field2.triggerOnFocusValidations(coroutineDispatcher) } returns true
        coEvery { field2.triggerOnBlurValidations(coroutineDispatcher) } returns true

        val form = flowForm {
            fields(field1, field2)
            dispatcher = coroutineDispatcher
        }

        form.status.test {
            assertTrue(form.validateAllFields())

            coVerify(exactly = 1) { field1.triggerOnValueChangeValidations(coroutineDispatcher) }
            coVerify(exactly = 1) { field1.triggerOnFocusValidations(coroutineDispatcher) }
            coVerify(exactly = 1) { field1.triggerOnBlurValidations(coroutineDispatcher) }

            coVerify(exactly = 1) { field2.triggerOnValueChangeValidations(coroutineDispatcher) }
            coVerify(exactly = 1) { field2.triggerOnFocusValidations(coroutineDispatcher) }
            coVerify(exactly = 1) { field2.triggerOnBlurValidations(coroutineDispatcher) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form with two fields with each kind of validation WHEN calling the form's validateAll method and at onValueChange it is not correct THEN assert only onValueChangeValidations were triggered`()
    = runTest {
        val coroutineDispatcher = StandardTestDispatcher(testScheduler, name = TEST_IO_DISPATCHER_NAME)
        val field1 = mockk<FlowField>()
        val field2 = mockk<FlowField>()

        every { field1.id } returns FIELD_ID_1
        every { field1.status } returns flowOf(FieldStatus(fieldId = FIELD_ID_1))
        coEvery { field1.triggerOnValueChangeValidations(coroutineDispatcher) } returns false
        coEvery { field1.triggerOnFocusValidations(coroutineDispatcher) } returns true
        coEvery { field1.triggerOnBlurValidations(coroutineDispatcher) } returns true

        every { field2.id } returns FIELD_ID_2
        every { field2.status } returns flowOf(FieldStatus(fieldId = FIELD_ID_2))
        coEvery { field2.triggerOnValueChangeValidations(coroutineDispatcher) } returns false
        coEvery { field2.triggerOnFocusValidations(coroutineDispatcher) } returns true
        coEvery { field2.triggerOnBlurValidations(coroutineDispatcher) } returns true

        val form = flowForm {
            fields(field1, field2)
            dispatcher = coroutineDispatcher
        }

        form.status.test {
            assertFalse(form.validateAllFields())

            coVerify(exactly = 1) { field1.triggerOnValueChangeValidations(coroutineDispatcher) }
            coVerify(exactly = 0) { field1.triggerOnBlurValidations(coroutineDispatcher) }
            coVerify(exactly = 0) { field1.triggerOnFocusValidations(coroutineDispatcher) }

            coVerify(exactly = 1) { field2.triggerOnValueChangeValidations(coroutineDispatcher) }
            coVerify(exactly = 0) { field2.triggerOnBlurValidations(coroutineDispatcher) }
            coVerify(exactly = 0) { field2.triggerOnFocusValidations(coroutineDispatcher) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form with two fields with each kind of validation WHEN calling the form's validateAll method and at onFocus it is not correct THEN assert that onBlur validations were not triggered`()
    = runTest {
        val coroutineDispatcher = StandardTestDispatcher(testScheduler, name = TEST_IO_DISPATCHER_NAME)

        val field1 = mockkFlowField(FIELD_ID_1)
        val field2 = mockkFlowField(FIELD_ID_2)

        coEvery { field1.triggerOnValueChangeValidations(coroutineDispatcher) } returns true
        coEvery { field1.triggerOnFocusValidations(coroutineDispatcher) } returns false
        coEvery { field1.triggerOnBlurValidations(coroutineDispatcher) } returns true

        coEvery { field2.triggerOnValueChangeValidations(coroutineDispatcher) } returns true
        coEvery { field2.triggerOnFocusValidations(coroutineDispatcher) } returns false
        coEvery { field2.triggerOnBlurValidations(coroutineDispatcher) } returns true

        val form = flowForm {
            fields(field1, field2)
            dispatcher = coroutineDispatcher
        }

        form.status.test {
            assertFalse(form.validateAllFields())

            coVerify(exactly = 1) { field1.triggerOnValueChangeValidations(coroutineDispatcher) }
            coVerify(exactly = 1) { field1.triggerOnFocusValidations(coroutineDispatcher) }
            coVerify(exactly = 0) { field1.triggerOnBlurValidations(coroutineDispatcher) }

            coVerify(exactly = 1) { field2.triggerOnValueChangeValidations(coroutineDispatcher) }
            coVerify(exactly = 1) { field2.triggerOnFocusValidations(coroutineDispatcher) }
            coVerify(exactly = 0) { field2.triggerOnBlurValidations(coroutineDispatcher) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form with 1 CORRECT field with cross-field validations and, 1 CORRECT and 1 INCORRECT fields WHEN field one is validated THEN assert the other fields are also validated`()
    = runTest {
        val crossFieldValidation = CrossFieldValidation(validation(ValidationResult.Correct), FIELD_ID_2)
        val crossFieldValidation2 = CrossFieldValidation(validation(ValidationResult.Correct), FIELD_ID_2)
        val crossFieldValidation3 = CrossFieldValidation(validation(ValidationResult.Correct), FIELD_ID_3)
        val field1RegularValidation = validation(ValidationResult.Correct)

        val field1Validations = listOf(
            field1RegularValidation,
            crossFieldValidation,
            crossFieldValidation2,
            crossFieldValidation3
        )
        val field2Validations = listOf(validation(ValidationResult.Correct, failFast = true))

        val field1 = mockkFlowField(FIELD_ID_1, allValidationsList = field1Validations)
        coEvery { field1.triggerOnValueChangeValidations(any(), any()) } returns true
        coEvery { field1.triggerOnFocusValidations(any(), any()) } returns true
        coEvery { field1.triggerOnBlurValidations(any(), any()) } returns true

        val field2 = mockkFlowField(FIELD_ID_2, allValidationsList = field2Validations)
        coEvery { field2.triggerOnValueChangeValidations(any(), any()) } returns true
        coEvery { field2.triggerOnFocusValidations(any(), any()) } returns true
        coEvery { field2.triggerOnBlurValidations(any(), any()) } returns true
        every { field2.getCurrentStatus() } returns FieldStatus(fieldId = FIELD_ID_2, code = CORRECT)

        val field3 = mockkFlowField(FIELD_ID_3)
        coEvery { field3.triggerOnValueChangeValidations(any(), any()) } returns true
        coEvery { field3.triggerOnFocusValidations(any(), any()) } returns true
        coEvery { field3.triggerOnBlurValidations(any(), any()) } returns true
        every { field3.getCurrentStatus() } returns FieldStatus(fieldId = FIELD_ID_3, code = INCORRECT)

        val form = flowForm {
            fields(field1, field2, field3)
        }

        form.validateOnValueChange(field1.id)
        coVerify(exactly = 1) {
            field1.triggerOnValueChangeValidations(validations = emptyList())
            field2.triggerOnValueChangeValidations(validations = listOf(
                crossFieldValidation.validation,
                crossFieldValidation2.validation
            ))
            field3.triggerOnValueChangeValidations(validations = listOf(
                crossFieldValidation3.validation
            ))
        }

        form.validateOnFocus(field1.id)
        coVerify(exactly = 1) {
            field1.triggerOnFocusValidations(validations = emptyList())
            field2.triggerOnFocusValidations(validations = listOf(
                crossFieldValidation.validation,
                crossFieldValidation2.validation
            ))
            field3.triggerOnFocusValidations(validations = listOf(
                crossFieldValidation3.validation
            ))
        }

        form.validateOnBlur(field1.id)
        coVerify(exactly = 1) {
            field1.triggerOnBlurValidations(validations = emptyList())
            field2.triggerOnBlurValidations(validations = listOf(
                crossFieldValidation.validation,
                crossFieldValidation2.validation
            ))
            field3.triggerOnBlurValidations(validations = listOf(
                crossFieldValidation3.validation
            ))
        }
    }

    @Test
    fun `GIVEN a form with 1 CORRECT field with cross-field validations and 1 UNMODIFIED field WHEN the field one is validated THEN assert the other field is not`()
    = runTest {
        val crossFieldValidation = CrossFieldValidation(validation(ValidationResult.Correct), FIELD_ID_2)
        val field1RegularValidation = validation(ValidationResult.Correct)

        val field1Validations = listOf(
            field1RegularValidation,
            crossFieldValidation,
        )
        val field2Validations = listOf(validation(ValidationResult.Correct, failFast = true))

        val field1 = mockkFlowField(FIELD_ID_1, allValidationsList = field1Validations)
        coEvery { field1.triggerOnValueChangeValidations(any(), any()) } returns true
        coEvery { field1.triggerOnFocusValidations(any(), any()) } returns true
        coEvery { field1.triggerOnBlurValidations(any(), any()) } returns true

        val field2 = mockkFlowField(FIELD_ID_2, allValidationsList = field2Validations)
        coEvery { field2.triggerOnValueChangeValidations(any(), any()) } returns true
        coEvery { field2.triggerOnFocusValidations(any(), any()) } returns true
        coEvery { field2.triggerOnBlurValidations(any(), any()) } returns true
        every { field2.getCurrentStatus() } returns FieldStatus(fieldId = FIELD_ID_2, code = UNMODIFIED)

        val form = flowForm {
            fields(field1, field2)
        }

        form.validateOnValueChange(field1.id)
        coVerify(exactly = 1) {
            field1.triggerOnValueChangeValidations(validations = emptyList())
        }
        coVerify(exactly = 0) {
            field2.triggerOnValueChangeValidations(validations = listOf(crossFieldValidation.validation))
        }

        form.validateOnFocus(field1.id)
        coVerify(exactly = 1) {
            field1.triggerOnFocusValidations(validations = emptyList())
        }
        coVerify(exactly = 0) {
            field2.triggerOnFocusValidations(validations = listOf(crossFieldValidation.validation))
        }

        form.validateOnBlur(field1.id)
        coVerify(exactly = 1) {
            field1.triggerOnBlurValidations(validations = emptyList())
        }
        coVerify(exactly = 0) {
            field2.triggerOnBlurValidations(validations = listOf(crossFieldValidation.validation))
        }
    }

    @Test
    fun `GIVEN a form with 1 INCORRECT field with cross-field validations and 1 UNMODIFIED field WHEN field one is validated THEN assert the other field is not`()
    = runTest {
        val crossFieldValidation = CrossFieldValidation(validation(ValidationResult.Correct), FIELD_ID_2)
        val field1RegularValidation = validation(ValidationResult.Incorrect)
        val field1Validations = listOf(
            field1RegularValidation,
            crossFieldValidation,
        )
        val field1 = mockkFlowField(FIELD_ID_1, allValidationsList = field1Validations)
        coEvery { field1.triggerOnValueChangeValidations(any(), any()) } returns false
        coEvery { field1.triggerOnFocusValidations(any(), any()) } returns false
        coEvery { field1.triggerOnBlurValidations(any(), any()) } returns false
        val field2 = mockkFlowField(FIELD_ID_2)

        val form = flowForm {
            fields(field1, field2)
        }

        form.validateOnValueChange(field1.id)
        coVerify(exactly = 1) {
            field1.triggerOnValueChangeValidations(validations = emptyList())
        }
        coVerify(exactly = 0) {
            field2.triggerOnValueChangeValidations(any(), any())
        }

        form.validateOnFocus(field1.id)
        coVerify(exactly = 1) {
            field1.triggerOnFocusValidations(validations = emptyList())
        }
        coVerify(exactly = 0) {
            field2.triggerOnValueChangeValidations(any(), any())
        }

        form.validateOnBlur(field1.id)
        coVerify(exactly = 1) {
            field1.triggerOnBlurValidations(validations = emptyList())
        }
        coVerify(exactly = 0) {
            field2.triggerOnValueChangeValidations(any(), any())
        }
    }

    @Test
    fun `GIVEN a form with only 1 CORRECT field with a cross-field validation WHEN the field is validated THEN assert it does not crash because dye to the missing target field`()
    = runTest {
        val crossFieldValidation = CrossFieldValidation(validation(ValidationResult.Correct), "non-existing-field-id")
        val field1RegularValidation = validation(ValidationResult.Correct)
        val field1Validations = listOf(
            field1RegularValidation,
            crossFieldValidation,
        )
        val field1 = mockkFlowField(FIELD_ID_1, allValidationsList = field1Validations)
        coEvery { field1.triggerOnValueChangeValidations(any(), any()) } returns true
        coEvery { field1.triggerOnFocusValidations(any(), any()) } returns true
        coEvery { field1.triggerOnBlurValidations(any(), any()) } returns true

        val form = flowForm {
            fields(field1)
        }

        form.validateOnValueChange(field1.id)
        coVerify(exactly = 1) {
            field1.triggerOnValueChangeValidations(validations = emptyList())
        }

        form.validateOnFocus(field1.id)
        coVerify(exactly = 1) {
            field1.triggerOnFocusValidations(validations = emptyList())
        }

        form.validateOnBlur(field1.id)
        coVerify(exactly = 1) {
            field1.triggerOnBlurValidations(validations = emptyList())
        }
        // if this test doesn't crash it means it worked.
    }

    @Test
    fun `GIVEN a form with 1 field WHEN validation process is cancelled THEN assert the validation result is false`()
    = runTest {
        val field1 = mockkFlowField(FIELD_ID_1)
        coEvery { field1.triggerOnValueChangeValidations(any(), any()) } throws ValidationsCancelledException("test exception")
        coEvery { field1.triggerOnFocusValidations(any(), any()) } throws ValidationsCancelledException("test exception")
        coEvery { field1.triggerOnBlurValidations(any(), any()) } throws ValidationsCancelledException("test exception")

        val form = flowForm {
            fields(field1)
        }

        assertFalse { form.validateOnValueChange(field1.id) }
        assertFalse { form.validateOnFocus(field1.id) }
        assertFalse { form.validateOnBlur(field1.id) }
    }

    @Test
    fun `GIVEN a form with 2 fields WHEN getting them by id THEN assert they are retrieved`() {
        val field1 = mockkFlowField(FIELD_ID_1)
        val field2 = mockkFlowField(FIELD_ID_2)

        val form = flowForm {
            fields(field1, field2)
        }

        assertEquals(field1, form.field(FIELD_ID_1))
        assertEquals(field2, form.field(FIELD_ID_2))
    }

    @Test
    fun `GIVEN a form WHEN getting a field with an incorrect id THEN assert null is retrieved`() {
        val field1 = mockkFlowField(FIELD_ID_1)
        val field2 = mockkFlowField(FIELD_ID_2)

        val form = flowForm {
            fields(field1, field2)
        }

        val emptyForm = flowForm { }

        assertNull(form.field("non-existing-field-id"))
        assertNull(emptyForm.field("non-existing-field-id"))
    }

    private fun mockkFlowField(
        id : String? = null,
        status: Flow<FieldStatus> = flowOf(FieldStatus(fieldId = id.orEmpty())),
        allValidationsList : List<Validation> = emptyList()
    ) = mockk<FlowField> {
        every { this@mockk.onValueChangeValidations } returns allValidationsList
        every { this@mockk.onFocusValidations } returns allValidationsList
        every { this@mockk.onBlurValidations } returns allValidationsList
        id?.let { every { this@mockk.id } returns id }
        every { this@mockk.status } returns status
    }

    companion object {
        private const val FIELD_ID_1 = "field1"
        private const val FIELD_ID_2 = "field2"
        private const val FIELD_ID_3 = "field3"
    }

}
