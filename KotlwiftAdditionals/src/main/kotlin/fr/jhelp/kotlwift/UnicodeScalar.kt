package fr.jhelp.kotlwift

class UnicodeScalar(scalar: Int)
{
    private val string = scalar.toChar().toString()

    override fun toString() = this.string
}