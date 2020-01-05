package me.lodthe.bdaytracker.vk

import com.vk.api.sdk.client.Lang
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.client.actors.ServiceActor
import com.vk.api.sdk.objects.friends.responses.GetFieldsResponse
import com.vk.api.sdk.objects.friends.responses.GetResponse
import com.vk.api.sdk.objects.users.Fields
import com.vk.api.sdk.objects.users.UserXtrCounters

class KVKBot(private val APP_ID: Int, private val APP_TOKEN: String) {
    private val vk = VkApiClient(HttpTransportClient())
    private val actor = ServiceActor(APP_ID, APP_TOKEN)

    fun getFriendList(userId: Int): GetFieldsResponse? {
        return vk.friends().getWithFields(actor, Fields.BDATE).userId(userId).lang(Lang.RU).execute()
    }
}