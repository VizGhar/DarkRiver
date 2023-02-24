package xyz.kandrac.exercise.e0004_long_run

import xyz.kandrac.exercise.base.AsynchronousExercise
import xyz.kandrac.exercise.base.StaticEvaluationResult
import xyz.kandrac.exercise.base.TestResult

internal object LongRunExercise : AsynchronousExercise<Boolean>() {

    override suspend fun call(vararg attrs: Any): Boolean {
        // force to overdo timeout without falling into coroutine timeout
        for (i in 0 .. 4) { Thread.sleep(1000) }
        return true
    }

    override val executionTimeoutMs: Long = 2000

    override val timeoutMs = 2000L

    override val testCases = 1

    override suspend fun staticEvaluation(): StaticEvaluationResult {
        return StaticEvaluationResult.Passed
    }

    override suspend fun test(iteration: Int): TestResult {
        return TestResult.Passed
    }
}