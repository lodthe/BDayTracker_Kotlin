package me.lodthe.bdaytracker.telegram.handlers

import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.AnswerCallbackQuery
import com.vk.api.sdk.exceptions.ApiPrivateProfileException
import kotlinx.coroutines.coroutineScope
import me.lodthe.bdaytracker.database.UserState
import me.lodthe.bdaytracker.telegram.ButtonLabel
import me.lodthe.bdaytracker.telegram.MessageLabel
import org.kodein.di.Kodein

class CallbackHandler(private val kodein: Kodein) : BaseHandler(kodein) {
    override fun getChatId(update: Update): Long {
        return update.callbackQuery().message().chat().id()
    }

    override suspend fun handle(update: Update): Unit = coroutineScope {
        val callbackData = update.callbackQuery().data()
        when {
            callbackData == ButtonLabel.MENU.label -> handleMenu(update)
            callbackData == ButtonLabel.IMPORT_FROM_VK.label -> handleImportFromVK(update)
            callbackData == ButtonLabel.UPDATE_VK_ID.label -> handleUpdateVkId(update)
            callbackData == ButtonLabel.ADD_FRIEND.label -> handleAddFriend(update)
            callbackData == ButtonLabel.REMOVE_FRIEND.label -> handleRemoveFriend(update)
            callbackData == ButtonLabel.HELP.label -> handleHelp(update)
            callbackData.startsWith(ButtonLabel.LIST_OF_FRIENDS.label) -> handleListOfFriends(update)
        }

        bot.execute(AnswerCallbackQuery(update.callbackQuery().id()))
        Unit
    }

    private suspend fun handleMenu(update: Update) {
        sendMessage(update, MessageLabel.MENU.label, buttonManager.getMenuButtons())
    }

    private suspend fun handleImportFromVK(update: Update) {
        val user = users.getOrCreateUser(getChatId(update))
        if (user.vkId == null) {
            handleUpdateVkId(update)
        } else {
            try {
                user.updateVKFriends(vkBot.getFriendList(user.vkId!!)!!.items)
            } catch (e: ApiPrivateProfileException) {
                sendMessage(update, MessageLabel.PROFILE_IS_CLOSED.label, buttonManager.getRegularButtons())
            }

            users.updateUser(user)
            sendMessage(update, MessageLabel.IMPORT_FROM_VK.label, buttonManager.getRegularButtons())
        }
    }

    private suspend fun handleUpdateVkId(update: Update) {
        val user = users.getOrCreateUser(getChatId(update))
        user.state = UserState.CHANGING_ID
        users.updateUser(user)
        sendMessage(update, MessageLabel.UPDATE_VK_ID.label, buttonManager.getGetIdButtons())
    }

    private suspend fun handleListOfFriends(update: Update) {
        val user = users.getOrCreateUser(getChatId(update))
        val offset: Int = (
                update
                .callbackQuery().data()
                .split("#")
                .getOrNull(1)?.toInt() ?: user.getNearestBirthdayId()
            )
            .let {
                user.fixOffsetValue(it)
            }

        if (update.callbackQuery().data() == ButtonLabel.LIST_OF_FRIENDS.label) {
            sendMessage(update, user.getFriendList(offset), buttonManager.getListOfFriendsButtons(offset))
        } else if (update.callbackQuery().message().replyMarkup() != buttonManager.getListOfFriendsButtons(offset)) {
            editMessage(
                update.callbackQuery().message(),
                user.getFriendList(offset),
                buttonManager.getListOfFriendsButtons(offset)
            )
        }
    }

    private suspend fun handleAddFriend(update: Update) {
        val user = users.getOrCreateUser(getChatId(update))
        user.state = UserState.ADDING_NEW_FRIEND
        users.updateUser(user)
        sendMessage(update, MessageLabel.ADD_FRIEND.label, buttonManager.getAddFriendButtons())
    }

    private suspend fun handleRemoveFriend(update: Update) {
        val user = users.getOrCreateUser(getChatId(update))
        user.state = UserState.REMOVING_FRIEND
        users.updateUser(user)
        sendMessage(update, MessageLabel.REMOVE_FRIEND.label, buttonManager.getRemoveFriendButtons())
    }

    private suspend fun handleHelp(update: Update) {
        sendMessage(update, MessageLabel.HELP.label, buttonManager.getHelpButtons())
    }
}