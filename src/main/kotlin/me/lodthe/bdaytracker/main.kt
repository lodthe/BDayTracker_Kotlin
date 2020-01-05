package me.lodthe.bdaytracker

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import kotlinx.coroutines.coroutineScope
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
    bind<KVKBot>() with singleton {
        KVKBot(System.getenv("VK_APP_ID").toInt(), System.getenv("VK_APP_TOKEN"))
    }

    bind<KTelegramBot>() with singleton {
        KTelegramBot(System.getenv("TELEGRAM_BOT_TOKEN"))
    }
    bind<PriorityTelegramRequestSender>() with singleton { PriorityTelegramRequestSender(kodein) }
    bind<MessageSender>() with singleton { MessageSender(kodein) }
    bind<MessageHandler>() with singleton { MessageHandler(kodein) }
    bind<CallbackHandler>() with singleton { CallbackHandler(kodein) }
    bind<ButtonManager>() with singleton { ButtonManager(kodein) }

    bind<CoroutineDatabase>() with singleton {
        val credential = MongoCredential.createCredential("root", "admin", "root".toCharArray())
        val settings = MongoClientSettings.builder()
            .credential(credential).build()
        KMongo.createClient(settings).coroutine
            .getDatabase("bdaytracker")
    }
    bind<UsersManager>() with singleton { UsersManager(kodein) }
}

suspend fun runControllers(kodein: Kodein) = coroutineScope {
    val telegramController = TelegramController(kodein)
    val queriesSender: PriorityTelegramRequestSender by kodein.instance()
    launch { telegramController.run() }
    launch { queriesSender.run() }
}

suspend fun main() {
    runControllers(kodein)
}