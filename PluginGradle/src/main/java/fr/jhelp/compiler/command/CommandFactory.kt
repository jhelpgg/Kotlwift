package fr.jhelp.compiler.command

import fr.jhelp.compiler.command.linux.LinuxCommandOS
import fr.jhelp.compiler.command.mac.MacCommandOS
import fr.jhelp.compiler.command.windows.WindowsCommandOS

object CommandFactory
{
    val commandOS: CommandOS

    init
    {
        val operatingSystem = System.getProperty("os.name")

        this.commandOS =
            when
            {
                operatingSystem.contains("Windows", true) -> WindowsCommandOS
                operatingSystem.contains("Linux", true)   -> LinuxCommandOS
                operatingSystem.contains("Mac", true)     -> MacCommandOS
                else                                      -> LinuxCommandOS
            }
    }
}
