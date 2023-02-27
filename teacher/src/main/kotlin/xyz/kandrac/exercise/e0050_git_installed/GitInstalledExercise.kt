package xyz.kandrac.exercise.e0050_git_installed

import xyz.kandrac.exercise.Exercise
import xyz.kandrac.exercise.base.StaticEvaluationResult
import xyz.kandrac.exercise.base.TestResult
import xyz.kandrac.isWindows

/**
 * Sample of Git related exercise. This one just checks whether Git is properly
 * installed on Student's machine
 */
internal object GitInstalledExercise : Exercise() {
    override val timeoutMs = 10L
    override val testCases = 1

    // No static code evaluation needed, since there is no code required to pass
    // this Exercise
    override suspend fun staticEvaluation() = StaticEvaluationResult.Passed

    override suspend fun test(iteration: Int): TestResult {
        return when {
            isWindows -> testWindows()
            else -> testUnix()
        }
    }

    /**
     * Let's assume user has allowed "Git from Command Prompt" while installing
     * Git tools. This way, common git commands are similar to those used on Unix
     * like machines
     */
    private fun testWindows(): TestResult = testUnix()

    private fun testUnix(): TestResult {
        val result = Runtime.getRuntime().exec(arrayOf("git", "--version"))
        val line = result.inputStream.reader().readLines().firstOrNull()

        return if (line?.contains("git version") == true) {
            TestResult.Passed
        } else {
            TestResult.Failed("")
        }
    }
}