package me.lodthe.bdaytracker.telegram

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.EditMessageText
import com.pengrad.telegrambot.request.SendMessage
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class SmartTelegramMessageRequestsController(kodein: Kodein) {
    private val bot: KTelegramBot by kodein.instance()
    private val requestSender: PriorityTelegramRequestSender by kodein.instance()

    suspend fun sendMessage(
        chatId: Long,
        messageText: String,
        markup: InlineKeyboardMarkup = InlineKeyboardMarkup(),
        priority: Int = 0,
        parseMode: ParseMode = ParseMode.Markdown
    ) {
        requestSender.addRequest(TelegramRequest(suspend {
            bot.execute(
                SendMessage(chatId, messageText)
                    .replyMarkup(markup)
                    .parseMode(parseMode)
                    .disableWebPagePreview(true)
            )
            Unit
        }, priority))
    }

    suspend fun editMessage(
        chatId: Long,
        messageId: Int,
        newMessageText: String,
        newMarkup: InlineKeyboardMarkup = InlineKeyboardMarkup(),
        priority: Int = 0,
        parseMode: ParseMode = ParseMode.Markdown
    ) {
        requestSender.addRequest(TelegramRequest(suspend {
            bot.execute(
                EditMessageText(chatId, messageId, newMessageText)
                    .replyMarkup(newMarkup)
                    .parseMode(parseMode)
                    .disableWebPagePreview(true)
            )
            Unit
        }, priority))
    }
}