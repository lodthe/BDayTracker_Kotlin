package me.lodthe.bdaytracker.database

data class BirthDate(val day: Int, val month: Int) {
    fun getDayOfYear() = month * 31 + day

    override fun toString(): String {
        return "${day.toString().padStart(2, '0')}.${month.toString().padStart(2, '0')}"
    }

    operator fun compareTo(other: BirthDate) = getDayOfYear() - other.getDayOfYear()

    companion object {
        fun fromString(str: String?): BirthDate? = when (str) {
            null -> null
            else -> {
                val numbers = str.split(".").map { it.toInt() }
                when {
                    numbers.size < 2 -> null
                    numbers[0] !in (1..31) -> null
                    numbers[1] !in (1..12) -> null
                    else -> BirthDate(numbers[0], numbers[1])
                }
            }
        }

        fun getDayOfYear(date: BirthDate?) = when (date) {
            null -> 0
            else -> date.getDayOfYear()
        }
    }
}