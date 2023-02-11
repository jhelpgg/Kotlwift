package fr.jhelp.compiler.command

const val END_MESSAGE = "-<END>-"

interface CommandOS
{
    val shellCommand: String

    val redirectionFooter : String

    val executableExtension : String

    fun makeExecutable(path:String) : String
}
