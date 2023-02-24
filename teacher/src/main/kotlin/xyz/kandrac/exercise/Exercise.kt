package xyz.kandrac.exercise

import xyz.kandrac.exercise.base.*
import xyz.kandrac.exercise.base.StaticEvaluationResult
import xyz.kandrac.exercise.base.TestResult
import xyz.kandrac.exercise.base.TestStatus
import xyz.kandrac.exercise.base.tests
import xyz.kandrac.runWithTimeout

/**
 * Exercise is basic tool to communicate requirements from Game to Teacher or Game to Student directly
 *
 * Implement exercise, and you can run [staticEvaluation] to check code statically (if supported by Teacher).
 * If code passes `staticEvaluation`, [test] is meant to be executed [testCases] times in order
 * to test whether solution returns valid results.
 *
 * To fully evaluate validity of Teacher's / Student's solution call [test] function.
 */
internal abstract class Exercise {

    /**
     * How much time is allowed for a solution to run all [testCases] tests
     */
    protected abstract val timeoutMs: Long

    /**
     * How many times will [test] run (amount of testcases)
     */
    protected abstract val testCases: Int

    /**
     * Statically evaluate Solution - any validations related to code placement and content should be done here.
     * No client code should run without successfully passing this evaluation
     *
     * @see test
     */
    protected abstract suspend fun staticEvaluation(): StaticEvaluationResult

    /**
     * [staticEvaluation] failed because implementation is found, but has invalid content (attributes, functions etc.)
     */
    protected open fun onInvalidContent() { }

    /**
     * [staticEvaluation] failed because implementation is not found
     */
    protected open fun onNotImplemented() { }
    
    /**
     * If [staticEvaluation] is successful, solution evaluation can run. The solution evaluation runs code [testCases]
     * times. Index of given `testCase` is given as [iteration] parameter. Here you should validate, whether solution
     * was properly implemented in terms of expected results from client code.
     */
    protected abstract suspend fun test(iteration: Int): TestResult

    /**
     * [test] for each `testCase` should be executed in time not exceeding [timeoutMs]. If
     * evaluation takes longer than `timeoutMs` this function will be invoked (`test` not yet finishes)
     *
     * default implementation will mark [tests] for this test to [TestStatus.Running] with [TestTimeConstraint.Outside]
     */
    protected open fun onTestTimeOut() {
        testStatus = TestStatus.Running(TestTimeConstraint.Outside)
    }

    /**
     * [test] for each `testCase` should be executed in time not exceeding [timeoutMs]. If
     * evaluation finishes but takes longer than `timeoutMs` this function will be invoked
     *
     * default implementation will mark [tests] for this test to [TestStatus.Finished] with [TestTimeConstraint.Outside]
     */
    protected open fun onTestTooLate(testResult: TestResult) {
        testStatus = TestStatus.Finished(testResult, TestTimeConstraint.Outside)
    }

    /**
     * [test] finishes successfully and within its bounds
     *
     * default implementation will mark [tests] for this test to [TestStatus.Finished] with [TestTimeConstraint.Within]
     */
    protected open fun onTestInTime(testResult: TestResult) {
        testStatus = TestStatus.Finished(testResult, TestTimeConstraint.Within)
    }

    /**
     * Run [staticEvaluation]. If static evaluation passes, [test] will be invoked [testCases] times.
     */
    suspend fun test() {
        if (tests.value.containsKey(this)) {
            // Evaluation for this exercise is already running
            return
        }
        when (staticEvaluation()) {
            is StaticEvaluationResult.FailContent -> { onInvalidContent() }
            is StaticEvaluationResult.NotImplemented -> { onNotImplemented() }
            StaticEvaluationResult.Passed -> runTests()
        }
    }

    /**
     * Tests can be executed only after [staticEvaluation]
     *
     * [test] is called [testCases]-times. If this evaluation takes longer than
     * [timeoutMs] - [onTestTimeOut] is called. If the evaluation finishes, but
     * it takes longer than [timeoutMs] - [onTestTooLate] is called.
     *
     * Otherwise [onTestTimeOut]
     */
    private fun runTests() {
        runWithTimeout(
            timeoutMs = timeoutMs,
            onSuccess = ::onTestInTime,
            onTooLate = ::onTestTooLate,
            onTimeout = ::onTestTimeOut,
            task = suspend {
                testStatus = TestStatus.Running(TestTimeConstraint.Within)
                var result: TestResult = TestResult.Passed
                for (i in 0 until testCases) {
                    val eval = test(iteration = i)
                    if (eval is TestResult.Failed) {
                        result = eval
                        break
                    }
                }
                result
            },
        )
    }

    protected var testStatus : TestStatus = TestStatus.NotTested
        set(value) {
            field = value
            tests.tryEmit(tests.value + (this to value))
        }

}