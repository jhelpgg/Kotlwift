package fr.jhelp.compiler.command.linux

import fr.jhelp.compiler.command.CommandOS

object LinuxCommandOS : CommandOS
{
    override val shellCommand: String
        get() = "sh"

    override val redirectionFooter: String
        get() = " 2>&1"

    override val executableExtension: String
        get() = ""

    override fun makeExecutable(path: String): String =
        "chmod 777 $path 2>&1"
}