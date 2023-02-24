package xyz.kandrac

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable

data class SpriteSheetAnimation(
    private val spriteSheet: Texture,
    val animations: List<Animation<TextureRegion>>
) : Disposable {

    override fun dispose() = spriteSheet.dispose()

}

/**
 * Loads animations from sprite-sheet. Sprite-sheet has [columns] columns and [rows] rows.
 *
 * Every row should contain single type of animation
 */
fun loadAnimations(spriteSheet: String, columns: Int, rows: Int): SpriteSheetAnimation {
    val sheet = Texture(Gdx.files.internal(spriteSheet))
    val frames = TextureRegion.split(sheet, sheet.width / columns, sheet.height / rows)
    return SpriteSheetAnimation(sheet, frames.map { f -> Animation(0.1f, *f) })
}