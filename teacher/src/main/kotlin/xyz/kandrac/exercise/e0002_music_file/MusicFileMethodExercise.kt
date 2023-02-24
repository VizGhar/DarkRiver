package xyz.kandrac.exercise.e0002_music_file

import xyz.kandrac.exercise.base.SynchronousMethodExercise
import xyz.kandrac.exercise.base.TestResult
import java.io.File

/**
 * Toto cvicenie ma za ulohu od studenta vyziadat muziku, ktoru ma hrat na pozadi. Student musi implementovat funkciu
 *
 * fun getBackgroundMusic() : File
 *
 * Vystupom tejto funkcie bude akykolvek existujuci subor, ten sa pokusime spustit na pozadi
 */
internal object MusicFileMethodExercise : SynchronousMethodExercise<File>() {

    override val timeoutMs = 10L
    override val testCases = 1
    override val methodName = "getBackgroundMusic"
    override val returnType = File::class.java

    override suspend fun test(iteration: Int): TestResult {
        return if (method?.invoke(null) == null) {
            TestResult.Failed
        } else {
            TestResult.Passed
        }
    }
}
