package fr.jhelp.kotlwift

infix fun <T> T?.guard(guard: () -> Unit) = this
                                            ?: guard()

// (age >= 0) guard { throw IllegalArgumentException("Age can't be negative") }
// =>
// guard age>=0 else { throw CommonManagedExceptions.IllegalArgumentException("Age can't be negative") }
infix fun Boolean.guard(guard: () -> Unit) =
    if (this)
    {
        true
    }
    else
    {
        guard()
        false
    }