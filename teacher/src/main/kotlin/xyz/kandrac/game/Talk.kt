package xyz.kandrac.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch

internal class Talk {

    private var text = ""
    private var duration : Long? = null
    private var start = 0L

    private val textCamera by lazy { OrthographicCamera().apply {
        setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        update()
    } }

    private val communicationBatch by lazy { SpriteBatch() }

    private val font by lazy { createFont(24) }

    fun say(text: String, duration: Long? = null) {
        this.text = text
        this.duration = duration
        start = System.currentTimeMillis()
    }

    fun silence() {
        this.text = ""
        this.duration = null
        communicationBatch.projectionMatrix = textCamera.combined
        communicationBatch.begin()
        font.draw(communicationBatch, text, 0f, 120f)
        communicationBatch.end()
    }

    fun render() {
        val duration = duration
        if (duration == null || System.currentTimeMillis() - start < duration) {
            communicationBatch.projectionMatrix = textCamera.combined
            communicationBatch.begin()
            font.draw(communicationBatch, text, 0f, 120f)
            communicationBatch.end()
        }
    }

    fun dispose() {
        font.dispose()
        communicationBatch.dispose()
    }
}
