package me.lodthe.bdaytracker.vk

import com.vk.api.sdk.client.Lang
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.ServiceActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.objects.friends.responses.GetFieldsResponse
import com.vk.api.sdk.objects.users.Fields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KVKBot(private val APP_ID: Int, private val APP_TOKEN: String) {
    private val vk = VkApiClient(HttpTransportClient())
    private val actor = ServiceActor(APP_ID, APP_TOKEN)

    suspend fun getFriendList(userId: Int): GetFieldsResponse? = withContext(Dispatchers.IO) {
        vk.friends().getWithFields(actor, Fields.BDATE).userId(userId).lang(Lang.RU).execute()
    }

    suspend fun getIdFromPageUrl(url: String)= withContext(Dispatchers.IO) {
        var nickname = url.takeLastWhile { it != '/' }
        if (nickname.isEmpty()) {
            nickname = ":("
        }

        vk.users().get(actor).userIds(nickname).execute()[0].id
    }
}