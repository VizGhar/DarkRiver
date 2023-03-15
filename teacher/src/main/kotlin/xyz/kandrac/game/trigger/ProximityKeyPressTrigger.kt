package xyz.kandrac.game.trigger

import com.badlogic.gdx.Gdx
import xyz.kandrac.game.obj.BodyOwner

/**
 * Trigger exercise when 2 game objects gets too close and key is hit
 * to each other
 */
internal class ProximityKeyPressTrigger(
    private val body1: BodyOwner,
    private val body2: BodyOwner,
    private val maxDistance: Float,
    private val key: Int,
    val onTriggerHitChange: (Boolean) -> Unit
) {

    private var previous = false

    fun check() {
        previous = if (body1.distanceFrom(body2) < maxDistance && Gdx.input.isKeyPressed(key)) {
            if (!previous) {
                onTriggerHitChange(true)
            }
            true
        } else {
            if (previous) {
                onTriggerHitChange(false)
            }
            false
        }
    }
}