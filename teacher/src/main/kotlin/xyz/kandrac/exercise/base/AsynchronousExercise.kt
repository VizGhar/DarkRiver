package xyz.kandrac.exercise.base

import kotlinx.coroutines.flow.MutableStateFlow
import xyz.kandrac.exercise.Exercise
import xyz.kandrac.runWithTimeout

/**
 * Active asynchronous exercises that didn't finish in required time
 */
internal val activeAsyncExercises by lazy { MutableStateFlow(emptyList<AsynchronousExercise<*>>()) }

/**
 * All exercise results will be stored here including those, that finishes too late
 *
 * @see AsyncExerciseResult
 */
internal val asyncExerciseResults by lazy { MutableStateFlow(emptyList<AsyncExerciseResult<*>>()) }

/**
 * Result of asynchronous exercise stores the exercise, its result and whether it was finished on time
 */
internal data class AsyncExerciseResult<T>(val exercise: AsynchronousExercise<T>, val result: T, val inTime: Boolean) {

    /**
     * Call when the result is processed, or when you don't need it anymore
     */
    fun process() { asyncExerciseResults.tryEmit(asyncExerciseResults.value - this) }

}

/**
 * Long-running exercise (anything longer than 10ms is too long because of risk of dropping frames). The UI won't wait
 * for its result.
 *
 * Examples - solve labyrinth, fetch data from file/internet/database etc.
 */
internal abstract class AsynchronousExercise<T> : Exercise() {

    @Suppress("LeakingThis")
    abstract val executionTimeoutMs: Long

    /**
     * Enqueue Exercise. Method [call] will be called with provided [attrs].
     * If method successfully finish within [executionTimeoutMs] the [onExecutionSuccess] will be invoked.
     *
     * This is not meant to replace [test] function. This should really return
     * result to the Game
     *
     * @see onExecutionTooLate
     * @see onExecutionTimeout
     */
    fun enqueue(vararg attrs: Any) {
        runWithTimeout(
            timeoutMs = executionTimeoutMs,
            onSuccess = ::onExecutionSuccess,
            onTooLate = ::onExecutionTooLate,
            onTimeout = ::onExecutionTimeout,
            task = suspend { call(attrs) }
        )
    }

    /**
     * This method is invoked when `AsynchronousExercise` finishes successfully and within [executionTimeoutMs]
     *
     * @param result of execution process
     */
    protected open fun onExecutionSuccess(result: T) {
        asyncExerciseResults.tryEmit(asyncExerciseResults.value + AsyncExerciseResult(this, result, true))
    }

    /**
     * This method is invoked when `AsynchronousExercise` finishes, but after [executionTimeoutMs] passes
     *
     * @param result of execution process
     */
    protected open fun onExecutionTooLate(result: T) {
        activeAsyncExercises.tryEmit(activeAsyncExercises.value - this@AsynchronousExercise)
        asyncExerciseResults.tryEmit(asyncExerciseResults.value + AsyncExerciseResult(this, result, false))
    }

    /**
     * This method is invoked when `AsynchronousExercise` is not yet finished and [executionTimeoutMs] already passed
     */
    protected open fun onExecutionTimeout() {
        activeAsyncExercises.tryEmit(activeAsyncExercises.value + this@AsynchronousExercise)
    }

    /**
     * Calls asynchronous exercise code
     */
    protected abstract suspend fun call(vararg attrs: Any): T

}