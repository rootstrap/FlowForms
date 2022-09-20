package com.rootstrap.flowforms.core.dsl.builders

import com.rootstrap.flowforms.core.dsl.FlowFormsDslMarker
import com.rootstrap.flowforms.core.field.DefaultFieldValidationBehavior
import com.rootstrap.flowforms.core.field.FieldValidationBehavior
import com.rootstrap.flowforms.core.field.FlowField
import com.rootstrap.flowforms.core.validation.Validation

/**
 * Builder to create [FlowField]s, intended primarily for DSL. To learn how to use it with DSL
 * please refer to the [FlowFormBuilder] DSL public functions, for example : [FlowFormBuilder.field].
 */
@FlowFormsDslMarker
class FlowFieldBuilder {

    private var onValueChangeValidations = listOf<Validation>()
    private var onFocusValidations = listOf<Validation>()
    private var onBlurValidations = listOf<Validation>()

    /**
     * field validation behavior used by the [FlowField] to trigger its [Validation]s.
     * By default it is [DefaultFieldValidationBehavior]
     */
    var validationBehavior: FieldValidationBehavior = DefaultFieldValidationBehavior()

    /**
     * Defines the list of [Validation] to trigger when the [FlowField]'s value changes.
     *
     * @param validations a vararg of [Validation] implementations
     */
    fun onValueChange(vararg validations : Validation) {
        this.onValueChangeValidations = validations.toList()
    }

    /**
     * Defines the list of [Validation] to trigger when the [FlowField] gets focus.
     *
     * @param validations a vararg of [Validation] implementations
     */
    fun onFocus(vararg validations : Validation) {
        this.onFocusValidations = validations.toList()
    }

    /**
     * Defines the list of [Validation] to trigger when the [FlowField] loses the focus.
     *
     * @param validations a vararg of [Validation] implementations
     */
    fun onBlur(vararg validations : Validation) {
        this.onBlurValidations = validations.toList()
    }

    /**
     * Builds a new [FlowField] with the given ID and the attributes specified in this builder.
     *
     * @param id [FlowField]'s unique id.
     */
    fun build(id: String) : FlowField {
        return FlowField(
            id = id,
            onValueChangeValidations = onValueChangeValidations,
            onFocusValidations = onFocusValidations,
            onBlurValidations = onBlurValidations,
            validationBehavior = validationBehavior
        )
    }

}
