package me.lodthe.bdaytracker.telegram.handlers

import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.AnswerCallbackQuery
import com.vk.api.sdk.exceptions.ApiException
import kotlinx.coroutines.coroutineScope
import me.lodthe.bdaytracker.database.UserState
import me.lodthe.bdaytracker.getLogger
import me.lodthe.bdaytracker.telegram.ButtonLabel
import me.lodthe.bdaytracker.telegram.MessageLabel
import org.kodein.di.Kodein

class CallbackHandler(kodein: Kodein) : BaseHandler(kodein) {
    override fun getChatId(update: Update): Long {
        return update.callbackQuery().message().chat().id()
    }

    override fun getMessageFromUpdate(update: Update): Message {
        return update.callbackQuery().message()
    }

    override suspend fun handle(update: Update): Unit = coroutineScope {
        val callbackData = update.callbackQuery().data()
        logger.info("${getChatId(update)} sent callback query: ${callbackData}")

        when {
            callbackData == ButtonLabel.MENU.label -> {
                sendUserRequest(MessageLabel.MENU.toString(), update)
                handleMenu(update)
            }
            callbackData == ButtonLabel.IMPORT_FROM_VK.label -> {
                sendUserRequest(MessageLabel.IMPORT_FROM_VK.toString(), update)
                handleImportFromVK(update)
            }
            callbackData == ButtonLabel.UPDATE_VK_ID.label -> {
                sendUserRequest(MessageLabel.UPDATE_VK_ID.toString(), update)
                handleUpdateVkId(update)
            }
            callbackData == ButtonLabel.ADD_FRIEND.label -> {
                sendUserRequest(MessageLabel.ADD_FRIEND.toString(), update)
                handleAddFriend(update)
            }
            callbackData == ButtonLabel.CANCEL_ADDING_FRIEND.label -> {
                sendUserRequest(MessageLabel.CANCEL_ADDING_FRIEND.toString(), update)
                handleCancelAddingFriend(update)
            }
            callbackData == ButtonLabel.REMOVE_FRIEND.label -> {
                sendUserRequest(MessageLabel.REMOVE_FRIEND.toString(), update)
                handleRemoveFriend(update)
            }
            callbackData == ButtonLabel.HELP.label -> {
                sendUserRequest(MessageLabel.HELP.toString(), update)
                handleHelp(update)
            }
            callbackData.startsWith(ButtonLabel.LIST_OF_FRIENDS.label) -> {
                sendUserRequest(MessageLabel.LIST_OF_FRIENDS.toString(), update)
                handleListOfFriends(update)
            }
            else ->
                logger.error("Couldn't recognize the callback sent by ${getChatId(update)}")
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
            } catch (e: ApiException) {
                MessageHandler.logger.info("Couldn't parse user's friend list", e)
                return sendMessage(update, MessageLabel.CANNOT_PARSE_FRIENDS.label,
                    buttonManager.getCannotParseFriendsButtons())
            }
            users.updateUser(user)
            sendMessage(update, MessageLabel.IMPORT_FROM_VK.label, buttonManager.getImportFromVKButtons())
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
        sendMessage(update, MessageLabel.FRIEND_NAME.label, buttonManager.getFriendNameButtons())
    }

    private suspend fun handleCancelAddingFriend(update: Update) {
        val user = users.getOrCreateUser(getChatId(update))
        user.state = UserState.NONE
        users.updateUser(user)
        sendMessage(update, MessageLabel.CANCEL_ADDING_FRIEND.label, buttonManager.getCancelAddingFriendButtons())
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

    companion object {
        val logger = getLogger<CallbackHandler>()
    }
}