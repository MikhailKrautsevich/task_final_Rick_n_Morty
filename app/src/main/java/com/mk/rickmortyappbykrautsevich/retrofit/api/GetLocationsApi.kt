package com.mk.rickmortyappbykrautsevich.retrofit.api

import com.mk.rickmortyappbykrautsevich.retrofit.models.AllLocationsContainer
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface GetLocationsApi {

    @GET(value = "location")
    fun getAllLocations(): Single<AllLocationsContainer>
}
