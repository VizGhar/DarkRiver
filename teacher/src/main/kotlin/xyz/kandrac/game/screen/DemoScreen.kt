package xyz.kandrac.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.box2d.createWorld
import ktx.graphics.lerpTo
import xyz.kandrac.exercise.e0001_user_name.UserNameMethodExercise
import xyz.kandrac.exercise.e0002_music_file.MusicFileMethodExercise
import xyz.kandrac.exercise.e0004_long_run.LongRunExercise
import xyz.kandrac.game.obj.creature.Hero
import xyz.kandrac.game.MusicControl
import xyz.kandrac.game.Talk
import xyz.kandrac.game.getCollisionBodies
import xyz.kandrac.game.obj.creature.Critter
import xyz.kandrac.game.trigger.ProximityKeyPressTrigger
import xyz.kandrac.game.trigger.ProximityTrigger

const val SCALE = 1 / 16f
const val DEMO_MAP = "dark_river/location_reached_tests.tmx"
const val VIEWPORT_WIDTH = 16f
const val VIEWPORT_HEIGHT = 9f

class DemoScreen : ScreenAdapter() {

    private val world by lazy { createWorld() }
    private val camera by lazy { OrthographicCamera() }
    private val viewport by lazy { ExtendViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera) }
    private val hero by lazy { Hero(world, SCALE) }
    private val critter by lazy { Critter(world, SCALE) }
    private val talk by lazy { Talk() }
    private val musicControl by lazy { MusicControl { setBackgroundMusic() } }
    private val tileMap by lazy { TmxMapLoader().load(DEMO_MAP) }
    private val mapRenderer by lazy { OrthogonalTiledMapRenderer(tileMap, SCALE) }
    private val boxRender by lazy { Box2DDebugRenderer() }
    private val heroCritterProximity by lazy { ProximityTrigger(hero, critter, 1.5f) { if(it) talk.say("Press (K) to talk") else talk.silence() } }
    private val heroCritterTalk by lazy { ProximityKeyPressTrigger(hero, critter, 1.5f, Keys.K) { if(it) talk.say("Nice") } }
    private var talkTested = false

//    private val debugRenderer by lazy { Box2DDebugRenderer() }

    init {
        camera; viewport; hero; critter; talk; musicControl; tileMap; mapRenderer
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)
        camera.position.set(hero.x, hero.y, 0f)
        addCollisions()
    }

    override fun render(delta: Float) {
        super.render(delta)
        camera.lerpTo(Vector2(hero.x, hero.y), 0.1f)

        world.step(1/60f, 6, 2)
        camera.update()
        mapRenderer.setView(camera)
        mapRenderer.render()
        hero.draw(camera)
        critter.draw(camera)
        talk.render()
        musicControl.draw()
        heroCritterProximity.check()
        heroCritterTalk.check()
//        debugRenderer.render(world, camera.combined);

        if (!talkTested) {
            talkTested = true
            val name = UserNameMethodExercise.call()
            LongRunExercise.enqueue()
            System.err.println("Hello $name")
        }
    }

    override fun dispose() {
        world.dispose()
        hero.dispose()
        critter.dispose()
        talk.dispose()
        musicControl.dispose()
        tileMap.dispose()
        mapRenderer.dispose()
        boxRender.dispose()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, false)
        camera.position.set(hero.x, hero.y, 0f)
    }

    private fun addCollisions() {
        tileMap.getCollisionBodies(world, SCALE, "collisions")
    }

    private fun setBackgroundMusic() {
        val exercise = MusicFileMethodExercise
        val result = exercise.call()
        if (result != null) {
            System.err.println("music file - ${result.absolutePath}")
            musicControl.setBackgroundMusic(result.absolutePath)
        } else {
            System.err.println("no music file")
        }
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }
}