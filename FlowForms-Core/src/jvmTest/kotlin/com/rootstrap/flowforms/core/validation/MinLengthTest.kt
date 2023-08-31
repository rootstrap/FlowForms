package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MinLengthTest {

    @Test
    fun `GIVEN a MinLength of 3 characters, when the value has 3 THEN assert the validation result is CORRECT`()
    = runTest {
        val minLength = MinLength(3) { "abc" }
        assertEquals(CORRECT, minLength.validate().resultId)
    }

    @Test
    fun `GIVEN a MinLength of 3 characters, when the value has more than 3 THEN assert the validation result is CORRECT`()
    = runTest {
        val minLength = MinLength(3) { "four" }
        assertEquals(CORRECT, minLength.validate().resultId)
    }

    @Test
    fun `GIVEN a MinLength of 3 characters, when the value has more than 3 spaces THEN assert the validation result is CORRECT`()
    = runTest {
        val minLength = MinLength(3) { "    " }
        assertEquals(CORRECT, minLength.validate().resultId)
    }

    @Test
    fun `GIVEN a MinLength of 3 characters, when the value has less than 3 THEN assert the validation result is MIN_LENGTH_UNSATISFIED`()
    = runTest {
        val minLength = MinLength(3) { "ab" }
        assertEquals(StatusCodes.MIN_LENGTH_UNSATISFIED, minLength.validate().resultId)
    }

    @Test
    fun `GIVEN a MinLength of 3 characters, when the value is null THEN assert the validation result is MIN_LENGTH_UNSATISFIED`()
            = runTest {
        val minLength = MinLength(3) { null }
        assertEquals(StatusCodes.MIN_LENGTH_UNSATISFIED, minLength.validate().resultId)
    }

}
