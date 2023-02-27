package xyz.kandrac.exercise.e0003_sound_effects

import xyz.kandrac.exercise.base.SynchronousMethodExercise
import xyz.kandrac.exercise.base.TestResult
import java.io.File

enum class SoundEffect {
    KNOCK
}

/**
 * Toto cvicenie ma za ulohu od studenta vyziadat mapovanie pre zvuky. Student musi implementovat funkciu
 *
 * fun getSoundEffectFile(effect: SoundEffect) : File
 *
 * Vystupom tejto funkcie bude akykolvek existujuci subor, ten sa pokusime spustit na pozadi
 */
internal object SoundEffectsMethodExercise : SynchronousMethodExercise<File>() {

    override val methodName = "getSoundEffectFile"
    override val returnType = File::class.java
    override val expectedArguments = arrayOf<Class<*>>(SoundEffect::class.java)
    override val timeoutMs: Long = 10L
    override val testCases: Int = SoundEffect.values().size

    override suspend fun test(iteration: Int): TestResult {
        return if (method?.invoke(null, SoundEffect.values()[iteration]) == null) {
            TestResult.Failed("")
        } else {
            TestResult.Passed
        }
    }
}
