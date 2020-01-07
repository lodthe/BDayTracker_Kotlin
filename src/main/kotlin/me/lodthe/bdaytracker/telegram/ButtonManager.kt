package me.lodthe.bdaytracker.telegram

import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import org.kodein.di.Kodein

class ButtonManager(private val kodein: Kodein) {
    fun getStartButtons() = InlineKeyboardMarkup(
        getOnlyAddButtons(),
        arrayOf(
            getInlineButton(ButtonLabel.MENU)
        )
    )

    fun getMenuButtons() = InlineKeyboardMarkup(
        getOnlyAddButtons(),
        arrayOf(
            getInlineButton(ButtonLabel.LIST_OF_FRIENDS)
        ),
        arrayOf(
            getInlineButton(ButtonLabel.UPDATE_VK_ID)
        )
    )

    fun getRegularButtons() = InlineKeyboardMarkup(
        getOnlyAddButtons(),
        arrayOf(
            getInlineButton(ButtonLabel.LIST_OF_FRIENDS)
        ),
        arrayOf(
            getInlineButton(ButtonLabel.UPDATE_VK_ID)
        ),
        arrayOf(
            getInlineButton(ButtonLabel.MENU)
        )
    )

    fun getGetIdButtons() = InlineKeyboardMarkup(
        arrayOf(
            getInlineButton(ButtonLabel.GET_ID).url(TextLabel.GET_ID.label)
        )
    )

    fun getAddFriendButtons() = InlineKeyboardMarkup(
        arrayOf(
            getInlineButton(ButtonLabel.MENU)
        )
    )

    fun getAddFriendsSuccessButtons() = InlineKeyboardMarkup(
        arrayOf(
            getInlineButton(ButtonLabel.LIST_OF_FRIENDS)
        ),
        arrayOf(
            getInlineButton(ButtonLabel.MENU)
        )
    )

    fun getListOfFriendsButtons() = InlineKeyboardMarkup(
        getOnlyRemoveButtons(),
        getOnlyAddButtons(),
        arrayOf(
            getInlineButton(ButtonLabel.MENU)
        )
    )

    fun getRemoveFriendButtons() = InlineKeyboardMarkup(
        arrayOf(
            getInlineButton(ButtonLabel.LIST_OF_FRIENDS)
        ),
        arrayOf(
            getInlineButton(ButtonLabel.MENU)
        )
    )

    fun getRemoveFriendWrongFormatButtons() = InlineKeyboardMarkup(
        arrayOf(
            getInlineButton(ButtonLabel.LIST_OF_FRIENDS)
        ),
        getOnlyRemoveButtons(),
        arrayOf(
            getInlineButton(ButtonLabel.MENU)
        )
    )

    private fun getOnlyRemoveButtons() = arrayOf(
        getInlineButton(ButtonLabel.REMOVE_FRIEND)
    )

    private fun getOnlyAddButtons() = arrayOf(
        getInlineButton(ButtonLabel.ADD_FRIEND),
        getInlineButton(ButtonLabel.IMPORT_FROM_VK)
    )

    private fun getInlineButton(label: ButtonLabel): InlineKeyboardButton {
        return InlineKeyboardButton(label.label).callbackData(label.label)
    }
}