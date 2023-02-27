package xyz.kandrac.exercise.base

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import xyz.kandrac.exercise.Exercise

/**
 * Tests that are running or finishes are stored here
 */
internal val tests by lazy { MutableStateFlow(mapOf<Exercise, TestStatus>()) }

/**
 * Observe [TestStatus]
 */
internal val Exercise.observeTestResult get() = tests.map { it[this] }

internal sealed interface TestResult {
    object Passed : TestResult
    data class Failed<T>(val reason: T): TestResult
}

enum class TestTimeConstraint { Within, Outside }

/**
 * Test can be running, or finished (state "before execution" doesn't exist yet). Both of
 * this states can be within time constraints or outside of them.
 */
internal sealed interface TestStatus {
    object NotTested : TestStatus
    data class Running(val timeConstraint: TestTimeConstraint) : TestStatus
    data class Finished(val result: TestResult, val timeConstraint: TestTimeConstraint) : TestStatus

    /**
     * Throw away this Exercise from list of running exercises
     */
    fun unsubscribe(exercise: Exercise) {
        tests.tryEmit(tests.value - exercise)
    }
}

internal sealed class StaticEvaluationResult {

    /**
     * Function, Class, Interface etc. not found
     */
    data class NotImplemented(val message: String) : StaticEvaluationResult()

    /**
     * Function, Class, Interface etc. doesn't have proper structure (parameters, attributes, ...)
     */
    data class FailContent(val message: String) : StaticEvaluationResult()

    /**
     * Validation successful
     */
    object Passed : StaticEvaluationResult()

}
