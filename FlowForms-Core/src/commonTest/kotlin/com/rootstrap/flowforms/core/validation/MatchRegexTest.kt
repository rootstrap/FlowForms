package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.MATCH_REGEX_UNSATISFIED
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MatchRegexTest {

    @Test
    fun `GIVEN a regex that matches the given value THEN assert the validation result is CORRECT`()
    = runTest {
        val stringToMatch = "abc"
        val regex = mockk<Regex>()
        every { regex.matches(stringToMatch) } returns true

        val matchRegex = MatchRegex(regex) { stringToMatch }

        assertEquals(CORRECT, matchRegex.validate().resultId)
    }

    @Test
    fun `GIVEN a regex that does not match the given value THEN assert the validation result is MATCH_REGEX_UNSATISFIED`()
            = runTest {
        val stringToMatch = "abc"
        val regex = mockk<Regex>()
        every { regex.matches(stringToMatch) } returns false

        val matchRegex = MatchRegex(regex) { stringToMatch }

        assertEquals(MATCH_REGEX_UNSATISFIED, matchRegex.validate().resultId)
    }

    @Test
    fun `GIVEN a null value THEN assert the validation result is MATCH_REGEX_UNSATISFIED`()
            = runTest {
        val regex = mockk<Regex>()
        val matchRegex = MatchRegex(regex) { null }

        assertEquals(MATCH_REGEX_UNSATISFIED, matchRegex.validate().resultId)
    }

}
