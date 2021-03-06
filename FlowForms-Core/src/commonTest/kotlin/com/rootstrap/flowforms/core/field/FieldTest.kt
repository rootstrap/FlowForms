@file:OptIn(ExperimentalCoroutinesApi::class)

package com.rootstrap.flowforms.core.field

import app.cash.turbine.test
import com.rootstrap.flowforms.core.common.StatusCodes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FieldTest {

    @Test
    fun `GIVEN a new required field THEN assert its status is UNMODIFIED`() = runTest {
        val field = FField("email")
        field.status.test {
            assertEquals(awaitItem().code, StatusCodes.UNMODIFIED)
            cancelAndIgnoreRemainingEvents()
        }
    }

}
