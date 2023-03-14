package xyz.kandrac.game.obj.creature

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import xyz.kandrac.game.obj.BodyOwner
import xyz.kandrac.loadAnimations

internal class Critter(world: World, private val scale: Float) : BodyOwner {

    private enum class SlimeOrientation { LEFT, RIGHT }

    private var stateTime = 200f
    private var orientation = SlimeOrientation.RIGHT

    private val spriteBatch by lazy { SpriteBatch() }

    private val animations by lazy {
        loadAnimations("dark_river/slime.png", 7, 5) { row, frames ->
            when (row) {
                0 -> Animation(0.1f, *(frames.take(4).toTypedArray()))
                1 -> Animation(0.1f, *(frames.take(6).toTypedArray()))
                2 -> Animation(0.1f, *(frames.take(7).toTypedArray()))
                3 -> Animation(0.1f, *(frames.take(3).toTypedArray()))
                4 -> Animation(0.1f, *(frames.take(5).toTypedArray()))
                else -> throw IllegalStateException()
            }
        }
    }

    private val idleAnimation by lazy { animations.animations[0] }

    override val body: Body by lazy {
        val def = BodyDef().apply { type = BodyDef.BodyType.StaticBody; position.set(20f, 6f); fixedRotation = true }
        val result = world.createBody(def)
        val shape = PolygonShape().apply { setAsBox(4f * scale, 4f * scale) }
        result.createFixture(shape, 1f)
        result
    }

    fun draw(camera: Camera) {
        stateTime += Gdx.graphics.deltaTime
        spriteBatch.begin()
        spriteBatch.projectionMatrix = camera.combined

        val frame = when {
            else -> idleAnimation
        }.getKeyFrame(stateTime, true)

        spriteBatch.draw(
            frame,
            (body.position.x + 0 * scale * 32) - 16f * scale,
            (body.position.y + 0 * scale * 32) - 9f * scale,
            32f * scale,
            32f * scale)

        spriteBatch.end()
    }

    fun dispose() {
        spriteBatch.dispose()
        animations.dispose()
    }
}