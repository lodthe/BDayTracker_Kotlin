package me.lodthe.bdaytracker.telegram.handlers

import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import com.vk.api.sdk.exceptions.ApiPrivateProfileException
import kotlinx.coroutines.coroutineScope
import me.lodthe.bdaytracker.database.BirthDate
import me.lodthe.bdaytracker.database.Friend
import me.lodthe.bdaytracker.database.UserState
import me.lodthe.bdaytracker.telegram.MessageLabel
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class MessageHandler(private val kodein: Kodein) : BaseHandler(kodein) {
    override suspend fun handle(update: Update) = coroutineScope {
        val trimmedText = update.message().text().trim()
        val user = users.getOrCreateUser(getChatId(update))
        when {
            trimmedText.startsWith("/start") -> handleStart(update)
            user.state == UserState.CHANGING_ID -> handleChangingId(update)
            user.state == UserState.ADDING_NEW_FRIEND -> handleAddingNewFriend(update)
            user.state == UserState.REMOVING_FRIEND -> handleRemovingFriend(update)
        }
    }

    private suspend fun handleChangingId(update: Update) {
        val id = update.message().text().toIntOrNull()
        if (id == null) {
            sendMessage(update, MessageLabel.UPDATE_VK_ID.label, buttonManager.getGetIdButtons())
        } else {
            val user = users.getOrCreateUser(getChatId(update))

            try {
                user.vkId = id
                user.updateVKFriends(vkBot.getFriendList(user.vkId!!)!!.items)
            } catch (e: ApiPrivateProfileException) {
                return sendMessage(update, MessageLabel.PROFILE_IS_CLOSED.label, buttonManager.getRegularButtons())
            }

            user.state = UserState.NONE
            users.updateUser(user)
            sendMessage(update, MessageLabel.ID_HAS_CHANGED.label, buttonManager.getRegularButtons())
        }
    }

    private suspend fun handleStart(update: Update) {
        sendMessage(update, MessageLabel.START.label, buttonManager.getStartButtons())
    }

    private suspend fun handleAddingNewFriend(update: Update) {
        val lines = update.message().text().split("\n")

        when {
            lines.size != 2 ->
                sendMessage(update, MessageLabel.ADD_FRIEND_WRONG_LINES_COUNT.label, buttonManager.getAddFriendButtons())

            BirthDate.fromString(lines[1]) == null ->
                sendMessage(update, MessageLabel.ADD_FRIEND_WRONG_DATE_FORMAT.label, buttonManager.getAddFriendButtons())

            else -> {
                val user = users.getOrCreateUser(getChatId(update))
                user.addFriend(Friend(name = lines[0], birthday = BirthDate.fromString(lines[1])))
                users.updateUser(user)
                sendMessage(update, MessageLabel.ADD_FRIEND_SUCCESS.label, buttonManager.getAddFriendsSuccessButtons())
            }
        }
    }

    private suspend fun handleRemovingFriend(update: Update) {
        val friendId = update.message().text().toIntOrNull()
        val user = users.getOrCreateUser(getChatId(update))
        user.state = UserState.NONE

        if ((friendId == null) or (friendId !in user.getUserFriendsSizeRange())) {
            sendMessage(update, MessageLabel.REMOVE_FRIEND_WRONG_FORMAT.label,
                buttonManager.getRemoveFriendWrongFormatButtons())
        } else {
            user.removeFriend(friendId!!)
            sendMessage(update, MessageLabel.REMOVE_FRIEND_SUCCESS.label, buttonManager.getRegularButtons())
        }

        users.updateUser(user)
    }
}