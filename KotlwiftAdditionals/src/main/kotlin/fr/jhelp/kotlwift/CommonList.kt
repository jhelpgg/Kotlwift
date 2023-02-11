package fr.jhelp.kotlwift

// var list = CommonList<String>()
// =>
// var list = Array<String>()
class CommonList<T> : Iterable<T>
{
    private val list = ArrayList<T>()
    val count get() = this.list.size
    val isEmpty get() = this.list.isEmpty()

    fun append(element: T)
    {
        this.list += element
    }

    // list.insert(element, 5)
    // =>
    // list.insert(element, at: 5)
    fun insert(element: T, at: Int) = this.list.add(at, element)

    // list.remove(index)
    // =>
    // list.remove(at: index)
    fun remove(at: Int) = this.list.removeAt(at)

    /**
     * Use [] notation
     */
    operator fun get(index: Int) = this.list[index]

    /**
     * Use [] notation
     */
    operator fun set(index: Int, element: T)
    {
        this.list[index] = element
    }

    fun contains(element: T) = element in this.list

    fun removeAll() = this.list.clear()

    fun enumerated() =
        this.list.withIndex()

    /**
     * Use in notation
     */
    override fun iterator(): Iterator<T> =
        this.list.iterator()
}

operator fun <T> CommonList<T>.plus(list: CommonList<T>): CommonList<T>
{
    val result = CommonList<T>()

    for (element in this)
    {
        result.append(element)
    }

    for (element in list)
    {
        result.append(element)
    }

    return result
}