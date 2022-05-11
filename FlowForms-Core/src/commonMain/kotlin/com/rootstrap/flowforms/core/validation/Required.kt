package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.REQUIRED_UNSATISFIED

class Required(val valueProvider : () -> String?) : Validation() {

    override fun validate() = ValidationResult(
        if (valueProvider().isNullOrEmpty())
            REQUIRED_UNSATISFIED
        else
            CORRECT
    )
}
