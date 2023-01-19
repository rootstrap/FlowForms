package com.rootstrap.flowforms.core.validation

class CrossFieldValidation(
    val validation : Validation,
    val targetFieldId: String
) : Validation() {

    override val failFast = validation.failFast
    override val async = validation.async
    override suspend fun validate() = validation.validate()

}
