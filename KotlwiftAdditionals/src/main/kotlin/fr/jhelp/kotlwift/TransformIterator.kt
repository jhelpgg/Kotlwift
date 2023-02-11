package fr.jhelp.kotlwift

internal class TransformIterator<T, T1>(private val iterator: Iterator<T>, private val transform: (T) -> T1) : Iterator<T1>
{
    override fun hasNext(): Boolean =
        this.iterator.hasNext()

    override fun next(): T1 =
        this.transform(this.iterator.next())
}