package com.rootstrap.flowforms.core.util

import app.cash.turbine.FlowTurbine
import com.rootstrap.flowforms.core.TEST_IO_DISPATCHER_NAME
import com.rootstrap.flowforms.core.field.FieldStatus
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlin.test.assertEquals


@ExperimentalCoroutinesApi
fun getTestDispatcher(testScheduler: TestCoroutineScheduler): TestDispatcher {
    return StandardTestDispatcher(testScheduler, name = TEST_IO_DISPATCHER_NAME)
}

fun validation(result : ValidationResult, failFast : Boolean = false, async : Boolean = false)
        = mockk<Validation> {
    every { this@mockk.async } returns async
    every { this@mockk.failFast } returns failFast
    coEvery { this@mockk.validate() } coAnswers { result }
}

fun asyncValidation(delayInMillis : Long, result : ValidationResult, failFast : Boolean = false)
        = mockk<Validation> {
    every { async } returns true
    every { this@mockk.failFast } returns failFast
    coEvery { validate() } coAnswers {
        delay(delayInMillis)
        result
    }
}

suspend fun assertFieldStatusSequence(flowTurbine: FlowTurbine<FieldStatus>, vararg statuses: String): FieldStatus {
    var lastValue : FieldStatus? = null
    statuses.forEach {
        lastValue = flowTurbine.awaitItem()
        assertEquals(it, lastValue?.code)
    }
    return lastValue!!
}
