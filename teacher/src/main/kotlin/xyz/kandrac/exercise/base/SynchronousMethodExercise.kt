package xyz.kandrac.exercise.base

import xyz.kandrac.exercise.Exercise
import xyz.kandrac.reflection.getMethod
import java.lang.reflect.Method

/**
 * This kind of Synchronous exercise will try to execute specified [call] function synchronously. The [call]
 * function is supposed to execute method with name of [methodName] with [returnType] and [expectedArguments]
 *
 * Examples - get sound effect (ideally prefetched), get username, or facing direction for immediate processing
 */
internal abstract class SynchronousMethodExercise<T> : Exercise() {

    abstract val methodName: String
    abstract val returnType: Class<T>
    open val expectedArguments: Array<Class<*>> = emptyArray()

    var method : Method? = null

    override suspend fun staticEvaluation(): StaticEvaluationResult {
        method = getMethod(methodName, returnType, *expectedArguments) ?: return StaticEvaluationResult.NotImplemented("")
        return StaticEvaluationResult.Passed
    }

    /**
     * If this Exercise passes all tests, the [method] will be executed
     */
    @Suppress("UNCHECKED_CAST")
    open fun call(vararg attrs: Any): T? =
        if ((testStatus as? TestStatus.Finished)?.result == TestResult.Passed)
            (method?.invoke(null, *attrs) as T?)
        else
            null

}