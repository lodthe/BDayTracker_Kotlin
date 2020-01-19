package me.lodthe.bdaytracker

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.lodthe.bdaytracker.database.UsersManager
import me.lodthe.bdaytracker.telegram.*
import me.lodthe.bdaytracker.telegram.handlers.CallbackHandler
import me.lodthe.bdaytracker.telegram.handlers.MessageHandler
import me.lodthe.bdaytracker.vk.KVKBot
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val kodein = Kodein {
    bind<ChatBaseAPI>() with singleton { ChatBaseAPI(System.getenv("CHATBASE_API_KEY")) }

    bind<KVKBot>() with singleton {
        KVKBot(System.getenv("VK_APP_ID").toInt(), System.getenv("VK_APP_TOKEN"))
    }

    bind<KTelegramBot>() with singleton {
        KTelegramBot(System.getenv("TELEGRAM_BOT_TOKEN"))
    }
    bind<TelegramNotificationController>() with singleton { TelegramNotificationController(kodein) }
    bind<PriorityTelegramRequestSender>() with singleton { PriorityTelegramRequestSender(kodein) }
    bind<SmartTelegramMessageRequestsController>() with singleton { SmartTelegramMessageRequestsController(kodein) }
    bind<MessageHandler>() with singleton { MessageHandler(kodein) }
    bind<CallbackHandler>() with singleton { CallbackHandler(kodein) }
    bind<ButtonManager>() with singleton { ButtonManager(kodein) }

    bind<CoroutineDatabase>() with singleton {
        KMongo.createClient(System.getenv("MONGO_CONNECTION_STRING")).coroutine
            .getDatabase(System.getenv("MONGO_DATABASE"))
    }
    bind<UsersManager>() with singleton { UsersManager(kodein) }
}

suspend fun runControllers(kodein: Kodein) = coroutineScope {
    val telegramController = TelegramUpdatesController(kodein)
    val queriesSender: PriorityTelegramRequestSender by kodein.instance()
    val notificator: TelegramNotificationController by kodein.instance()
    launch { telegramController.run() }
    launch { queriesSender.run() }
    launch { notificator.run() }
    launch {
        delay(1000 * 1 * 60 * 60)
        println("It's time to restart!")
    }
}

suspend fun main() {
    runControllers(kodein)
}