package me.lodthe.bdaytracker.telegram

import com.pengrad.telegrambot.TelegramException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import me.lodthe.bdaytracker.database.BirthDate
import me.lodthe.bdaytracker.database.UsersManager
import me.lodthe.bdaytracker.getCurrentDate
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.time.LocalDateTime
import java.time.ZoneId

class TelegramNotificationController(private val kodein: Kodein) {
    private val users: UsersManager by kodein.instance()
    private val smartMessageController: SmartTelegramMessageRequestsController by kodein.instance()
    private val buttonManager: ButtonManager by kodein.instance()

    suspend fun run() = coroutineScope {
        while (true) {
            val currentHour = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).hour
            val currentMinute = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).minute
            if ((currentHour == BIRTHDAY_NOTIFICATION_HOUR) and (currentMinute == BIRTHDAY_NOTIFICATION_MINUTE)) {
                val currentDate: BirthDate = getCurrentDate()

                users.getAllUsers().consumeEach { user ->
                    val birthdayList = user
                        .friends
                        .filter {
                            it.birthday == currentDate
                        }
                        .joinToString(separator = "\n") {
                            it.getNameWithVKURL()
                        }

                    try {
                        if (birthdayList.isNotEmpty()) {
                            smartMessageController.sendMessage(
                                user.telegramId,
                                "${MessageLabel.FRIENDS_TO_CONGRATULATE_LIST.label}${birthdayList}",
                                buttonManager.getNotificateButtons(),
                                priority = 1
                            )
                        }
                    } catch (e: TelegramException) {
                        TODO("Fix something here")
                    }
                }
            }

            delay(DELAY_BETWEEN_DATE_CHECKS)
        }
    }

    companion object {
        const val BIRTHDAY_NOTIFICATION_HOUR = 1
        const val BIRTHDAY_NOTIFICATION_MINUTE = 45
        const val DELAY_BETWEEN_DATE_CHECKS = 30_000L
    }
}