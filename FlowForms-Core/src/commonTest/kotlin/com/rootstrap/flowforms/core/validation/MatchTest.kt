package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.MATCH_UNSATISFIED
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MatchTest {

    @Test
    fun `GIVEN two equal ints THEN assert the validation result is CORRECT`()
            = runTest {
        val match = Match { 1 to 1 }
        assertEquals(CORRECT, match.validate().resultId)
    }

    @Test
    fun `GIVEN two different ints THEN assert the validation result is MATCH_UNSATISFIED`()
            = runTest {
        val match = Match { 1 to 10 }
        assertEquals(MATCH_UNSATISFIED, match.validate().resultId)
    }

    @Test
    fun `GIVEN two null values THEN assert the validation result is CORRECT`()
            = runTest {
        val match = Match { null to null }
        assertEquals(CORRECT, match.validate().resultId)
    }

    @Test
    fun `GIVEN two empty strings THEN assert the validation result is CORRECT`()
            = runTest {
        val match = Match { "" to "" }
        assertEquals(CORRECT, match.validate().resultId)
    }

    @Test
    fun `GIVEN an empty string and a string with one space THEN assert the validation result is MATCH_UNSATISFIED`()
            = runTest {
        val match = Match { "" to " " }
        assertEquals(MATCH_UNSATISFIED, match.validate().resultId)
    }

    @Test
    fun `GIVEN two equal strings THEN assert the validation result is CORRECT`()
            = runTest {
        val match = Match { "aBc" to "aBc" }
        assertEquals(CORRECT, match.validate().resultId)
    }

    @Test
    fun `GIVEN two different strings THEN assert the validation result is MATCH_UNSATISFIED`()
            = runTest {
        val match = Match { "abc" to "aBc" }
        assertEquals(MATCH_UNSATISFIED, match.validate().resultId)
    }

    @Test
    fun `GIVEN two equal objects THEN assert the validation result is CORRECT`()
            = runTest {
        val givenObject = mockk<Any>()
        val objectToMatch = mockk<Any>()
        every { givenObject == objectToMatch } returns true

        val match = Match { givenObject to objectToMatch }
        assertEquals(CORRECT, match.validate().resultId)
    }

    @Test
    fun `GIVEN two non-equal objects THEN assert the validation result is MATCH_UNSATISFIED`()
            = runTest {
        val givenObject = mockk<Any>()
        val objectToMatch = mockk<Any>()
        every { givenObject == objectToMatch } returns false

        val match = Match { givenObject to objectToMatch }
        assertEquals(MATCH_UNSATISFIED, match.validate().resultId)
    }

}
