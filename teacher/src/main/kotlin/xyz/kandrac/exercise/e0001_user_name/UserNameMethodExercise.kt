package xyz.kandrac.exercise.e0001_user_name

import xyz.kandrac.exercise.base.SynchronousMethodExercise
import xyz.kandrac.exercise.base.TestResult

/**
 * Toto jednoduche cvicenie ma za ulohu od studenta vyziadat jeho pouzivatelske meno. Student musi implementovat funkciu
 *
 * fun getUserName() : String
 *
 * Vystupom tejto funkcie bude akykolvek text, ktory sa nasledne bude pouzivat na oslovenie Studenta (jeho postavicky).
 */
internal object UserNameMethodExercise : SynchronousMethodExercise<String>() {

    override val timeoutMs = 10L
    override val testCases = 1
    override val methodName = "getUserName"
    override val returnType = String::class.java

    override suspend fun test(iteration: Int): TestResult {
        return if (method?.invoke(null) == null) {
            TestResult.Failed("")
        } else {
            TestResult.Passed
        }
    }
}
