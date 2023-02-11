package fr.jhelp.kotlwift

import kotlinx.coroutines.Job

class DispatchWorkItem(internal val block: () -> Unit)
{
    internal var job: Job? = null
    var isCancelled = this.job?.isCancelled
                      ?: false
        private set

    fun cancel()
    {
        this.job?.cancel()
    }
}