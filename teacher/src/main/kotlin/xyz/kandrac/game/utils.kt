package xyz.kandrac.game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import io.github.serpro69.kfaker.faker
import ktx.box2d.body
import ktx.box2d.box
import ktx.tiled.forEachMapObject
import java.security.SecureRandom

private val allowedCharacters = "abcdefghijklmnopqrstuvwxzyABCDEFGHIJKLMNOPQRSTUVWXYZ"
internal val random: SecureRandom = SecureRandom(System.currentTimeMillis().toString().toByteArray())
val faker = faker { fakerConfig { locale = "sk" } }

internal fun SecureRandom.getRandomText(length: Int) =
    (0 until length).map { allowedCharacters[nextInt(allowedCharacters.length)] }.joinToString("")

internal fun TiledMap.getCollisionBodies(
    world: World,
    scale: Float = 1f,
    collisionLayerName: String = "collisions"
) {
    forEachMapObject(collisionLayerName) { mapObject ->
        if (mapObject !is RectangleMapObject) return@forEachMapObject
        val rectangle: Rectangle = mapObject.rectangle
        val body = world.body { box(width = rectangle.width * scale, height = rectangle.height * scale) }
        val center = Vector2()
        rectangle.getCenter(center)
        body.setTransform(center.x * scale, center.y * scale, 0f)
    }
}

internal fun createFont(size: Int) : BitmapFont {
    val generator = FreeTypeFontGenerator(Gdx.files.internal("VT323-Regular.ttf"))
    val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
    parameter.size = size
    parameter.characters += "áäčďéíľĺňóôŕšťúýžÁČĎÉÍĽĹŇÓŔŠŤÚÝŽ"
    return with(generator) {
        val font = generateFont(parameter)
        dispose()
        font
    }
}