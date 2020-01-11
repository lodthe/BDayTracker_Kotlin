package me.lodthe.bdaytracker.database

import com.fasterxml.jackson.annotation.JsonIgnore
import com.vk.api.sdk.objects.friends.UserXtrLists
import me.lodthe.bdaytracker.getCurrentDate
import me.lodthe.bdaytracker.telegram.TextLabel
import kotlin.math.max
import kotlin.math.min

enum class UserState {
    NONE,
    START,
    CHANGING_ID,
    ADDING_NEW_FRIEND,
    REMOVING_FRIEND
}

data class Friend(
    val id: Int? = null,
    val name: String,
    var birthday: BirthDate?
) {
    @JsonIgnore
    fun getNameWithVKURL(): String {
        return if (id == null) "`$name`" else "[$name](https://vk.com/id$id)"
    }

    @JsonIgnore
    fun getRepresentationWithIndex(index: Int, countOfRadix: Int): String {
        val indexRepresentation = (index + 1).toString().padStart(countOfRadix, '0')
        return "*$indexRepresentation*. ${getNameWithVKURL()} — ${getBirthdayRepresentation()}"
    }

    @JsonIgnore
    private fun getBirthdayRepresentation() = "${birthday ?: TextLabel.NO_BIRTHDATE_DATA.label}"
}

data class DatabaseUser(val telegramId: Long) {
    var vkId: Int? = null
    var state: UserState = UserState.NONE
    @Suppress
    val friends = mutableSetOf<Friend>()

    @JsonIgnore
    fun getUserFriendsSizeRange() = IntRange(1, friends.size)

    fun fixOffsetValue(offset: Int) = max(0, min(offset, friends.size - FRIEND_LIST_PAGE_SIZE))

    fun addFriend(friend: Friend) {
        friends.add(friend)
    }

    @JsonIgnore
    fun getSortedFriendList() = friends.toList().sortedBy { BirthDate.getDayOfYear(it.birthday) }

    /**
     * @param offset describes index of the first friend in sorted list
     * @param take describes max count of friends in list should be added to the result
     * @return string with range described by offset and take friends representation like this:
     * 001. John Newman -- 05.10
     * 002. Nelson M -- 12.04
     * ...
     */
    fun getFriendList(offset: Int = getNearestBirthdayId(), take: Int = FRIEND_LIST_PAGE_SIZE): String {
        val countOfRadix = friends.size.toString().length
        val response = getSortedFriendList()
            .mapIndexed { index, friend -> friend.getRepresentationWithIndex(index, countOfRadix) }
            .drop(max(offset, 0))
            .take(max(take, 0))
            .joinToString(separator = "\n")

        return if (response.isNotEmpty()) response else TextLabel.EMPTY_FRIEND_LIST.label
    }

    /**
     * @return friends names who have birthday in day which is equal to date
     */
    fun getFriendsNamesToCongratulate(date: BirthDate): String {
        return friends
            .filter {
                it.birthday == date
            }
            .joinToString(separator = "\n") {
                it.getNameWithVKURL()
            }
    }

    /**
     * Remove friend in sorted friend list by it's index and return it
     */
    fun removeFriend(id: Int): Friend {
        return getSortedFriendList()[id - 1].also { friends.remove(it) }
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

    /**
     * @return first friend in sorted friends list whose birthday day of year number is greater or equal to given date,
     * and move offset by 1/6 length of FRIEND_LIST_PAGE_SIZE to see friends in more convenient way
     */
    fun getNearestBirthdayId(date: BirthDate = getCurrentDate()): Int {
        return getSortedFriendList().indexOfFirst {
            BirthDate.getDayOfYear(date) <= BirthDate.getDayOfYear(it.birthday)
        } - FRIEND_LIST_PAGE_SIZE / 6
    }

    companion object {
        const val FRIEND_LIST_PAGE_SIZE: Int = 20
    }
}