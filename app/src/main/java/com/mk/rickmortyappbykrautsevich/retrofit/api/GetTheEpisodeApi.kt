package com.mk.rickmortyappbykrautsevich.retrofit.api

import com.mk.rickmortyappbykrautsevich.retrofit.models.EpisodeRetrofitModel
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface GetTheEpisodeApi {

    @GET(value = "episode/{id}")
    fun getEpisode(
        @Path("id") id: Int
    ): Single<EpisodeRetrofitModel>
}