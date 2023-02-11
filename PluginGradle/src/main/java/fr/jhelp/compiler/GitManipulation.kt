package fr.jhelp.compiler

import fr.jhelp.compiler.command.CommandFactory
import fr.jhelp.compiler.command.CommandManager
import fr.jhelp.compiler.command.LogVerbosity
import java.io.File
import kotlin.system.exitProcess

/**
 * Clone a **Git** repository
 * @param directoryCloned Directory where leads cloned repositories
 * @param gitRepo **Git** repository to clone
 * @param directoryName Directory name where write cloned files
 */
fun gitClone(directoryCloned: File, gitRepo: String, directoryName: String)
{
    CommandManager.launchCommands(arrayOf("git clone $gitRepo $directoryName ${CommandFactory.commandOS.redirectionFooter}"),
                                  directoryCloned,
                                  LogVerbosity.NONE)
}

/**
 * Check if current branch is the target one.
 *
 * If not, it indicates where the  target branch, local or distant
 *
 * It fails if branch not exists locally nor distant
 *
 * @param directoryClone Directory where lead the git to check
 * @param branch Target branch
 * @return Where the target branch
 */
fun checkBranch(directoryClone: File, branch: String): CheckBranchStatus
{
    var currentBranch = ""
    val localBranches = ArrayList<String>()
    val distantBranches = ArrayList<String>()
    var fetchDone = false

    CommandManager.launchCommands(arrayOf(
        "git fetch -a ${CommandFactory.commandOS.redirectionFooter}",
        "echo \"$FETCH_DONE\"",
        "git branch -a ${CommandFactory.commandOS.redirectionFooter}"),
                                  directoryClone,
                                  LogVerbosity.NONE) { line ->
        val trimmed = line.trim()

        if (!fetchDone)
        {
            fetchDone = trimmed == FETCH_DONE
        }

        if (fetchDone)
        {
            when
            {
                trimmed[0] == '*'                         ->
                {
                    currentBranch = trimmed.substring(1).trim()
                    localBranches += currentBranch
                }
                trimmed.startsWith(REMOTES_ORIGIN_BRANCH) ->
                    distantBranches += trimmed.substring(REMOTES_ORIGIN_BRANCH_LENGTH)
                else                                      ->
                    localBranches += trimmed
            }
        }
    }

    return when
    {
        currentBranch == branch          ->
            CheckBranchStatus.ON_GOOD_BRANCH
        localBranches.contains(branch)   ->
            CheckBranchStatus.LOCAL_BUT_NOT_GOOD_BRANCH
        distantBranches.contains(branch) ->
            CheckBranchStatus.ONLY_DISTANT
        else                             ->
        {
            System.err.println("No local on distant branch $branch in ${directoryClone.name}")
            exitProcess(-1)
        }
    }
}

/**
 * Do necessary checkout to reach specified branch target
 *
 * And update the target branch
 * @param directoryClone Directory where lead the git to update
 * @param branch Branch to set and update
 */
fun checkoutUpdateBranch(directoryClone: File, branch: String)
{
    when (checkBranch(directoryClone, branch))
    {
        CheckBranchStatus.ON_GOOD_BRANCH ->
            gitPull(directoryClone, branch)
        CheckBranchStatus.LOCAL_BUT_NOT_GOOD_BRANCH ->
            checkoutPull(directoryClone, branch, "")
        CheckBranchStatus.ONLY_DISTANT ->
            checkoutPull(directoryClone, branch, " -b ")
    }
}

/**
 * Checkout to a specific branch, then update it
 * @param directoryClone Directory where lead the git to update
 * @param checkoutOption Option gives to checkout
 *  @param branch Branch to set and update
 */
fun checkoutPull(directoryClone: File, branch: String, checkoutOption: String)
{
    CommandManager.launchCommands(arrayOf("git checkout $checkoutOption $branch ${CommandFactory.commandOS.redirectionFooter}"),
                                  directoryClone,
                                  LogVerbosity.FULL)

    gitPull(directoryClone, branch)
}

/**
 * Pull to update a specific branch
 * @param directoryClone Directory where lead the git to update
 * @param branch Branch to update
 */
fun gitPull(directoryClone: File, branch: String)
{
    CommandManager.launchCommands(arrayOf("git pull origin $branch ${CommandFactory.commandOS.redirectionFooter}"),
                                  directoryClone,
                                  LogVerbosity.NONE)
}


