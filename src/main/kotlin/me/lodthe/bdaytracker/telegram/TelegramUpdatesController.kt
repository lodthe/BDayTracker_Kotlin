package me.lodthe.bdaytracker.telegram

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import me.lodthe.bdaytracker.getLogger
import me.lodthe.bdaytracker.telegram.handlers.CallbackHandler
import me.lodthe.bdaytracker.telegram.handlers.MessageHandler
import org.kodein.di.Kodein
import org.kodein.di.generic.instance


class TelegramUpdatesController(private val kodein: Kodein) {
    private val bot: KTelegramBot by kodein.instance()
    private val messageHandler: MessageHandler by kodein.instance()
    private val callbackHandler: CallbackHandler by kodein.instance()

    suspend fun run()= coroutineScope {
        logger.info("Telegram updates controller was started")
        bot.runUpdatesListener().collect {update ->
            try {
                when {
                    update.message()?.text() != null -> messageHandler.handle(update)
                    update.callbackQuery() != null -> callbackHandler.handle(update)
                }
            } catch(e: Exception) {
                logger.error("Couldn't process update", e)
            }
        }
    }

    companion object {
        val logger = getLogger<TelegramUpdatesController>()
    }
}