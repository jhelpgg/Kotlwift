package fr.jhelp.compiler.command.windows

import fr.jhelp.compiler.command.CommandOS

object WindowsCommandOS : CommandOS
{
    override val shellCommand: String
        get() = "cmd"

    override val redirectionFooter: String
        get() = ""

    override val executableExtension: String
        get() = ".bat"

    override  fun makeExecutable(path:String) : String = ""
}