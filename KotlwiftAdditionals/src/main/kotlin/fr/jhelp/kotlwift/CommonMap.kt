package fr.jhelp.kotlwift

// Translation in swift:
// var map = CommonMap<String, Int>()
// =>
// var map = Dictionary<String, Int>()
class CommonMap<K, V> : Iterable<Pair<K, V>>
{
    private val map = HashMap<K, V>()
    val count get() = this.map.size
    val isEmpty get() = this.map.isEmpty()
    val keys: Iterable<K> get() = this.map.keys
    val values: Iterable<V> get() = this.map.values

    /**
     * Put at null remove the key
     */
    operator fun set(key: K, value: V?)
    {
        if (value == null)
        {
            this.map.remove(key)
            return
        }

        this.map[key] = value
    }

    operator fun get(key: K): V? =
        this.map[key]

    /*
    // Translation in swift
    // map.clear()
    // =>
    // map = [:]
    fun clear()
    {
        this.map.clear()
    }
    */

    override fun iterator(): Iterator<Pair<K, V>> =
        this.map.entries.iterator().transform { entry -> Pair(entry.key, entry.value) }
}

