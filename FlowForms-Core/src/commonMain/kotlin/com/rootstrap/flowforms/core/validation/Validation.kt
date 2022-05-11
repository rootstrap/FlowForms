package com.rootstrap.flowforms.core.validation

abstract class Validation(val failFast : Boolean = true) {
    abstract fun validate() : ValidationResult
}

data class ValidationResult(
    val resultId : String,
    val extras : Map<String, Any> = mutableMapOf()
)
