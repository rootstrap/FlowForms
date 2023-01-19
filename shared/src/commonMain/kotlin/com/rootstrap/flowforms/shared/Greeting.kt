package com.rootstrap.flowforms.shared

import com.rootstrap.flowforms.core.common.StatusCodes
import com.rootstrap.flowforms.core.dsl.flowForm
import com.rootstrap.flowforms.core.field.FieldDefinition
import com.rootstrap.flowforms.core.field.FieldStatus
import com.rootstrap.flowforms.core.field.FlowField
import com.rootstrap.flowforms.core.form.FlowForm
import com.rootstrap.flowforms.core.validation.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class Greeting {
    private val platform: Platform = getPlatform()

    fun greeting(): String {
        return "Hello, ${platform.name}!"
    }
}

class ClaseNueva {
    var title: String = ""
}

class FormModel {
    var termsAccepted: Boolean = false
    var name: String = ""
    var email: String = ""
    var password: String = ""
    var confirmPassword: String = ""

    val form = flowForm {
        field(CONFIRMATION, RequiredTrue { termsAccepted })
        field(NAME, Required { name })
        field(EMAIL, BasicEmailFormat { email }, Required { email})
        field(PASSWORD, MinLength(8) { password })
        field(CONFIRM_PASSWORD, MinLength(8) { confirmPassword}, Match { password to confirmPassword })
    }

    companion object {
        const val NAME = "name"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val CONFIRM_PASSWORD = "confirm_password"
        const val CONFIRMATION = "confirmation"
    }
}

class FormModelPublishers {
    private val formModel = FormModel()
}

fun FlowForm.fieldFor(id: String) = fields.value[id] as FlowField

fun FlowField.onStatusChange(onEach: (FieldStatus) -> Unit) : Cancellable {
    return status.collectWithCallback(onEach)
}

interface Cancellable {
    fun cancel()
}

private fun <T> Flow<T>.collectWithCallback(onEach: (T) -> Unit): Cancellable {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    scope.launch {
        collect {
            onEach(it)
        }
    }

    return object : Cancellable {
        override fun cancel() {
            scope.cancel()
        }
    }
}