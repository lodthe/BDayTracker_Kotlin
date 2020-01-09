package me.lodthe.bdaytracker

import me.lodthe.bdaytracker.database.BirthDate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneId

val ZONE_ID: ZoneId = ZoneId.of("Europe/Moscow")

fun getCurrentDate(): BirthDate {
    val date = LocalDateTime.now().atZone(ZONE_ID)
    return BirthDate(date.dayOfMonth, date.month.value)
}


inline fun <reified T> getLogger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}