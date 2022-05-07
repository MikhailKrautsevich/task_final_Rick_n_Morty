package com.mk.rickmortyappbykrautsevich.retrofit.api

import android.support.annotation.IntRange
import com.mk.rickmortyappbykrautsevich.enums.Gender
import com.mk.rickmortyappbykrautsevich.enums.Status
import com.mk.rickmortyappbykrautsevich.retrofit.models.AllCharactersContainer
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GetCharactersApi {

    @GET(value = "character")
    fun getAllCharacters(
        @Query(value = "page") @IntRange(from = 1, to = 42) page: Int = 1,
        @Query(value = "name") name: String? = null,
        @Query(value = "status") status: Status? = null,
        @Query(value = "species") species: String? = null,
        @Query(value = "type") type: String? = null,
        @Query(value = "gender") gender: Gender? = null,
    ): Single<AllCharactersContainer>
}