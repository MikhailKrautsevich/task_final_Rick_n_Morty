package com.mk.rickmortyappbykrautsevich.retrofit.api

import com.mk.rickmortyappbykrautsevich.retrofit.models.AllEpisodesContainer
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface GetEpisodesApi {

    @GET(value = "episode")
    fun getAllEpisodes(): Single<AllEpisodesContainer>
}