package fr.jhelp.kotlwift

import kotlin.math.pow

fun timeSince1970InMilliseconds(): Long =
    System.currentTimeMillis()

internal fun <T, T1> Iterator<T>.transform(transformation: (T) -> T1) =
    TransformIterator(this, transformation)

internal fun <T, T1> Iterable<T>.transform(transformation: (T) -> T1) =
    TransformIterable(this, transformation)

@Throws(RuntimeException::class)
fun fatalError(message: String) {
    throw RuntimeException("Fatal Error : $message")
}

fun <T> fatal(message: String): T {
    throw RuntimeException("Fatal Error : $message")
}

fun power(value: Double, exponent: Int): Double =
    value.pow(exponent)

fun power(value: Float, exponent: Int): Float =
    value.pow(exponent)

fun createDispatchQueue(name: String): DispatchQueue = DispatchQueue(name)

/** For free memory of "NS" hiding things in IOS like file management ... */
inline fun <R> autoreleasepool(block: () -> R): R = block()
