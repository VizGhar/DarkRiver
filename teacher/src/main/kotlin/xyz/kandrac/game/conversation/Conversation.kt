package xyz.kandrac.game.conversation

import com.badlogic.gdx.files.FileHandle
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.util.Scanner

class ConversationHandler(private val handle: FileHandle) {

    private val content by lazy {
        Gson().fromJson(handle.reader(), ConversationData::class.java)
    }

    fun startConversation() {
        val scanner = Scanner(System.`in`)
        var activeConversation: Conversation? = content.conversations.first { it.id == content.initialConversationId }
        while (activeConversation != null) {
            val choices = activeConversation.choices
            System.err.println(activeConversation.text)

            if (choices.isEmpty()) break
            for (choice in choices) {
                System.err.println(choice.text)
            }

            val pickedChoiceKey = scanner.nextLine()
            val pickedChoice = choices.firstOrNull { it.key == pickedChoiceKey }

            if (pickedChoice == null) {
                System.err.println("I didn't get it")
            } else {
                activeConversation = content.conversations.first { it.id == pickedChoice.conversation }
            }
        }
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