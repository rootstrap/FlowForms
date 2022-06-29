package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.REQUIRED_UNSATISFIED

/**
 * Required value validation to use with nullable strings. It uses the valueProvider to receive
 * a value at runtime when the validation fun is called.
 *
 * The validation fun returns [CORRECT] if the string is not null and is not empty.
 * Otherwise returns [REQUIRED_UNSATISFIED]
 *
 * @property valueProvider function that returns the value used by the [validate] implementation
 */
class Required(val valueProvider : () -> String?) : Validation() {

    override suspend fun validate() = ValidationResult(
        if (valueProvider().isNullOrEmpty())
            REQUIRED_UNSATISFIED
        else
            CORRECT
    )
}
