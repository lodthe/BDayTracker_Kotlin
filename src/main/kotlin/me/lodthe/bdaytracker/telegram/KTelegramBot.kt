package me.lodthe.bdaytracker.telegram

import com.pengrad.telegrambot.Callback
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.TelegramException
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.BaseRequest
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.BaseResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class KTelegramBot(private val TOKEN: String) {
    private val bot = TelegramBot(TOKEN)

    fun runUpdatesListener(): Flow<Update> = callbackFlow {
        bot.setUpdatesListener { updates ->
            updates.forEach { offer(it) }
            UpdatesListener.CONFIRMED_UPDATES_ALL
        }

        awaitClose()
    }

    suspend fun <T : BaseRequest<T, R>, R : BaseResponse> executeUnsafe(request: T): R {
        val stackTrace = getStackTrace()

        return suspendCoroutine { continuation ->
            bot.execute(request, object : Callback<T, R> {
                override fun onFailure(request: T, e: IOException?) {
                    stackTrace.initCause(e)
                    continuation.resumeWithException(stackTrace)
                }

                override fun onResponse(request: T, response: R) {
                    continuation.resume(response)
                }
            })
        }
    }

    suspend fun <T : BaseRequest<T, R>, R : BaseResponse> execute(request: T): R {
        val result = executeUnsafe(request)

        if (!result.isOk) {
            val message = "${request.method} failed with error_code ${result.errorCode()} ${result.description()}"
            val exception = TelegramException(message, result)
            throw exception
        }

        return result
    }

    private fun getStackTrace() = IOException().apply {
        stackTrace = stackTrace.copyOfRange(2, stackTrace.size)
    }
}