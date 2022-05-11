package form

import com.rootstrap.flowforms.core.field.FField
import com.rootstrap.flowforms.core.form.FForm
import com.rootstrap.flowforms.core.validation.Required
import kotlin.test.Test
import kotlin.test.assertTrue

class FormTest {

    object EmptyFormModel { }

    @Test
    fun `GIVEN a form WHEN created with 2 fields THEN assert it has 2 fields`() {
        val form = FForm().withFields(listOf(
            FField("testField1", listOf(Required { "1" }, Required { "2" })),
            FField("testField2",
                onValueChange = listOf(Required { "1" }, Required { "2" })
            )
        ))

        assertTrue { form.fields.size == 2 }
    }

}
