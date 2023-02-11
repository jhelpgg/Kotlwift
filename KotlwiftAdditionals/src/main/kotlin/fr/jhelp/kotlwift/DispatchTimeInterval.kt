package fr.jhelp.kotlwift

class DispatchTimeInterval private constructor(val timeInterval: Int = 0)
{
    companion object
    {
        fun seconds(seconds: Int) = DispatchTimeInterval(seconds * 1000)
        fun milliseconds(milliseconds: Int) = DispatchTimeInterval(milliseconds)
    }
}