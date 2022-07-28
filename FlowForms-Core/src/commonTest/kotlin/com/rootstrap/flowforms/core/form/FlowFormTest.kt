package com.rootstrap.flowforms.core.form

import app.cash.turbine.test
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCOMPLETE
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.field.FieldStatus
import com.rootstrap.flowforms.core.field.FlowField
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FlowFormTest {

    @Test
    fun `GIVEN a form WHEN created with 2 fields THEN assert it has 2 fields`()
    = runTest {
        val form = FlowForm()

        form.fields.test {
            form.withFields(
                FlowField("testField1", emptyList()),
                FlowField("testField2", onValueChangeValidations = emptyList())
            )
            awaitItem()
            assertEquals(2, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form WHEN created with 2 fields THEN assert the form status is UNMODIFIED`()
    = runTest {
        val form = FlowForm()

        val field1 = mockk<FlowField>()
        val field2 = mockk<FlowField>()

        every { field1.id } returns "field1"
        every { field1.status } returns flowOf(FieldStatus())
        every { field2.id } returns "field2"
        every { field2.status } returns flowOf(FieldStatus())

        form.status.test {
            form.withFields(field1, field2)
            assertEquals(awaitItem().code, UNMODIFIED)
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `GIVEN a form with 2 fields WHEN one field become incorrect THEN assert the form status changes from UNMODIFIED to INCORRECT`()
    = runTest {
        val form = FlowForm()

        val field1 = mockk<FlowField>()
        val field2 = mockk<FlowField>()

        every { field1.id } returns "field1"
        every { field1.status } returns flowOf(FieldStatus(), FieldStatus(INCORRECT))
        every { field2.id } returns "field2"
        every { field2.status } returns flowOf(FieldStatus())

        form.status.test {
            form.withFields(field1, field2)
            assertEquals(awaitItem().code, UNMODIFIED)
            assertEquals(awaitItem().code, INCORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form with 2 fields WHEN one field become correct THEN assert the form status changes from UNMODIFIED to INCOMPLETE`()
            = runTest {
        val form = FlowForm()

        val field1 = mockk<FlowField>()
        val field2 = mockk<FlowField>()

        every { field1.id } returns "field1"
        every { field1.status } returns flowOf(FieldStatus())
        every { field2.id } returns "field2"
        every { field2.status } returns flowOf(FieldStatus(), FieldStatus(CORRECT))

        form.status.test {
            form.withFields(field1, field2)
            assertEquals(awaitItem().code, UNMODIFIED)
            assertEquals(awaitItem().code, INCOMPLETE)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form with 2 fields WHEN both fields become correct at the same time THEN assert the form status changes from UNMODIFIED to CORRECT`()
            = runTest {
        val form = FlowForm()

        val field1 = mockk<FlowField>()
        val field2 = mockk<FlowField>()

        every { field1.id } returns "field1"
        every { field1.status } returns flowOf(FieldStatus(), FieldStatus(CORRECT))
        every { field2.id } returns "field2"
        every { field2.status } returns flowOf(FieldStatus(), FieldStatus(CORRECT))

        form.status.test {
            form.withFields(field1, field2)
            assertEquals(awaitItem().code, UNMODIFIED)
            assertEquals(awaitItem().code, CORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form with 3 fields WHEN only two fields become correct at different times THEN assert the form status changes from UNMODIFIED to INCOMPLETE`()
            = runTest {
        val form = FlowForm()

        val field1 = mockk<FlowField>()
        val field2 = mockk<FlowField>()
        val field3 = mockk<FlowField>()

        every { field1.id } returns "field1"
        every { field1.status } returns flowOf(FieldStatus(), FieldStatus(), FieldStatus(CORRECT))

        every { field2.id } returns "field2"
        every { field2.status } returns flowOf(FieldStatus(), FieldStatus(CORRECT))

        every { field3.id } returns "field3"
        every { field3.status } returns flowOf(FieldStatus())

        form.status.test {
            form.withFields(field1, field2, field3)
            assertEquals(awaitItem().code, UNMODIFIED)
            assertEquals(awaitItem().code, INCOMPLETE)
            assertEquals(awaitItem().code, INCOMPLETE)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form with 3 fields WHEN one field become correct and one become incorrect THEN assert the form status changes from UNMODIFIED to INCOMPLETE TO INCORRECT`()
            = runTest {
        val form = FlowForm()

        val field1 = mockk<FlowField>()
        val field2 = mockk<FlowField>()
        val field3 = mockk<FlowField>()

        every { field1.id } returns "field1"
        every { field1.status } returns flowOf(FieldStatus(), FieldStatus(), FieldStatus(INCORRECT))

        every { field2.id } returns "field2"
        every { field2.status } returns flowOf(FieldStatus(), FieldStatus(CORRECT))

        every { field3.id } returns "field3"
        every { field3.status } returns flowOf(FieldStatus())

        form.status.test {
            form.withFields(field1, field2, field3)
            assertEquals(awaitItem().code, UNMODIFIED)
            assertEquals(awaitItem().code, INCOMPLETE)
            assertEquals(awaitItem().code, INCORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form with 3 fields WHEN from they become correct at different times THEN assert the form status changes from UNMODIFIED to INCOMPLETE x2 to CORRECT`()
            = runTest {
        val form = FlowForm()

        val field1 = mockk<FlowField>()
        val field2 = mockk<FlowField>()
        val field3 = mockk<FlowField>()

        every { field1.id } returns "field1"
        every { field1.status } returns flowOf(FieldStatus(), FieldStatus(CORRECT))

        every { field2.id } returns "field2"
        every { field2.status } returns flowOf(FieldStatus(), FieldStatus(), FieldStatus(CORRECT))

        every { field3.id } returns "field3"
        every { field3.status } returns flowOf(FieldStatus(), FieldStatus(), FieldStatus(), FieldStatus(CORRECT))

        form.status.test {
            form.withFields(field1, field2, field3)
            assertEquals(awaitItem().code, UNMODIFIED)
            assertEquals(awaitItem().code, INCOMPLETE)
            assertEquals(awaitItem().code, INCOMPLETE)
            assertEquals(awaitItem().code, CORRECT)
            cancelAndIgnoreRemainingEvents()
        }
    }

}
