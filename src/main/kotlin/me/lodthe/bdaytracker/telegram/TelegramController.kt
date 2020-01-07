package me.lodthe.bdaytracker.telegram

import com.pengrad.telegrambot.TelegramException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import me.lodthe.bdaytracker.telegram.handlers.CallbackHandler
import me.lodthe.bdaytracker.telegram.handlers.MessageHandler
import org.kodein.di.Kodein
import org.kodein.di.generic.instance


class TelegramController(private val kodein: Kodein) {
    private val bot: KTelegramBot by kodein.instance()
    private val messageHandler: MessageHandler by kodein.instance()
    private val callbackHandler: CallbackHandler by kodein.instance()

    suspend fun run()= coroutineScope {
        bot.runUpdatesListener().collect {update ->
            try {
                when {
                    update.message()?.text() != null -> messageHandler.handle(update)
                    update.callbackQuery() != null -> callbackHandler.handle(update)
                }
            } catch(e: TelegramException) {
                println(e)
            }
        }
    }
}