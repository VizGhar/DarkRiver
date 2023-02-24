package xyz.kandrac.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import xyz.kandrac.loadAnimations


private const val characterSpeed = 5f

internal class Hero(world: World, private val scale: Float) {

    private enum class HeroOrientation { LEFT, RIGHT }

    val x get() = body.position.x
    val y get() = body.position.y

    private var stateTime = 0f
    private var orientation = HeroOrientation.RIGHT

    private val spriteBatch by lazy { SpriteBatch() }

    private val animations by lazy { loadAnimations("dark_river/player.png", 6, 4) }
    private val idleAnimation by lazy { animations.animations[0] }
    private val walkRightAnimation by lazy { animations.animations[1] }
    private val swordAnimation by lazy { animations.animations[2] }     // TODO only 4 frames
    private val deathAnimation by lazy { animations.animations[3] }     // TODO only 3 frames

    private val body by lazy {
        val def = BodyDef().apply { type = BodyDef.BodyType.DynamicBody; position.set(8f, 6f); fixedRotation = true }
        val result = world.createBody(def)
        val shape = PolygonShape().apply { setAsBox(4f * scale, 4f * scale) }
        result.createFixture(shape, 1f)
        result
    }

    fun draw(camera: Camera) {
        stateTime += Gdx.graphics.deltaTime
        spriteBatch.begin()
        spriteBatch.projectionMatrix = camera.combined

        body.setLinearVelocity(
            when {
                Gdx.input.isKeyPressed(Keys.RIGHT) && Gdx.input.isKeyPressed(Keys.LEFT) -> 0f
                Gdx.input.isKeyPressed(Keys.RIGHT) -> characterSpeed
                Gdx.input.isKeyPressed(Keys.LEFT) -> -characterSpeed
                else -> 0f
            }, when {
                Gdx.input.isKeyPressed(Keys.UP) && Gdx.input.isKeyPressed(Keys.DOWN) -> 0f
                Gdx.input.isKeyPressed(Keys.UP) -> characterSpeed
                Gdx.input.isKeyPressed(Keys.DOWN) -> -characterSpeed
                else -> 0f
            })

        when {
            body.linearVelocity.x < 0f -> orientation = HeroOrientation.LEFT
            body.linearVelocity.x > 0f -> orientation = HeroOrientation.RIGHT
        }

        val frame = when {
            body.linearVelocity.epsilonEquals(0f, 0f) -> idleAnimation
            else -> walkRightAnimation
        }.getKeyFrame(stateTime, true)

        if ((orientation == HeroOrientation.RIGHT && frame.isFlipX) || (orientation == HeroOrientation.LEFT && !frame.isFlipX)) {
            frame.flip(true, false)
        }

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
