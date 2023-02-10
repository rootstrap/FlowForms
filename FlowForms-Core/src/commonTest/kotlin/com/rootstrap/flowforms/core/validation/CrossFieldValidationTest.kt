package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.util.validation
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CrossFieldValidationTest {

    @Test
    fun `GIVEN a regular cross field validation WHEN used as is THEN assert it is working as the given validation`()
    = runTest {
        val validation = validation(ValidationResult.Correct, failFast = true, async = true)
        val crossFieldValidation = CrossFieldValidation(
            validation = validation,
            targetFieldId = "targetFieldId"
        )

        assertEquals(validation.failFast, crossFieldValidation.failFast)
        assertEquals(validation.async, crossFieldValidation.async)
        assertEquals(ValidationResult.Correct, crossFieldValidation.validate())
        coVerify(exactly = 1) {
            validation.validate()
        }
    }

}
