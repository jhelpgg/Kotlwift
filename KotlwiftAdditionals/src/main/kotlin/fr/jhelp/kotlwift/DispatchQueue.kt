package fr.jhelp.kotlwift

import kotlinx.coroutines.*

// @ImportSwift("Dispatch")
// =>
// import Dispatch

class DispatchQueue internal constructor(val label: String) {
    companion object {
        private val dispatchQueueGlobal = DispatchQueue("global")
        fun global() = DispatchQueue.dispatchQueueGlobal
    }

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * Label and qos must be used explicitly
     *
     * ie:
     *
     * ```kotlin
     * var queue = DispatchQueue(label="memorypie", qos=DispatchQoS.background)
     * ```
     */
    constructor(label: String, qos: DispatchQoS) : this(label)

    // DispatchQueue.global().async { something() }
    // =>
    // DispatchQueue.global().async { something() }
    // ---
    // DispatchQueue.global().async(task)
    // =>
    // DispatchQueue.global().async(execute : task)
    fun async(execute: () -> Unit) {
        this.coroutineScope.launch {
            withContext(Dispatchers.Default)
            {
                execute.suspended()()
            }
        }
    }

    // DispatchQueue.global().asyncAfter(DispatchTime.now()+DispatchTimeInterval.milliseconds(128)) { something() }
    // =>
    // DispatchQueue.global().asyncAfter(deadline: DispatchTime.now()+DispatchTimeInterval.milliseconds(128)) { something() }
    // ---
    // DispatchQueue.global().asyncAfter(DispatchTime.now()+DispatchTimeInterval.milliseconds(128), task)
    // =>
    // DispatchQueue.global().asyncAfter(deadline: DispatchTime.now()+DispatchTimeInterval.milliseconds(128), execute : task)
    fun asyncAfter(deadline: DispatchTime, execute: DispatchWorkItem) {
        execute.job = this.coroutineScope.launch {
            delay(deadline.timeMilliseconds - System.currentTimeMillis())

            withContext(Dispatchers.Default)
            {
                if (!execute.isCancelled) {
                    execute.block()
                }
            }
        }
    }
}


internal fun (() -> Unit).suspended(): suspend () -> Unit = { this() }