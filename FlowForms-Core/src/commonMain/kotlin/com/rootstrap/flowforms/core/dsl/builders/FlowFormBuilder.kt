package com.rootstrap.flowforms.core.dsl.builders

import com.rootstrap.flowforms.core.field.FlowField
import com.rootstrap.flowforms.core.form.FlowForm
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow

class FlowFormBuilder internal constructor() {

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

}
