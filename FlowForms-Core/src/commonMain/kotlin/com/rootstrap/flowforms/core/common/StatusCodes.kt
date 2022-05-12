package com.rootstrap.flowforms.core.common

object StatusCodes {

    /**
     * Default status for fields.
     */
    const val UNMODIFIED = "unmodified"

    /**
     * Status for successful validations and correct fields.
     */
    const val CORRECT = "correct"

    // out of the box validation result status codes

    /**
     * Represents an incorrect field with a not well defined error code.
     *
     * Being it directly not defined or that more than one validation failed.
     */
    const val INCORRECT = "incorrect"

    /**
     * Represents a required validation failure.
     */
    const val REQUIRED_UNSATISFIED = "required_unsatisfied"
}
