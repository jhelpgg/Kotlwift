package fr.jhelp.kotlwift

internal class TransformIterable<T, T1>(private val iterable: Iterable<T>, private val transform: (T) -> T1) : Iterable<T1>
{
    override fun iterator(): Iterator<T1> =
        this.iterable.iterator().transform(this.transform)
}