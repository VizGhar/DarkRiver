package xyz.kandrac.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import xyz.kandrac.game.screen.DemoScreen
import xyz.kandrac.game.screen.MenuScreen
import xyz.kandrac.game.screen.routeEvents
import xyz.kandrac.exercise.base.activeAsyncExercises
import xyz.kandrac.exercise.base.tests
import xyz.kandrac.exercise.e0001_user_name.UserNameMethodExercise
import xyz.kandrac.exercise.e0002_music_file.MusicFileMethodExercise
import xyz.kandrac.exercise.e0051_git_global_config.GitGlobalConfigExercise
import xyz.kandrac.exercise.e0052_git_local_config.GitLocalConfigExercise

internal class TeacherGame : Game() {

    override fun create() {
        KtxAsync.initiate()


        KtxAsync.launch {
            activeAsyncExercises.collect { exercises ->
                System.err.println("Active exercises after timeout -> ${exercises.size} -> ${exercises.joinToString(", ") {it.javaClass.name}}")
            }
        }

        KtxAsync.launch {
            tests.collect { tests ->
                System.err.println("Active tests")
                tests.forEach { (exercise, status) ->
                    System.err.println("${exercise::class.simpleName} - $status")
                }
                System.err.println("Active tests ---- end")
            }
        }

        val screens = mapOf(
            "menu" to MenuScreen(),
            "demo" to DemoScreen()
        )

        screen = screens["menu"]

//        if (!Gdx.graphics.isFullscreen) {
//            Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
//        }

        CoroutineScope(Dispatchers.Unconfined).launch {
            UserNameMethodExercise.test()
            MusicFileMethodExercise.test()
            GitGlobalConfigExercise.test()
            GitLocalConfigExercise.test()
            routeEvents.collect {
                screen = screens[it]
            }
        }
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        super.render()
    }
}
