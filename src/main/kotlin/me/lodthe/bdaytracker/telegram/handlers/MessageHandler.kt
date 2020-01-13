package me.lodthe.bdaytracker.telegram.handlers

import com.pengrad.telegrambot.model.Update
import com.vk.api.sdk.exceptions.ApiException
import kotlinx.coroutines.coroutineScope
import me.lodthe.bdaytracker.database.BirthDate
import me.lodthe.bdaytracker.database.Friend
import me.lodthe.bdaytracker.database.UserState
import me.lodthe.bdaytracker.getLogger
import me.lodthe.bdaytracker.telegram.MessageLabel
import org.kodein.di.Kodein

class MessageHandler(kodein: Kodein) : BaseHandler(kodein) {
    override suspend fun handle(update: Update) = coroutineScope {
        val trimmedText = update.message().text().trim()
        val user = users.getOrCreateUser(getChatId(update))
        logger.info("${getChatId(update)} sent message: ${trimmedText}")

        when {
            trimmedText.startsWith("/start") -> {
                sendUserRequest(UserState.START.toString(), update)
                handleStart(update)
            }
            user.state == UserState.CHANGING_ID -> {
                sendUserRequest(UserState.CHANGING_ID.toString(), update)
                handleChangingId(update)
            }
            user.state == UserState.ADDING_NEW_FRIEND -> {
                sendUserRequest(UserState.ADDING_NEW_FRIEND.toString(), update)
                handleAddingNewFriend(update)
            }
            user.state == UserState.REMOVING_FRIEND -> {
                sendUserRequest(UserState.REMOVING_FRIEND.toString(), update)
                handleRemovingFriend(update)
            }
            else -> {
                sendUserRequest(null, update)
                handleWrongCommand(update)
            }
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
            } catch (e: ApiException) {
                logger.info("Couldn't parse user's friend list", e)
                return sendMessage(update, MessageLabel.PROFILE_IS_CLOSED.label, buttonManager.getChangeIdButtons())
            }

            user.state = UserState.NONE
            users.updateUser(user)
            sendMessage(update, MessageLabel.ID_HAS_CHANGED.label.format(id), buttonManager.getChangeIdButtons())
        }
    }

    private suspend fun handleStart(update: Update) {
        sendMessage(update, MessageLabel.START.label, buttonManager.getStartButtons())
    }

    private suspend fun handleAddingNewFriend(update: Update, chatId: Long = getChatId(update)) {
        val text = update.message().text()
        val user = users.getOrCreateUser(chatId)
        val friendToAdd = user.friendToAdd ?: Friend()
        user.friendToAdd = friendToAdd

        when {
            friendToAdd.name == null -> {
                if ((text.length > MAX_NAME_SIZE) || text.any { it == '\n' } ) {
                    sendMessage(update, MessageLabel.WRONG_FRIEND_NAME_FORMAT.label.format(MAX_NAME_SIZE),
                        buttonManager.getWrongFriendNameFormatButtons())
                } else {
                    friendToAdd.name = text
                    sendMessage(update, MessageLabel.FRIEND_BIRTHDATE.label.format(friendToAdd.getNameWithVKURL()),
                        buttonManager.getFriendBirthdateButtons())
                }
            }

            friendToAdd.birthday == null -> {
                if (BirthDate.fromString(text) == null) {
                    sendMessage(update, MessageLabel.WRONG_DATE_FORMAT.label,
                        buttonManager.getWrongDateFormatButtons())

                } else {
                    friendToAdd.birthday = BirthDate.fromString(text)
                    user.state = UserState.NONE
                    user.addFriend(friendToAdd)
                    user.friendToAdd = null
                    sendMessage(update, MessageLabel.ADD_FRIEND_SUCCESS.label,
                        buttonManager.getAddFriendsSuccessButtons())
                }
            }
        }

        users.updateUser(user)
    }

    private suspend fun handleRemovingFriend(update: Update) {
        val friendId = update.message().text().toIntOrNull()
        val user = users.getOrCreateUser(getChatId(update))
        user.state = UserState.NONE

        if ((friendId == null) or (friendId !in user.getUserFriendsSizeRange())) {
            sendMessage(update, MessageLabel.REMOVE_FRIEND_WRONG_FORMAT.label,
                buttonManager.getRemoveFriendWrongFormatButtons())
        } else {
            sendMessage(
                update,
                MessageLabel.REMOVE_FRIEND_SUCCESS.label.format(user.removeFriend(friendId!!).getNameWithVKURL()),
                buttonManager.getRegularButtons()
            )
        }

        users.updateUser(user)
    }

    private suspend fun handleWrongCommand(update: Update) {
        sendMessage(update, MessageLabel.WRONG_COMMAND.label, buttonManager.getWrongCommandButtons())
    }

    companion object {
        val logger = getLogger<MessageHandler>()
        const val MAX_NAME_SIZE = 100
    }
}