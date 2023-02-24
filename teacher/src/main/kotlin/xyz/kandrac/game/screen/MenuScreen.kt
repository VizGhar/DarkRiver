package xyz.kandrac.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.utils.viewport.ExtendViewport
import xyz.kandrac.game.createFont


class MenuScreen : ScreenAdapter() {

    private val stage: Stage = Stage(ExtendViewport(800f, 800f))
    private val startGame: TextButton by lazy { TextButton("Start Game", textButtonStyle) }
    private val demoButton: TextButton by lazy { TextButton("Start Demo", textButtonStyle) }
    private val textButtonStyle: TextButtonStyle by lazy { TextButtonStyle().apply { font = createFont(24) }}

    init {
        val table = Table().apply {
            setFillParent(true)
            center()
            add(startGame)
            row()
            add(demoButton)
            row()
        }
        stage.addActor(table)
        Gdx.input.inputProcessor = stage
        demoButton.addListener {
            if (it !is InputEvent || it.type != InputEvent.Type.touchDown) return@addListener true
            route("demo")
            true
        }
    }

    override fun render(delta: Float) {
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun dispose() {
        stage.dispose()
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }
}