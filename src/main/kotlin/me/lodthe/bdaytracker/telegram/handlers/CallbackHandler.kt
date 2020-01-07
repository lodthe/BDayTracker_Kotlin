package me.lodthe.bdaytracker.telegram.handlers

import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import com.vk.api.sdk.exceptions.ApiPrivateProfileException
import kotlinx.coroutines.coroutineScope
import me.lodthe.bdaytracker.database.UserState
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
            callbackData == ButtonLabel.ADD_FRIEND.label -> handleAddFriend(update)
            callbackData == ButtonLabel.REMOVE_FRIEND.label -> handleRemoveFriend(update)
            callbackData.startsWith(ButtonLabel.LIST_OF_FRIENDS.label) -> handleListOfFriends(update)
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
        if (user.vkId == null) {
            handleUpdateVkId(update)
        } else {
            try {
                user.updateVKFriends(vkBot.getFriendList(user.vkId!!)!!.items)
            } catch (e: ApiPrivateProfileException) {
                return sender.send(
                    SendMessage(getChatId(update), MessageLabel.PROFILE_IS_CLOSED.label)
                        .replyMarkup(buttonManager.getRegularButtons())
                )
            }
            users.updateUser(user)

            sender.send(
                SendMessage(getChatId(update), MessageLabel.IMPORT_FROM_VK.label)
                    .replyMarkup(buttonManager.getRegularButtons())
            )
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

    private suspend fun handleListOfFriends(update: Update) {
        sender.send(
            SendMessage(getChatId(update), users.getOrCreateUser(getChatId(update)).getFriendList())
                .replyMarkup(buttonManager.getListOfFriendsButtons())
        )
    }

    private suspend fun handleAddFriend(update: Update) {
        val user = users.getOrCreateUser(getChatId(update))
        user.state = UserState.ADDING_NEW_FRIEND
        users.updateUser(user)
        sender.send(
            SendMessage(getChatId(update), MessageLabel.ADD_FRIEND.label)
                .replyMarkup(buttonManager.getAddFriendButtons())
        )
    }

    private suspend fun handleRemoveFriend(update: Update) {
        val user = users.getOrCreateUser(getChatId(update))
        user.state = UserState.REMOVING_FRIEND
        users.updateUser(user)
        sender.send(
            SendMessage(getChatId(update), MessageLabel.REMOVE_FRIEND.label)
                .replyMarkup(buttonManager.getRemoveFriendButtons())
        )
    }

    override fun getChatId(update: Update): Long {
        return update.callbackQuery().message().chat().id()
    }
}