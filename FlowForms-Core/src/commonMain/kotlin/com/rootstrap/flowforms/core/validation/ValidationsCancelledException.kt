package com.rootstrap.flowforms.core.validation

import kotlinx.coroutines.CancellationException

/**
 * Exception fired during validation processing when the validations were triggered again
 */
class ValidationsCancelledException(message: String) : CancellationException(message)
