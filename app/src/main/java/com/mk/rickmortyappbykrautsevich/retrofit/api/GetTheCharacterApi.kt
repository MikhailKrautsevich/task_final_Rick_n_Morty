package com.mk.rickmortyappbykrautsevich.retrofit.api

import com.mk.rickmortyappbykrautsevich.retrofit.models.CharacterRetrofitModel
import com.mk.rickmortyappbykrautsevich.retrofit.models.EpisodeRetrofitModel
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface GetTheCharacterApi {
    @GET(value = "character/{id}")
    fun getCharacter(
        @Path("id") id: Int
    ): Single<CharacterRetrofitModel>

    @GET(value = "episode/{array}")
    fun getList(
        @Path("array") array: String
    ): Single<List<EpisodeRetrofitModel>>
}
