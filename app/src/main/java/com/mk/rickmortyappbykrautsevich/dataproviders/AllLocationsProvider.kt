package com.mk.rickmortyappbykrautsevich.dataproviders

import com.mk.rickmortyappbykrautsevich.retrofit.RetrofitHelper

import com.mk.rickmortyappbykrautsevich.retrofit.api.GetLocationsApi
import com.mk.rickmortyappbykrautsevich.retrofit.models.AllLocationsContainer
import io.reactivex.rxjava3.core.Single

class AllLocationsProvider {
    var api: GetLocationsApi? = null

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getLocsApi(retrofit)
    }

    fun loadAllLocations(): Single<AllLocationsContainer>? = api?.getAllLocations()
}