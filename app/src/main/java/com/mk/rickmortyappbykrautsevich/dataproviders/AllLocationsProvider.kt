package com.mk.rickmortyappbykrautsevich.dataproviders

import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.LocationRecData
import com.mk.rickmortyappbykrautsevich.retrofit.RetrofitHelper

import com.mk.rickmortyappbykrautsevich.retrofit.api.GetLocationsApi
import com.mk.rickmortyappbykrautsevich.retrofit.models.AllLocationsContainer
import com.mk.rickmortyappbykrautsevich.retrofit.models.LocationRetrofitModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class AllLocationsProvider {
    private var api: GetLocationsApi? = null
    private var currentPageNumber = 1
    private val maxPageNumber = 7

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getLocsApi(retrofit)
    }

    fun loadAllLocations(): Single<List<LocationRecData>>? {
        val single = api?.getAllLocations()
        return handleSingle(single)
    }

    fun loadNewPage(): Single<List<LocationRecData>>? {
        return if (hasMoreData()) {
            val single = api?.getAllLocations(page = ++currentPageNumber)
            handleSingle(single)
        } else Single.just(emptyList())
    }

    private fun handleSingle(single: Single<AllLocationsContainer>?): Single<List<LocationRecData>>? {
        val result: Single<List<LocationRecData>>? =
            single?.subscribeOn(Schedulers.io())?.flatMap { t -> Single.just(t.results) }
                ?.flatMap { t ->
                    Single.just(
                        transformRepoLocsListInRecLocsList(t)
                    )
                }?.subscribeOn(Schedulers.computation())
        return result
    }

    private fun transformRepoLocsListInRecLocsList(list: List<LocationRetrofitModel>?): List<LocationRecData> {
        val l = ArrayList<LocationRecData>()
        list?.forEach { l.add(LocationRecData(it)) }
        return l
    }

    fun hasMoreData(): Boolean = currentPageNumber + 1 <= maxPageNumber
}