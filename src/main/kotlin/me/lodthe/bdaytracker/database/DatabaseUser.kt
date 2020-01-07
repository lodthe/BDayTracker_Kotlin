package me.lodthe.bdaytracker.database

import com.fasterxml.jackson.annotation.JsonIgnore
import com.vk.api.sdk.objects.friends.UserXtrLists
import me.lodthe.bdaytracker.getCurrentDate
import me.lodthe.bdaytracker.telegram.TextLabel
import kotlin.math.max

enum class UserState {
    NONE,
    CHANGING_ID,
    ADDING_NEW_FRIEND,
    REMOVING_FRIEND,
    WATCHING_FRIENDLIST
}

data class Friend(
    val id: Int? = null,
    val name: String,
    var birthday: BirthDate?
) {
    fun getNameWithVKURL(): String {
        return if (id == null) "`$name`" else "[$name](https://vk.com/id$id)"
    }
}

data class DatabaseUser(val telegramId: Long) {
    var vkId: Int? = null
    var state: UserState = UserState.NONE
    val friends = mutableSetOf<Friend>()

    fun getUserFriendsSizeRange() = IntRange(1, friends.size)

    fun addFriend(friend: Friend) {
        friends.add(friend)
    }

    @JsonIgnore
    fun getSortedFriendList() = friends.toList().sortedBy { BirthDate.getDayOfYear(it.birthday) }

    fun getFriendList(offset: Int = getNearestBirthdayId(), take: Int = FRIEND_LIST_PAGE_SIZE): String {
        val countOfRadix = friends.size.toString().length
        return getSortedFriendList()
            .mapIndexed { index, friend ->
                "*${(index + 1).toString().padStart(countOfRadix, '0')}*. " +
                    "${friend.getNameWithVKURL()} — ${friend.birthday ?: TextLabel.NO_BIRTHDATE_DATA.label}"
            }.drop(max(offset, 0))
            .take(take)
            .joinToString(separator = "\n")
    }

    fun removeFriend(id: Int) {
        friends.remove(getSortedFriendList()[id - 1])
    }

    private fun removeAllVKFriends() {
        friends.removeIf { it.id != null }
    }

    fun updateVKFriends(newFriends: List<UserXtrLists>) {
        removeAllVKFriends()
        newFriends.forEach {
            addFriend(
                Friend(
                    id = it.id,
                    name = "${it.firstName} ${it.lastName}",
                    birthday = BirthDate.fromString(it.bdate)
                )
            )
        }
    }

    fun getNearestBirthdayId(date: BirthDate = getCurrentDate()): Int {
        return getSortedFriendList().indexOfFirst {
            BirthDate.getDayOfYear(date) <= BirthDate.getDayOfYear(it.birthday)
        } - FRIEND_LIST_PAGE_SIZE / 6
    }

    companion object {
        const val FRIEND_LIST_PAGE_SIZE: Int = 20
    }
}