package com.mrrobot.aiworkspace.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mrrobot.aiworkspace.data.ChatMessage
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(
    name = "chat_memory"
)

object ChatStorage {

    private val CHAT_KEY =
        stringPreferencesKey("messages")

    suspend fun saveMessages(
        context: Context,
        messages: List<ChatMessage>
    ) {

        val array = JSONArray()

        messages.forEach {

            val obj = JSONObject()

            obj.put("role", it.role)
            obj.put("content", it.content)

            array.put(obj)
        }

        context.dataStore.edit {

            it[CHAT_KEY] = array.toString()
        }
    }

    suspend fun loadMessages(
        context: Context
    ): List<ChatMessage> {

        return try {

            val prefs =
                context.dataStore.data.first()

            val raw =
                prefs[CHAT_KEY] ?: return emptyList()

            val array = JSONArray(raw)

            buildList {

                for (i in 0 until array.length()) {

                    val obj =
                        array.getJSONObject(i)

                    add(
                        ChatMessage(
                            role =
                                obj.getString("role"),

                            content =
                                obj.getString("content")
                        )
                    )
                }
            }

        } catch (e: Exception) {

            emptyList()
        }
    }
}
