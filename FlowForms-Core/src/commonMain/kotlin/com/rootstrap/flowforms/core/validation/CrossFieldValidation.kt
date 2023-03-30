package com.rootstrap.flowforms.core.validation

/**
 * Validation decorator that defines the given validation should affect a different field rather
 * than the one which triggered this validation. The target field is specified via
 * the targetFieldId parameter.
 *
 * If used _"as is"_ will work in the same way as the specified validation.
 */
class CrossFieldValidation(
    val validation : Validation,
    val targetFieldId: String
) : Validation() {

    override val failFast = validation.failFast
    override val async = validation.async
    override suspend fun validate() = validation.validate()

}
