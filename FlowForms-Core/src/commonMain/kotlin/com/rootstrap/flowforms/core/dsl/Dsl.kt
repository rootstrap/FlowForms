package com.rootstrap.flowforms.core.dsl

import com.rootstrap.flowforms.core.dsl.builders.FlowFormBuilder
import com.rootstrap.flowforms.core.form.FlowForm

/**
 * Simply create a [FlowForm] using [FlowForm]'s DSL.
 *
 * @param init builder function to define the [FlowForm]'s properties. Refer to [FlowFormBuilder]'s
 * public methods and variables for customization options
 */
fun flowForm(init : FlowFormBuilder.() -> Unit) : FlowForm {
    val formBuilder = FlowFormBuilder()
    formBuilder.init()
    return formBuilder.build()
}
