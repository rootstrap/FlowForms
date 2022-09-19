package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.validation.Validation

class FlowFieldBuilder {

    private var onValueChangeValidations = listOf<Validation>()
    private var onFocusValidations = listOf<Validation>()
    private var onBlurValidations = listOf<Validation>()
    var validationBehavior: FieldValidationBehavior = DefaultFieldValidationBehavior()

    fun onValueChange(vararg validations : Validation) {
        this.onValueChangeValidations = validations.toList()
    }

    fun onFocus(vararg validations : Validation) {
        this.onFocusValidations = validations.toList()
    }

    fun onBlur(vararg validations : Validation) {
        this.onBlurValidations = validations.toList()
    }

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
