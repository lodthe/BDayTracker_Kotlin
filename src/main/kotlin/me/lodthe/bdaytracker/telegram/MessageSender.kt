package me.lodthe.bdaytracker.telegram

import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class MessageSender(private val kodein: Kodein) {
    private val requestSender: PriorityTelegramRequestSender by kodein.instance()
    private val bot: KTelegramBot by kodein.instance()

    suspend fun send(message: SendMessage, priority: Int = 0, parseMode: ParseMode = ParseMode.Markdown) {
        requestSender.addRequest(TelegramRequest(suspend {
            bot.execute(
                message
                    .parseMode(parseMode)
                    .disableWebPagePreview(true)
            )
            Unit
        }, priority))
    }
}