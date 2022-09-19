package com.rootstrap.flowforms.core.dsl

import com.rootstrap.flowforms.core.form.FlowForm
import com.rootstrap.flowforms.core.dsl.builders.FlowFormBuilder

fun flowForm(init : FlowFormBuilder.() -> Unit) : FlowForm {
    val formBuilder = FlowFormBuilder()
    formBuilder.init()
    return formBuilder.build()
}
