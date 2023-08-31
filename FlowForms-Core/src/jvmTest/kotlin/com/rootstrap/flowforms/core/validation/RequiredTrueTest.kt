package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.REQUIRED_TRUE_UNSATISFIED
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RequiredTrueTest {

    @Test
    fun `GIVEN true THEN assert the validation result is CORRECT`() = runTest {
        val requiredTrue = RequiredTrue { true }
        assertEquals(CORRECT, requiredTrue.validate().resultId)
    }

    @Test
    fun `GIVEN false THEN assert the validation result is REQUIRED_TRUE_UNSATISFIED`() = runTest {
        val requiredTrue = RequiredTrue { false }
        assertEquals(REQUIRED_TRUE_UNSATISFIED, requiredTrue.validate().resultId)
    }

    @Test
    fun `GIVEN null THEN assert the validation result is REQUIRED_TRUE_UNSATISFIED`() = runTest {
        val requiredTrue = RequiredTrue { null }
        assertEquals(REQUIRED_TRUE_UNSATISFIED, requiredTrue.validate().resultId)
    }

}
