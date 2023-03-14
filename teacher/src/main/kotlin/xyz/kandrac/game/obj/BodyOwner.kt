package xyz.kandrac.game.obj

import com.badlogic.gdx.physics.box2d.Body

interface BodyOwner {
    val body: Body
    val x get() = body.position.x
    val y get() = body.position.y
}