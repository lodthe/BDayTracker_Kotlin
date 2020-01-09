package me.lodthe.bdaytracker.telegram

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import me.lodthe.bdaytracker.getLogger
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

data class TelegramRequest(
    val request: suspend () -> Unit,
    val priority: Int = 0
)

class PriorityTelegramRequestSender(private val kodein: Kodein, private val COUNT_OF_CHANNELS: Int = 2) {
    private val delayBetweenRequests = 1000L / 30 + 1
    private val channels = List<Channel<suspend () -> Unit>>(COUNT_OF_CHANNELS) { Channel<suspend () -> Unit>() }
    private val bot: KTelegramBot by kodein.instance()

    suspend fun run() = GlobalScope.launch {
        logger.info("Priority request sender was started")
        while (true) {
            launch { receive() }
            delay(delayBetweenRequests)
        }
    }

    suspend fun addRequest(request: TelegramRequest) {
        channels[request.priority].send( suspend {
            GlobalScope.launch {
                request.request.invoke()
            }
            Unit
        })
    }

    private suspend fun receive(): Unit = select {
        for (channel in channels) {
            channel.onReceive { it.invoke() }
        }
    }

    companion object {
        val logger = getLogger<PriorityTelegramRequestSender>()
    }
}