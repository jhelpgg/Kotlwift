package fr.jhelp.kotlwift

/**
 * To have a `Thread` class tht use `main` as running method like swift
 *
 * Need to import `Foundation` for swift
 */
abstract class Thread : java.lang.Thread() {
    final override fun run() {
        this.main()
    }

    abstract fun main()
}