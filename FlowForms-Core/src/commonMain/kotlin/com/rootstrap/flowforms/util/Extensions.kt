package com.rootstrap.flowforms.util

/**
 * Executes the given function when the list is not empty sending itself as a parameter
 */
inline fun <E> Collection<E>.whenNotEmpty(block : Collection<E>.() -> Unit ) {
    if (this.isNotEmpty()) {
        block()
    }
}
