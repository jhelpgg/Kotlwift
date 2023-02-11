package fr.jhelp.kotlwift

enum class DispatchQoS {
    /**
     * The quality-of-service class for user-interactive tasks, such as animations, event handling, or updates to your app's user interface.
     */
    userInteractive,

    /**
     * The quality-of-service class for tasks that prevent the user from actively using your app.
     */
    userInitiated,

    /**
     * The quality-of-service class for tasks that the user does not track actively.
     */
    utility,

    /**
     * The quality-of-service class for maintenance or cleanup tasks that you create.
     */
    background
}