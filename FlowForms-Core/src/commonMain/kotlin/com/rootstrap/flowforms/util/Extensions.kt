package com.rootstrap.flowforms.util

/**
 * Executes the given function when the collection is not empty and the given condition
 * is met, using the collection as the scope
 */
inline fun <E> Collection<E>.whenNotEmptyAnd(condition : () -> Boolean, block : Collection<E>.() -> Unit ) {
    if (this.isNotEmpty() && condition()) {
        block()
    }
}
