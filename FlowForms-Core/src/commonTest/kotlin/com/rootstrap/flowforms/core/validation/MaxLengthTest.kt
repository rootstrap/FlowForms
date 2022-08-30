package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.MAX_LENGTH_UNSATISFIED
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MaxLengthTest {

    @Test
    fun `GIVEN a MaxLength of 3 characters, when the value has 3 THEN assert the validation result is CORRECT`()
    = runTest {
        val maxLength = MaxLength(3) { "abc" }
        assertEquals(CORRECT, maxLength.validate().resultId)
    }

    @Test
    fun `GIVEN a MaxLength of 3 characters, when the value has more than 3 THEN assert the validation result is MAX_LENGTH_UNSATISFIED`()
    = runTest {
        val maxLength = MaxLength(3) { "four" }
        assertEquals(MAX_LENGTH_UNSATISFIED, maxLength.validate().resultId)
    }

    @Test
    fun `GIVEN a MaxLength of 3 characters, when the value has more than 3 spaces THEN assert the validation result is MAX_LENGTH_UNSATISFIED`()
    = runTest {
        val maxLength = MaxLength(3) { "    " }
        assertEquals(MAX_LENGTH_UNSATISFIED, maxLength.validate().resultId)
    }

    @Test
    fun `GIVEN a MaxLength of 3 characters, when the value has less than 3 THEN assert the validation result is CORRECT`()
    = runTest {
        val maxLength = MaxLength(3) { "ab" }
        assertEquals(CORRECT, maxLength.validate().resultId)
    }

    @Test
    fun `GIVEN a MaxLength of 3 characters, when the value is null THEN assert the validation result is MAX_LENGTH_UNSATISFIED`()
            = runTest {
        val maxLength = MaxLength(3) { null }
        assertEquals(MAX_LENGTH_UNSATISFIED, maxLength.validate().resultId)
    }

}
