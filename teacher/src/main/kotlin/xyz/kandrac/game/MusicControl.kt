package xyz.kandrac.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

internal class MusicControl(val onClick: () -> Unit) {

    private val buttonSpriteBatch = SpriteBatch()

    private var backgroundMusic: Music? = null

    fun setBackgroundMusic(absolutePath: String) {
        if (backgroundMusic != null) return
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.absolute(absolutePath)).apply {
            isLooping = true
            play()
            buttonDisable.isVisible = true
            buttonEnable.isVisible = false
        }
    }

    private val stage by lazy { Stage() }

    private val buttonEnable: ImageButton by lazy {
        val texture = Texture(Gdx.files.internal("music-note-outline.png"))
        val region = TextureRegion(texture)
        val myTexRegionDrawable = TextureRegionDrawable(region)

        val button = ImageButton(myTexRegionDrawable)

        button.setSize(16f, 16f)
        button.setPosition(Gdx.graphics.width - button.width, Gdx.graphics.height - button.height)
        button.addListener {
            if (it is InputEvent && it.type == InputEvent.Type.touchDown) {
                onClick()
                true
            } else {
                false
            }
        }
        button.isVisible = true
        stage.addActor(button)
        Gdx.input.inputProcessor = stage
        button
    }

    private val buttonDisable: ImageButton by lazy {
        val texture = Texture(Gdx.files.internal("music-note-off-outline.png"))
        val region = TextureRegion(texture)
        val myTexRegionDrawable = TextureRegionDrawable(region)

        val button = ImageButton(myTexRegionDrawable)

        button.setSize(16f, 16f)
        button.setPosition(Gdx.graphics.width - button.width, Gdx.graphics.height - button.height)
        button.addListener {
            if (it is InputEvent && it.type == InputEvent.Type.touchDown) {
                backgroundMusic?.stop()
                backgroundMusic?.dispose()
                backgroundMusic = null
                buttonDisable.isVisible = false
                buttonEnable.isVisible = true
                true
            } else {
                false
            }
        }
        button.isVisible = false
        stage.addActor(button)
        Gdx.input.inputProcessor = stage
        button
    }

    fun draw() {
        buttonSpriteBatch.begin()
        if (buttonDisable.isVisible) buttonDisable.draw(buttonSpriteBatch, 1f)
        if (buttonEnable.isVisible) buttonEnable.draw(buttonSpriteBatch, 1f)
        buttonSpriteBatch.end()
    }

    fun dispose() {
        buttonSpriteBatch.dispose()
        backgroundMusic?.dispose()
    }

}