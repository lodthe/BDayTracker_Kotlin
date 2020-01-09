package me.lodthe.bdaytracker.telegram

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import me.lodthe.bdaytracker.ZONE_ID
import me.lodthe.bdaytracker.database.BirthDate
import me.lodthe.bdaytracker.database.UsersManager
import me.lodthe.bdaytracker.getCurrentDate
import me.lodthe.bdaytracker.getLogger
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.time.LocalDateTime

class TelegramNotificationController(kodein: Kodein) {
    private val users: UsersManager by kodein.instance()
    private val smartMessageController: SmartTelegramMessageRequestsController by kodein.instance()
    private val buttonManager: ButtonManager by kodein.instance()
    private val latestNotificationDate: BirthDate? = null

    suspend fun run() = coroutineScope {
        logger.info("Telegram notificator was started")
        while (true) {
            val currentHour = LocalDateTime.now().atZone(ZONE_ID).hour
            val currentDate: BirthDate = getCurrentDate()

            if ((currentHour == BIRTHDAY_NOTIFICATION_HOUR) and (latestNotificationDate != currentDate)) {
                users.getAllUsers().consumeEach { user ->
                    val birthdayList = user.getFriendsNamesToCongratulate(currentDate)

                    try {
                        if (birthdayList.isNotEmpty()) {
                            logger.info("""
                                Sending notification about congratulating friends to ${user.telegramId}: 
                                ${MessageLabel.FRIENDS_TO_CONGRATULATE_LIST.label.format(birthdayList)}
                            """.trimIndent())

                            smartMessageController.sendMessage(
                                user.telegramId,
                                MessageLabel.FRIENDS_TO_CONGRATULATE_LIST.label.format(birthdayList),
                                buttonManager.getNotificationButtons(),
                                priority = 1
                            )
                        }
                    } catch (e: Exception) {
                        logger.error(e.stackTrace.toString())
                    }
                }
            }

            delay(DELAY_BETWEEN_DATE_CHECKS)
        }
    }

    companion object {
        const val BIRTHDAY_NOTIFICATION_HOUR = 1
        const val DELAY_BETWEEN_DATE_CHECKS = 60_000L
        val logger = getLogger<TelegramNotificationController>()
    }
}