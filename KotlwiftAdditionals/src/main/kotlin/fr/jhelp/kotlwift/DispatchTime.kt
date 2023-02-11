package fr.jhelp.kotlwift

class DispatchTime private constructor(val timeMilliseconds: Long = System.currentTimeMillis())
{
    companion object
    {
        fun now() = DispatchTime()
    }

    operator fun plus(dispatchTimeInterval: DispatchTimeInterval) =
        DispatchTime(this.timeMilliseconds + dispatchTimeInterval.timeInterval)
}