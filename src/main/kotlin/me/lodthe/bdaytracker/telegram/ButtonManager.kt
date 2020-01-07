package me.lodthe.bdaytracker.telegram

import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import me.lodthe.bdaytracker.database.DatabaseUser
import org.kodein.di.Kodein

class ButtonManager(private val kodein: Kodein) {
    fun getStartButtons() = InlineKeyboardMarkup(
        getOnlyAddButtons(),
        getOnlyMenuButtons(),
        getOnlyHelpButtons()
    )

    fun getHelpButtons() = getStartButtons()

    fun getMenuButtons() = InlineKeyboardMarkup(
        getOnlyAddButtons(),
        getOnlyListOfFriendsButtons(),
        getOnlyUpdateVKIDButtons()
    )

    fun getRegularButtons() = InlineKeyboardMarkup(
        getOnlyAddButtons(),
        getOnlyListOfFriendsButtons(),
        getOnlyUpdateVKIDButtons(),
        getOnlyMenuButtons()
    )

    fun getGetIdButtons() = InlineKeyboardMarkup(
        getOnlyGetIdButtons(),
        getOnlyMenuButtons()
    )

    fun getAddFriendButtons() = InlineKeyboardMarkup(
        getOnlyMenuButtons()
    )

    fun getAddFriendsSuccessButtons() = InlineKeyboardMarkup(
        getOnlyListOfFriendsButtons(),
        getOnlyMenuButtons()
    )

    fun getListOfFriendsButtons(currentOffset: Int) = InlineKeyboardMarkup(
        getOnlyPaginationButtons(currentOffset),
        getOnlyRemoveButtons(),
        getOnlyAddButtons(),
        getOnlyMenuButtons()
    )

    fun getRemoveFriendButtons() = InlineKeyboardMarkup(
        getOnlyListOfFriendsButtons(),
        getOnlyMenuButtons()
    )

    fun getRemoveFriendWrongFormatButtons() = InlineKeyboardMarkup(
        getOnlyListOfFriendsButtons(),
        getOnlyRemoveButtons(),
        getOnlyMenuButtons()
    )

    fun getWrongCommandButtons() = InlineKeyboardMarkup(
        getOnlyHelpButtons(),
        getOnlyMenuButtons()
    )


    private fun getInlineButton(label: ButtonLabel, callback: String = label.label): InlineKeyboardButton {
        return InlineKeyboardButton(label.label).callbackData(callback)
    }

    private fun getOnlyPaginationButtons(currentOffset: Int) = arrayOf (
        getInlineButton(
            ButtonLabel.LIST_OF_FRIENDS_PREVIOUS_PAGE,
            "${ButtonLabel.LIST_OF_FRIENDS.label}#${currentOffset - DatabaseUser.FRIEND_LIST_PAGE_SIZE}"
        ),
        getInlineButton(
            ButtonLabel.LIST_OF_FRIENDS_NEXT_PAGE,
            "${ButtonLabel.LIST_OF_FRIENDS.label}#${currentOffset + DatabaseUser.FRIEND_LIST_PAGE_SIZE}"
        )
    )

    private fun getOnlyRemoveButtons() = arrayOf(
        getInlineButton(ButtonLabel.REMOVE_FRIEND)
    )

    private fun getOnlyAddButtons() = arrayOf(
        getInlineButton(ButtonLabel.ADD_FRIEND),
        getInlineButton(ButtonLabel.IMPORT_FROM_VK)
    )

    private fun getOnlyMenuButtons() = arrayOf(
        getInlineButton(ButtonLabel.MENU)
    )

    private fun getOnlyHelpButtons() = arrayOf(
        getInlineButton(ButtonLabel.HELP)
    )

    private fun getOnlyListOfFriendsButtons() = arrayOf(
        getInlineButton(ButtonLabel.LIST_OF_FRIENDS)
    )

    private fun getOnlyUpdateVKIDButtons() = arrayOf(
        getInlineButton(ButtonLabel.UPDATE_VK_ID)
    )

    private fun getOnlyGetIdButtons() = arrayOf(
        getInlineButton(ButtonLabel.GET_ID).url(TextLabel.GET_ID.label)
    )
}