package me.lodthe.bdaytracker.telegram.handlers

import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.ParseMode
import me.lodthe.bdaytracker.ChatBaseAPI
import me.lodthe.bdaytracker.database.UsersManager
import me.lodthe.bdaytracker.getLogger
import me.lodthe.bdaytracker.telegram.ButtonManager
import me.lodthe.bdaytracker.telegram.KTelegramBot
import me.lodthe.bdaytracker.telegram.SmartTelegramMessageRequestsController
import me.lodthe.bdaytracker.vk.KVKBot
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

open class BaseHandler(kodein: Kodein) {
    private val chatbase: ChatBaseAPI by kodein.instance()
    protected val vkBot: KVKBot by kodein.instance()
    protected val bot: KTelegramBot by kodein.instance()
    private val smartMessageController: SmartTelegramMessageRequestsController by kodein.instance()
    protected val buttonManager: ButtonManager by kodein.instance()
    protected val users: UsersManager by kodein.instance()

    open suspend fun handle(update: Update): Unit {
        sendMessage(update, "Handled")
    }

    protected open fun getChatId(update: Update): Long {
        return update.message().chat().id()
    }

    private fun getChatId(message: Message): Long {
        return message.chat().id()
    }

    protected open fun getMessageFromUpdate(update: Update): Message {
        return update.message()
    }

    protected suspend fun sendUserRequest(intent: String?, update: Update) {
        chatbase.sendUserRequest(intent, getMessageFromUpdate(update).text(), getChatId(update))
    }

    protected open suspend fun sendMessage(
        update: Update,
        messageText: String,
        markup: InlineKeyboardMarkup = InlineKeyboardMarkup(),
        priority: Int = 0,
        parseMode: ParseMode = ParseMode.Markdown
    ) {
        logger.info("Sending message to ${getChatId(update)}: $messageText")
        chatbase.sendAgentResponse(getMessageFromUpdate(update).text(), getChatId(update))
        smartMessageController.sendMessage(
            getChatId(update),
            messageText,
            markup,
            priority,
            parseMode
        )
    }

    protected open suspend fun editMessage(
        message: Message,
        newMessageText: String,
        newMarkup: InlineKeyboardMarkup = InlineKeyboardMarkup(),
        priority: Int = 0,
        parseMode: ParseMode = ParseMode.Markdown
    ) {
        logger.error("Editing message #${message.messageId()} from ${getChatId(message)}: $newMessageText")
        chatbase.sendAgentResponse(message.text(), getChatId(message))
        smartMessageController.editMessage(
            getChatId(message),
            message.messageId(),
            newMessageText,
            newMarkup,
            priority,
            parseMode
        )
    }

    companion object {
        val logger = getLogger<BaseHandler>()
    }
}