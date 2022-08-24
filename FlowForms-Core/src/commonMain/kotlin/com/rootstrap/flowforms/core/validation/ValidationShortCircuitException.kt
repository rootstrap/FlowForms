package com.rootstrap.flowforms.core.validation

/**
 * Exception fired during validation processing to stop unnecessary and in-progress validation executions
 */
class ValidationShortCircuitException(val validationResult: ValidationResult) : Exception()
