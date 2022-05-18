package com.mk.rickmortyappbykrautsevich.data.retrofit.api

import com.mk.rickmortyappbykrautsevich.data.retrofit.models.CharacterRetrofitModel
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.LocationRetrofitModel
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface GetTheLocationApi {

    @GET(value = "location/{id}")
    fun getLocation(
        @Path("id") id: Int
    ): Single<LocationRetrofitModel>

    @GET(value = "character/{array}")
    fun getList(
        @Path("array") array: String
    ): Single<List<CharacterRetrofitModel>>
}