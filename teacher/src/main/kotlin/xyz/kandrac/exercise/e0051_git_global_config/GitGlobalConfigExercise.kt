package xyz.kandrac.exercise.e0051_git_global_config

import xyz.kandrac.exercise.Exercise
import xyz.kandrac.exercise.base.StaticEvaluationResult
import xyz.kandrac.exercise.base.TestResult
import xyz.kandrac.isMailValid
import xyz.kandrac.isWindows

/**
 * Sample of Git related exercise. This one just checks whether Git user.name and user.email
 * are "properly" set in global settings
 */
internal object GitGlobalConfigExercise : Exercise() {

    override val timeoutMs = 30L
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
        val userName = Runtime.getRuntime().exec(arrayOf("git", "config", "--global", "user.name")).inputStream.reader().readLines().firstOrNull()
        val userEmail = Runtime.getRuntime().exec(arrayOf("git", "config", "--global", "user.email")).inputStream.reader().readLines().firstOrNull()

        return when {
            userName.isNullOrBlank() -> TestResult.Failed("reason")
            userEmail.isNullOrBlank() -> TestResult.Failed("reason")
            !isMailValid(userEmail) -> TestResult.Failed("reason")
            else -> TestResult.Passed.also {
                System.err.println("GitGlobalConfigExercise success -> \"$userName\" / \"$userEmail\"")
            }
        }
    }
}