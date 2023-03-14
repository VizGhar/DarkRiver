package xyz.kandrac.game.trigger

import xyz.kandrac.game.obj.BodyOwner
import kotlin.math.absoluteValue

/**
 * Manhattan distance
 */
private fun BodyOwner.distanceFrom(that: BodyOwner) =
    (this.body.position.x - that.body.position.x).absoluteValue +
    (this.body.position.y - that.body.position.y).absoluteValue

/**
 * Trigger exercise when 2 game objects gets too close
 * to each other
 */
internal class ProximityTrigger(
    private val body1: BodyOwner,
    private val body2: BodyOwner,
    private val maxDistance: Float,
    val onTriggerHitChange: (Boolean) -> Unit
) {

    private var previous = false

    fun check() {
        previous = if (body1.distanceFrom(body2) < maxDistance) {
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