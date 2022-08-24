package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.REQUIRED_FALSE_UNSATISFIED
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RequiredFalseTest {

    @Test
    fun `GIVEN true THEN assert the validation result is REQUIRED_FALSE_UNSATISFIED`() = runTest {
        val requiredFalse = RequiredFalse { true }
        assertEquals(REQUIRED_FALSE_UNSATISFIED, requiredFalse.validate().resultId)
    }

    @Test
    fun `GIVEN false THEN assert the validation result is CORRECT`() = runTest {
        val requiredFalse = RequiredFalse { false }
        assertEquals(CORRECT, requiredFalse.validate().resultId)
    }

    @Test
    fun `GIVEN null THEN assert the validation result is REQUIRED_FALSE_UNSATISFIED`() = runTest {
        val requiredFalse = RequiredFalse { null }
        assertEquals(REQUIRED_FALSE_UNSATISFIED, requiredFalse.validate().resultId)
    }

}
