package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.REQUIRED_UNSATISFIED
import kotlin.test.Test
import kotlin.test.assertEquals

class RequiredTest {

    @Test
    fun `GIVEN a string with only spaces THEN assert the validation result is CORRECT`() {
        val required = Required { "   " }
        assertEquals(CORRECT, required.validate().resultId)
    }

    @Test
    fun `GIVEN a string with some text THEN assert the validation result is CORRECT`() {
        val required = Required { "text" }
        assertEquals(CORRECT, required.validate().resultId)
    }

    @Test
    fun `GIVEN a null value THEN assert the validation result is REQUIRED_UNSATISFIED`() {
        val required = Required { null }
        assertEquals(REQUIRED_UNSATISFIED, required.validate().resultId)
    }

    @Test
    fun `GIVEN an empty string THEN assert the validation result is REQUIRED_UNSATISFIED`() {
        val required = Required { "" }
        assertEquals(REQUIRED_UNSATISFIED, required.validate().resultId)
    }

}
