package me.lodthe.bdaytracker.database

import me.lodthe.bdaytracker.vk.KVKBot
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import kotlinx.coroutines.coroutineScope

class UsersManager(kodein: Kodein) {
    private val db: CoroutineDatabase by kodein.instance()
    private val users = db.getCollection<DatabaseUser>()

    suspend fun getOrCreateUser(telegramId: Long): DatabaseUser {
        return users.findOne(DatabaseUser::telegramId eq telegramId) ?: suspend {
            users.insertOne(DatabaseUser(telegramId))
            DatabaseUser(telegramId)
        }.invoke()
    }

    suspend fun updateUser(user: DatabaseUser): Unit = coroutineScope {
        users.deleteOne(DatabaseUser::telegramId eq user.telegramId)
        users.insertOne(user)
        Unit
    }
}