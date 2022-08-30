package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.BASIC_EMAIL_FORMAT_UNSATISFIED
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class BasicEmailFormatTest {

    @Test
    fun `GIVEN a correct basic email THEN assert the validation result is CORRECT`()
            = runTest {
        val basicEmailFormat = BasicEmailFormat { "some@email.com" }
        assertEquals(CORRECT, basicEmailFormat.validate().resultId)
    }

    @Test
    fun `GIVEN an email without '@' THEN assert the validation result is BASIC_EMAIL_FORMAT_UNSATISFIED`()
            = runTest {
        val basicEmailFormat = BasicEmailFormat { "some-email.x" }
        assertEquals(BASIC_EMAIL_FORMAT_UNSATISFIED, basicEmailFormat.validate().resultId)
    }

    @Test
    fun `GIVEN an email without dot THEN assert the validation result is BASIC_EMAIL_FORMAT_UNSATISFIED`()
            = runTest {
        val basicEmailFormat = BasicEmailFormat { "some@email" }
        assertEquals(BASIC_EMAIL_FORMAT_UNSATISFIED, basicEmailFormat.validate().resultId)
    }

    @Test
    fun `GIVEN an email without text after dot THEN assert the validation result is BASIC_EMAIL_FORMAT_UNSATISFIED`()
            = runTest {
        val basicEmailFormat = BasicEmailFormat { "some@email." }
        assertEquals(BASIC_EMAIL_FORMAT_UNSATISFIED, basicEmailFormat.validate().resultId)
    }

    @Test
    fun `GIVEN an email without text between '@' and dot THEN assert the validation result is BASIC_EMAIL_FORMAT_UNSATISFIED`()
            = runTest {
        val basicEmailFormat = BasicEmailFormat { "some@.com" }
        assertEquals(BASIC_EMAIL_FORMAT_UNSATISFIED, basicEmailFormat.validate().resultId)
    }

    @Test
    fun `GIVEN an email without text before '@' THEN assert the validation result is BASIC_EMAIL_FORMAT_UNSATISFIED`()
            = runTest {
        val basicEmailFormat = BasicEmailFormat { "@email.com" }
        assertEquals(BASIC_EMAIL_FORMAT_UNSATISFIED, basicEmailFormat.validate().resultId)
    }

    @Test
    fun `GIVEN a null value THEN assert the validation result is BASIC_EMAIL_FORMAT_UNSATISFIED`()
            = runTest {
        val basicEmailFormat = BasicEmailFormat { null }
        assertEquals(BASIC_EMAIL_FORMAT_UNSATISFIED, basicEmailFormat.validate().resultId)
    }

}
