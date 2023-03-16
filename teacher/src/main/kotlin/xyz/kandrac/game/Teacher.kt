package xyz.kandrac.game

import com.badlogic.gdx.Graphics
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

internal object Teacher {

    private val game = TeacherGame()

    internal fun start() {
        val config = Lwjgl3ApplicationConfiguration()
        config.setForegroundFPS(60)
        config.setTitle(configuration.screenTitle)
        config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 0, 0)
        val dm: Graphics.DisplayMode = Lwjgl3ApplicationConfiguration.getDisplayMode()
        config.setWindowedMode(dm.width / 2, dm.height / 2)
        config.setResizable(false)
        Lwjgl3Application(game, config)
    }
}

internal lateinit var configuration: Config

fun go(config: Config) {
    configuration = config
    Teacher.start()
}

enum class Language {
    KOTLIN
}

data class Config(
    val packageName: String,
    val screenTitle: String = "Student",
    val language: Language = Language.KOTLIN
)