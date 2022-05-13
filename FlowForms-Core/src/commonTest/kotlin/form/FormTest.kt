package form

import app.cash.turbine.test
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.field.FField
import com.rootstrap.flowforms.core.form.FForm
import com.rootstrap.flowforms.core.validation.Required
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FormTest {

    @Test
    fun `GIVEN a form WHEN created with 2 fields THEN assert it has 2 fields`()
    = runTest {
        val form = FForm()

        form.fields.test {
            form.withFields(
                FField("testField1", emptyList()),
                FField("testField2", onValueChange = emptyList())
            )
            awaitItem()
            assertEquals(2, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN a form WHEN created with 2 required fields THEN assert the base state is UNMODIFIED`()
    = runTest {
        val form = FForm()

        form.status.test {
            form.withFields(
                FField("testField1", listOf(Required { "" }) ),
                FField("testField2", onValueChange = listOf(Required { "" }) )
            )
            assertEquals(awaitItem().code, UNMODIFIED)
            cancelAndIgnoreRemainingEvents()
        }
    }

}
