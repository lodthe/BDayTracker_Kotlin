package me.lodthe.bdaytracker.telegram.handlers

import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.EditMessageText
import com.pengrad.telegrambot.request.SendMessage
import me.lodthe.bdaytracker.database.UsersManager
import me.lodthe.bdaytracker.telegram.*
import me.lodthe.bdaytracker.vk.KVKBot
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

open class BaseHandler(private val kodein: Kodein) {
    protected val vkBot: KVKBot by kodein.instance()
    protected val bot: KTelegramBot by kodein.instance()
    private val requestSender: PriorityTelegramRequestSender by kodein.instance()
    protected val buttonManager: ButtonManager by kodein.instance()
    protected val users: UsersManager by kodein.instance()

    open suspend fun handle(update: Update) {
        sendMessage(update, "Handled")
    }

    protected open fun getChatId(update: Update): Long {
        return update.message().chat().id()
    }

    protected fun getChatId(message: Message): Long {
        return message.chat().id()
    }

    protected open suspend fun sendMessage(
        update: Update,
        messageText: String,
        markup: InlineKeyboardMarkup = InlineKeyboardMarkup(),
        priority: Int = 0,
        parseMode: ParseMode = ParseMode.Markdown
    ) {
        requestSender.addRequest(TelegramRequest(suspend {
            bot.execute(
                SendMessage(getChatId(update), messageText)
                    .replyMarkup(markup)
                    .parseMode(parseMode)
                    .disableWebPagePreview(true)
            )
            Unit
        }, priority))
    }

    protected open suspend fun editMessage(
        message: Message,
        newMessageText: String,
        newMarkup: InlineKeyboardMarkup = InlineKeyboardMarkup(),
        priority: Int = 0,
        parseMode: ParseMode = ParseMode.Markdown
    ) {
        requestSender.addRequest(TelegramRequest(suspend {
            bot.execute(
                EditMessageText(getChatId(message), message.messageId(), newMessageText)
                    .replyMarkup(newMarkup)
                    .parseMode(parseMode)
                    .disableWebPagePreview(true)
            )
            Unit
        }, priority))
    }
}