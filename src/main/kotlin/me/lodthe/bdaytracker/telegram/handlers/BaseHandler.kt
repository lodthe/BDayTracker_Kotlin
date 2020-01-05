package me.lodthe.bdaytracker.telegram.handlers

import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import me.lodthe.bdaytracker.database.UsersManager
import me.lodthe.bdaytracker.telegram.*
import me.lodthe.bdaytracker.vk.KVKBot
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

open class BaseHandler(private val kodein: Kodein) {
    protected val vkBot: KVKBot by kodein.instance()
    protected val bot: KTelegramBot by kodein.instance()
    protected val sender: MessageSender by kodein.instance()
    protected val buttonManager: ButtonManager by kodein.instance()
    protected val users: UsersManager by kodein.instance()

    open suspend fun handle(update: Update) {
        sender.send(
            SendMessage(update.message().chat().id(), "Handled")
        )
    }

    open fun getChatId(update: Update): Long {
        return update.message().chat().id()
    }
}