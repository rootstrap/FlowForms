package com.rootstrap.flowforms.core.form

import com.rootstrap.flowforms.core.field.FlowField
import com.rootstrap.flowforms.core.field.FlowFieldBuilder
import com.rootstrap.flowforms.core.validation.Validation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow

class FlowFormBuilder internal constructor() {

    /**
     * fields mapped by their ID, used to create the final Form implementation.
     */
    private val fieldsMap = HashMap<String, FlowField>()

    /**
     * CoroutineDispatcher that will be used when triggering the [FlowField] validations,
     * by default it is required and used to run asynchronous validations in the
     * [DefaultFieldValidationBehavior][com.rootstrap.flowforms.core.field.DefaultFieldValidationBehavior].
     */
    var dispatcher : CoroutineDispatcher? = null

    /**
     * Builds the form using this builder's properties.
     */
    fun build() : FlowForm {
        return FlowForm(
            MutableStateFlow(fieldsMap),
            dispatcher
        )
    }

    /**
     * Add fields to the FlowForm being built
     */
    fun fields(vararg fields : FlowField) {
        fieldsMap.putAll(fields.associateBy { it.id })
    }

    /**
     * Add a field to the FlowForm being built, if a field with the same ID already exists
     * it will be replaced.
     */
    fun field(id: String, init: FlowFieldBuilder.() -> Unit) {
        val builder = FlowFieldBuilder()
        builder.init()
        fieldsMap[id] = builder.build(id)
    }

    /**
     * Add a field to the FlowForm being built, if a field with the same ID already exists
     * it will be replaced.
     * Additionally assigns the given validations to the onValueChange validations of the field
     */
    fun field(id: String, vararg validations : Validation, init: (FlowFieldBuilder.() -> Unit)? = null) {
        val builder = FlowFieldBuilder()
        if (init != null) {
            builder.init()
        }
        builder.onValueChange(*validations)
        fieldsMap[id] = builder.build(id)
    }

}
