package com.rootstrap.flowforms.core.validation

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.REQUIRED_UNSATISFIED

/**
 * Validation to use when the given string should not be null nor empty. It uses the valueProvider
 * to receive the string at runtime when the validation fun is called.
 *
 * The validation fun returns [CORRECT] if the string is not null and is not empty.
 * Otherwise returns [REQUIRED_UNSATISFIED].
 *
 * @param failFast **Optional**, refer to failFast property on [Validation] class.
 * @param async **Optional**, refer to async property on [Validation] class.
 * @param valueProvider function that returns the value used by the [validate] implementation.
 */
class Required(
    failFast : Boolean = true,
    async : Boolean = false,
    val valueProvider: () -> String?
) : Validation(failFast = failFast, async = async) {

    @NativeCoroutines
    override suspend fun validate() = ValidationResult(
        if (valueProvider().isNullOrEmpty())
            REQUIRED_UNSATISFIED
        else
            CORRECT
    )
}
