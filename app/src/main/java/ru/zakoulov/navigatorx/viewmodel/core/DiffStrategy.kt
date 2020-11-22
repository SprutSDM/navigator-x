@file:Suppress("NOTHING_TO_INLINE")
package ru.zakoulov.navigatorx.viewmodel.core

/**
 * @return true if the values are different, false otherwise
 */
typealias DiffStrategy<T> = (T, T) -> Boolean

inline fun <T> byValue(): DiffStrategy<T> = { p1, p2 -> p2 != p1 }
inline fun <T> byRef(): DiffStrategy<T> = { p1, p2 -> p2 !== p1 }
