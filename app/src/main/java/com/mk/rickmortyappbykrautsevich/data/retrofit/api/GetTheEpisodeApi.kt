package com.mk.rickmortyappbykrautsevich.data.retrofit.api

import com.mk.rickmortyappbykrautsevich.data.retrofit.models.CharacterRetrofitModel
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.EpisodeRetrofitModel
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface GetTheEpisodeApi {

    @GET(value = "episode/{id}")
    fun getEpisode(
        @Path("id") id: Int
    ): Single<EpisodeRetrofitModel>

    @GET(value = "character/{array}")
    fun getList(
        @Path("array") array: String
    ): Single<List<CharacterRetrofitModel>>
}