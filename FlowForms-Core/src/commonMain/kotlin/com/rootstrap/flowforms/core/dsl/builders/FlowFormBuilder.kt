package com.rootstrap.flowforms.core.dsl.builders

import com.rootstrap.flowforms.core.dsl.FlowFormsDslMarker
import com.rootstrap.flowforms.core.field.DefaultFieldValidationBehavior
import com.rootstrap.flowforms.core.field.FieldDefinition
import com.rootstrap.flowforms.core.field.FlowField
import com.rootstrap.flowforms.core.form.FlowForm
import com.rootstrap.flowforms.core.validation.Validation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Builder to create FlowForms, intended primarily for DSL. To learn how to use it with DSL
 * please refer to the [flowForm][com.rootstrap.flowforms.core.dsl.flowForm] DSL function.
 */
@FlowFormsDslMarker
class FlowFormBuilder {

    /**
     * fields mapped by their ID, used to create the final Form implementation.
     */
    private val fieldsMap = HashMap<String, FieldDefinition>()

    /**
     * CoroutineDispatcher that will be used when triggering the [FlowField] validations,
     * by default it is required and used to run asynchronous validations in the
     * [DefaultFieldValidationBehavior].
     */
    var dispatcher : CoroutineDispatcher? = null

    /**
     * Builds a [FlowForm] using this builder properties.
     */
    fun build() : FlowForm {
        return FlowForm(
            MutableStateFlow(fieldsMap),
            dispatcher
        )
    }

    /**
     * Add fields to the FlowForm being built.
     */
    fun fields(vararg fields : FieldDefinition) {
        fieldsMap.putAll(fields.associateBy { it.id })
    }

    /**
     * Add a [FlowField] to the [FlowForm] being built, if a field with the same ID already exists
     * it will be replaced.
     *
     * @param id unique ID for this [FlowField].
     * @param init builder function to customize the field's behavior.
     */
    fun field(id: String, init: FlowFieldBuilder.() -> Unit) {
        val builder = FlowFieldBuilder()
        builder.init()
        fieldsMap[id] = builder.build(id)
    }

    /**
     * Add a field to the FlowForm being built, if a field with the same ID already exists
     * it will be replaced.
     * Additionally assigns the given validations to the onValueChange validations of the field.
     *
     * @param id unique ID for this [FlowField].
     * @param validations onValueChange validations to attach to this FlowField.
     * @param init builder function to customize the field's behavior.
     */
    fun field(id: String, vararg validations: Validation, init: (FlowFieldBuilder.() -> Unit)? = null) {
        val builder = FlowFieldBuilder()
        if (init != null) {
            builder.init()
        }
        builder.onValueChange(*validations)
        fieldsMap[id] = builder.build(id)
    }

}
