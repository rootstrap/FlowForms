package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.REQUIRED_UNSATISFIED
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RequiredTest {

    @Test
    fun `GIVEN a string with only spaces THEN assert the validation result is CORRECT`() = runTest {
        val required = Required { "   " }
        assertEquals(CORRECT, required.validate().resultId)
    }

    @Test
    fun `GIVEN a string with some text THEN assert the validation result is CORRECT`() = runTest {
        val required = Required { "text" }
        assertEquals(CORRECT, required.validate().resultId)
    }

    @Test
    fun `GIVEN a null value THEN assert the validation result is REQUIRED_UNSATISFIED`() = runTest {
        val required = Required { null }
        assertEquals(REQUIRED_UNSATISFIED, required.validate().resultId)
    }

    @Test
    fun `GIVEN an empty string THEN assert the validation result is REQUIRED_UNSATISFIED`() = runTest {
        val required = Required { "" }
        assertEquals(REQUIRED_UNSATISFIED, required.validate().resultId)
    }

}
