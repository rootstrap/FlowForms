package com.rootstrap.flowforms.core.form

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCOMPLETE
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED

class FormStatus(
    val code: String
) {
    companion object {
        val Correct = FormStatus(CORRECT)
        val Unmodified = FormStatus(UNMODIFIED)
        val Incorrect = FormStatus(INCORRECT)
        val Incomplete = FormStatus(INCOMPLETE)
    }
}
