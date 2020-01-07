package me.lodthe.bdaytracker

import me.lodthe.bdaytracker.database.BirthDate
import java.time.LocalDateTime
import java.time.ZoneId

fun getCurrentDate(): BirthDate {
    val date = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow"))
    return BirthDate(date.dayOfMonth, date.month.value)
}