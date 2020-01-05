package me.lodthe.bdaytracker.telegram.handlers

import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import kotlinx.coroutines.coroutineScope
import me.lodthe.bdaytracker.database.Friend
import me.lodthe.bdaytracker.database.UserState
import me.lodthe.bdaytracker.parseDateFromString
import me.lodthe.bdaytracker.telegram.ButtonLabel
import me.lodthe.bdaytracker.telegram.MessageLabel
import org.kodein.di.Kodein

class CallbackHandler(private val kodein: Kodein) : BaseHandler(kodein) {
    override suspend fun handle(update: Update) = coroutineScope {
        val callbackData = update.callbackQuery().data()
        when {
            callbackData == ButtonLabel.MENU.label -> handleMenu(update)
            callbackData == ButtonLabel.IMPORT_FROM_VK.label -> handleImportFromVK(update)
            callbackData == ButtonLabel.UPDATE_VK_ID.label -> handleUpdateVkId(update)
            callbackData.startsWith(ButtonLabel.LIST_OF_USERS.label) -> handleListOfUsers(update)
        }
    }

    private suspend fun handleMenu(update: Update) {
        sender.send(
            SendMessage(getChatId(update), MessageLabel.MENU.label)
                .replyMarkup(buttonManager.getMenuButtons())
        )
    }

    private suspend fun handleImportFromVK(update: Update) {
        val user = users.getOrCreateUser(getChatId(update))
        when {
            user.vkId == null -> handleUpdateVkId(update)
            else -> {
                user.updateVKFriends(vkBot.getFriendList(user.vkId!!)!!.items)
                users.updateUser(user)

                sender.send(
                    SendMessage(getChatId(update), MessageLabel.IMPORT_FROM_VK.label)
                        .replyMarkup(buttonManager.getRegularButtons())
                )
            }
        }
    }

    private suspend fun handleUpdateVkId(update: Update) {
        val user = users.getOrCreateUser(getChatId(update))
        user.state = UserState.CHANGING_ID
        users.updateUser(user)
        sendUpdateVkIdMessage(getChatId(update))
    }

    suspend fun sendUpdateVkIdMessage(chatId: Long) {
        sender.send(
            SendMessage(chatId, MessageLabel.UPDATE_VK_ID.label)
                .replyMarkup(buttonManager.getGetIdButtons())
        )
    }

    private suspend fun handleListOfUsers(update: Update) {
        sender.send(
            SendMessage(getChatId(update), users.getOrCreateUser(getChatId(update)).getFriendList())
                .replyMarkup(buttonManager.getRegularButtons())
        )
    }

    override fun getChatId(update: Update): Long {
        return update.callbackQuery().message().chat().id()
    }
}