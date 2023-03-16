package xyz.kandrac.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import xyz.kandrac.game.conversation.Conversation
import xyz.kandrac.game.conversation.ConversationData
import java.lang.Exception

/**
 * Class for handling all the talking. Do not make any other class that covers the bottom part of
 * the screen.
 */
internal class Talk {

    private var activeConversationData: ConversationData? = null
    private var activeConversation: Conversation? = null
    private var lines = listOf("")
    private var duration : Long? = null
    private var start = 0L

    private val textCamera by lazy { OrthographicCamera().apply {
        setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        update()
    } }

    private val communicationBatch by lazy { SpriteBatch() }

    private val font by lazy { createFont(24) }

    fun say(text: String, duration: Long? = null) {
        activeConversationData = null
        activeConversation = null
        this.lines = listOf(text)
        this.duration = duration
        start = System.currentTimeMillis()
    }

    fun say(conversationData: ConversationData) {
        activeConversationData = conversationData
        activeConversation = conversationData.conversations.first { it.id == conversationData.initialConversationId }.also {
            this.lines = listOf(it.text) + it.choices.map { it.text }
        }
        this.duration = null
        start = System.currentTimeMillis()
    }

    private fun say(conversation: Conversation) {
        activeConversation = conversation.also {
            this.lines = listOf(it.text) + it.choices.map { it.text }
        }
        this.duration = null
        start = System.currentTimeMillis()
    }

    fun silence() {
        this.lines = listOf("")
        this.duration = null
        activeConversationData = null
        activeConversation = null
        communicationBatch.projectionMatrix = textCamera.combined
        communicationBatch.begin()
        font.draw(communicationBatch, lines[0], 0f, 150f)
        communicationBatch.end()
    }

    fun render() {
        val duration = duration
        if (duration == null || System.currentTimeMillis() - start < duration) {
            communicationBatch.projectionMatrix = textCamera.combined
            communicationBatch.begin()
            lines.forEachIndexed { index, line -> font.draw(communicationBatch, line, 30f, 150f - 30 * index) }
            communicationBatch.end()
        }

        activeConversation?.let { conversation ->
            for (choice in conversation.choices) {
                try {
                    if (Gdx.input.isKeyPressed(Input.Keys.valueOf(choice.key))) {
                        activeConversation = activeConversationData!!.conversations.first { it.id == choice.conversation }
                        say(activeConversation!!)
                    }
                } catch (_: Exception) { }
            }
        }
    }

    fun dispose() {
        font.dispose()
        communicationBatch.dispose()
    }
}
