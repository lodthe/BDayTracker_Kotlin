package me.lodthe.bdaytracker

import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class User(
    val api_key: String,
    val type: String = "user",
    val platform: String = "Telegram",
    val message: String,
    val intent: String?,
    val not_handled: String? = null,
    val version: String = "1.0",
    val user_id: String
)

data class Agent(
    val api_key: String,
    val type: String = "agent",
    val platform: String = "Telegram",
    val message: String,
    val version: String = "1.0",
    val user_id: String
)

class ChatBaseAPI(private val API_KEY: String) {
    private val client = OkHttpClient()
    private val gson = GsonBuilder()
        .let {
            it.setPrettyPrinting()
            it.create()
        }

    suspend fun sendUserRequest(intent: String?, message: String, userId: Long) = GlobalScope.launch {
        logger.info("$userId sent message with $intent")
        execute(gson.toJson(User(
            api_key = API_KEY,
            message = message,
            intent = intent,
            not_handled = if (intent == null) "true" else null,
            user_id = userId.toString()
        )))
    }

    suspend fun sendAgentResponse(message: String, userId: Long) = GlobalScope.launch {
        logger.info("Agent sent response to $userId")
        execute(gson.toJson(Agent(
            api_key = API_KEY,
            message = message,
            user_id = userId.toString()
        )))
    }

    private suspend fun execute(json: String): Response = suspendCoroutine { continuation ->
        try {
            client.newCall(
                Request.Builder()
                    .url(API_URL)
                    .addHeader("cache-control", "no-cache")
                    .addHeader("content-type", "application/json")
                    .post(RequestBody.create(MediaType.get("application/json; charset=utf-8"), json))
                    .build()
            ).enqueue(object: Callback {
                override fun onResponse(call: Call, response: Response) {
                    response.close()
                    continuation.resume(response)
                }

                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val API_URL = "https://chatbase-area120.appspot.com/api/message"
        val logger = getLogger<ChatBaseAPI>()
    }
}