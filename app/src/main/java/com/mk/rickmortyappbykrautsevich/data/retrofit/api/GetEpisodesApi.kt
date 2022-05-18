package com.mk.rickmortyappbykrautsevich.data.retrofit.api

import android.support.annotation.IntRange
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.AllEpisodesContainer
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GetEpisodesApi {

    @GET(value = "episode")
    fun getEpisodes(
        @Query(value = "page") @IntRange(from = 1, to = 3) page: Int = 1,
        @Query(value = "name") name: String? = null,
        @Query(value = "episode") episode: String? = null,
    ): Single<AllEpisodesContainer>
}