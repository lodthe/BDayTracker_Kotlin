package me.lodthe.bdaytracker.telegram.handlers

import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import kotlinx.coroutines.coroutineScope
import me.lodthe.bdaytracker.database.UserState
import me.lodthe.bdaytracker.telegram.MessageLabel
import me.lodthe.bdaytracker.telegram.TelegramRequest
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class MessageHandler(private val kodein: Kodein) : BaseHandler(kodein) {
    private val callbackHandler: CallbackHandler by kodein.instance()

    override suspend fun handle(update: Update) = coroutineScope {
        val trimmedText = update.message().text().trim()
        val user = users.getOrCreateUser(getChatId(update))
        when {
            trimmedText.startsWith("/start") -> handleStart(update)
            user.state == UserState.CHANGING_ID -> handleChangingId(update)
        }
    }

    private suspend fun handleChangingId(update: Update) {
        val id = update.message().text()
        when {
            id.any { !it.isDigit() } -> {
                callbackHandler.sendUpdateVkIdMessage(getChatId(update))
            }
            else -> {
                val user = users.getOrCreateUser(getChatId(update))
                user.vkId = id.toInt()
                user.state = UserState.NONE
                user.updateVKFriends(vkBot.getFriendList(user.vkId!!)!!.items)
                users.updateUser(user)

                sender.send(
                    SendMessage(update.message().chat().id(), MessageLabel.ID_HAS_CHANGED.label)
                        .replyMarkup(buttonManager.getRegularButtons())
                )
            }
        }
    }

    private suspend fun handleStart(update: Update) {
        sender.send(
        SendMessage(update.message().chat().id(), MessageLabel.START.label)
            .replyMarkup(buttonManager.getStartButtons())
        )
    }
}