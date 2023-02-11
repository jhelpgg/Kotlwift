package fr.jhelp.kotlwift

import java.util.concurrent.Semaphore

class Mutex
{
    private val semaphore = Semaphore(1)

    fun safeExecute(task: () -> Unit)
    {
        this.semaphore.acquire()

        try
        {
            task()
        }
        finally
        {
            this.semaphore.release()
        }
    }

    fun safeExecuteMayThrows(task: () -> Unit)
    {
        this.semaphore.acquire()

        try
        {
            task()
        }
        finally
        {
            this.semaphore.release()
        }
    }
}