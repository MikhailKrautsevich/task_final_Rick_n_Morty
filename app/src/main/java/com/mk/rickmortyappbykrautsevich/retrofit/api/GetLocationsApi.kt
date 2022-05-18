package com.mk.rickmortyappbykrautsevich.retrofit.api

import android.support.annotation.IntRange
import com.mk.rickmortyappbykrautsevich.retrofit.models.AllLocationsContainer
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GetLocationsApi {

    @GET(value = "location")
    fun getLocations(
        @Query(value = "page") @IntRange(from = 1, to = 7) page: Int = 1,
        @Query(value = "name") name: String? = null,
        @Query(value = "type") type: String? = null,
        @Query(value = "dimension") dimension: String? = null,
    ): Single<AllLocationsContainer>
}
