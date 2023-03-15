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

private const val characterSpeed = 5f

internal class Hero(world: World, private val scale: Float) : BodyOwner {

    private enum class HeroOrientation { LEFT, RIGHT }

    private var stateTime = 200f
    private var orientation = HeroOrientation.RIGHT

    private val spriteBatch by lazy { SpriteBatch() }

    private val animations by lazy {
        loadAnimations("dark_river/player.png", 6, 4) { row, frames ->
            when (row) {
                2 -> Animation(0.1f, *(frames.take(4).toTypedArray()))
                3 -> Animation(0.1f, *(frames.take(3).toTypedArray()))
                else -> Animation(0.1f, *frames)
            }
        }
    }
    private val idleAnimation by lazy { animations.animations[0] }
    private val walkRightAnimation by lazy { animations.animations[1] }
    private val swordAnimation by lazy { animations.animations[2] }
    private val deathAnimation by lazy { animations.animations[3] }

    override val body: Body by lazy {
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
                Gdx.input.isKeyPressed(Input.Keys.RIGHT) && Gdx.input.isKeyPressed(Input.Keys.LEFT) -> 0f
                Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> characterSpeed
                Gdx.input.isKeyPressed(Input.Keys.LEFT) -> -characterSpeed
                else -> 0f
            }, when {
                Gdx.input.isKeyPressed(Input.Keys.UP) && Gdx.input.isKeyPressed(Input.Keys.DOWN) -> 0f
                Gdx.input.isKeyPressed(Input.Keys.UP) -> characterSpeed
                Gdx.input.isKeyPressed(Input.Keys.DOWN) -> -characterSpeed
                else -> 0f
            }
        )

        if (stateTime <= swordAnimation.animationDuration) { body.setLinearVelocity(0f, 0f) }

        when {
            body.linearVelocity.x < 0f -> orientation = HeroOrientation.LEFT
            body.linearVelocity.x > 0f -> orientation = HeroOrientation.RIGHT
        }

        val frame = when {
            stateTime < swordAnimation.animationDuration -> swordAnimation
            Gdx.input.isKeyPressed(Input.Keys.SPACE) -> { stateTime = 0f; swordAnimation }
            body.linearVelocity.x != 0f || body.linearVelocity.y != 0f-> walkRightAnimation
            else -> idleAnimation
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