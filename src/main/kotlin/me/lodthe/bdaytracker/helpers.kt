package me.lodthe.bdaytracker

const val NO_BDATE_DATA_MESSAGE = "Нет информации"

fun parseDateFromString(str: String?): String {
    return when (str) {
        null -> NO_BDATE_DATA_MESSAGE
        else -> {
            val numbers = str
                .split(".")
                .map { it.padStart(2, '0') }
                .take(2)
            when (numbers.size) {
                2 -> numbers.joinToString(separator = ".") { it }
                else -> NO_BDATE_DATA_MESSAGE
            }
        }
    }
}