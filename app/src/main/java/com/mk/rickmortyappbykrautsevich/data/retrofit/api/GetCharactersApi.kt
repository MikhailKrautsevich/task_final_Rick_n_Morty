package com.mk.rickmortyappbykrautsevich.data.retrofit.api

import android.support.annotation.IntRange
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.AllCharactersContainer
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GetCharactersApi {

    @GET(value = "character")
    fun getCharacters(
        @Query(value = "page") @IntRange(from = 1, to = 42) page: Int = 1,
        @Query(value = "name") name: String? = null,
        @Query(value = "status") status: String? = null,
        @Query(value = "species") species: String? = null,
        @Query(value = "type") type: String? = null,
        @Query(value = "gender") gender: String? = null,
    ): Single<AllCharactersContainer>
}