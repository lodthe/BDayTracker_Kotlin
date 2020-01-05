package me.lodthe.bdaytracker.telegram

import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import org.kodein.di.Kodein

class ButtonManager(private val kodein: Kodein) {
    fun getStartButtons() = InlineKeyboardMarkup(
        getAddButtons(),
        arrayOf(
            getInlineButton(ButtonLabel.MENU)
        )
    )

    fun getMenuButtons() = InlineKeyboardMarkup(
        getAddButtons(),
        arrayOf(
            getInlineButton(ButtonLabel.LIST_OF_USERS)
        ),
        arrayOf(
            getInlineButton(ButtonLabel.UPDATE_VK_ID)
        )
    )

    fun getRegularButtons() = InlineKeyboardMarkup(
        getAddButtons(),
        arrayOf(
            getInlineButton(ButtonLabel.LIST_OF_USERS)
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

    private fun getAddButtons() = arrayOf(
        getInlineButton(ButtonLabel.ADD_DATE),
        getInlineButton(ButtonLabel.IMPORT_FROM_VK)
    )

    private fun getInlineButton(label: ButtonLabel): InlineKeyboardButton {
        return InlineKeyboardButton(label.label).callbackData(label.label)
    }
}