package xyz.kandrac.game.conversation

import com.badlogic.gdx.files.FileHandle
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import xyz.kandrac.game.Talk

internal class ConversationHandler(private val handle: FileHandle, private val talk: Talk) {

    private val content by lazy {
        Gson().fromJson(handle.reader(), ConversationData::class.java)
    }

    fun startConversation() {
        talk.say(content)
    }
}

data class ConversationData(
    @SerializedName("initial_id") val initialConversationId: String,
    @SerializedName("conversations") val conversations: List<Conversation>,
)

data class Conversation(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String,
    @SerializedName("choices") val choices: List<Choice>
)

data class Choice(
    @SerializedName("id") val id: String,
    @SerializedName("key") val key: String,
    @SerializedName("text") val text: String,
    @SerializedName("conversation_id") val conversation: String
)