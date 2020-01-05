package me.lodthe.bdaytracker.database

import com.vk.api.sdk.objects.friends.UserXtrLists
import com.vk.api.sdk.objects.users.UserFull
import me.lodthe.bdaytracker.parseDateFromString

data class Friend(
    val id: Int? = null,
    val name: String,
    var bdate: String?
)

data class DatabaseUser(val telegramId: Long) {
    var vkId: Int? = null
    var state: UserState = UserState.NONE
    val friends = mutableSetOf<Friend>()

    fun addFriend(friend: Friend) {
        friends.add(friend)
    }

    fun getFriendList(): String = friends.joinToString(separator = "\n") {
        "`${it.name}` — ${it.bdate}"
    }

    fun removeAllVKFriends() {
        friends.removeIf { it.id != null }
    }

    fun updateVKFriends(newFriends: List<UserXtrLists>) {
        removeAllVKFriends()
        newFriends.forEach {
            addFriend(
                Friend(
                    id = it.id,
                    name = "${it.firstName} ${it.lastName}",
                    bdate = parseDateFromString(it.bdate)
                )
            )
        }
    }
}