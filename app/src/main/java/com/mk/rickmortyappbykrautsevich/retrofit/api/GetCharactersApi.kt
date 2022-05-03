package com.mk.rickmortyappbykrautsevich.retrofit.api

import com.mk.rickmortyappbykrautsevich.retrofit.models.AllCharactersContainer
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface GetCharactersApi {

    @GET(value = "character")
    fun getAllCharacters(): Single<AllCharactersContainer>
}