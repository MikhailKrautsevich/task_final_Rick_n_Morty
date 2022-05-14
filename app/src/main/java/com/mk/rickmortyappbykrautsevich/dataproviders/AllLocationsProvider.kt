package com.mk.rickmortyappbykrautsevich.dataproviders

import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.LocationData
import com.mk.rickmortyappbykrautsevich.retrofit.RetrofitHelper

import com.mk.rickmortyappbykrautsevich.retrofit.api.GetLocationsApi
import com.mk.rickmortyappbykrautsevich.retrofit.models.AllLocationsContainer
import com.mk.rickmortyappbykrautsevich.retrofit.models.LocationRetrofitModel
import com.mk.rickmortyappbykrautsevich.retrofit.models.queries.LocationQuery
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class AllLocationsProvider {
    private var api: GetLocationsApi? = null
    private var currentPageNumber = 1

    // при запросе по умолчанию число страниц 7
    private var maxPageNumber = 7
    private var currentQuery: LocationQuery? = null

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getLocsApi(retrofit)
    }

    fun loadLocations(query: LocationQuery?): Single<List<LocationData>>? {
        currentPageNumber = 1
        currentQuery = query
        val single: Single<AllLocationsContainer>? = if (query == null) {
            api?.getLocations()
        } else api?.getLocations(
            name = query.name,
            type = query.type,
            dimension = query.dimension
        )
        return handleSingle(single)
    }

    fun loadNewPage(): Single<List<LocationData>>? {
        return if (hasMoreData()) {
            val single = api?.getLocations(
                page = ++currentPageNumber,
                name = currentQuery?.name,
                type = currentQuery?.type,
                dimension = currentQuery?.dimension)
            handleSingle(single)
        } else Single.just(emptyList())
    }

    private fun handleSingle(single: Single<AllLocationsContainer>?): Single<List<LocationData>>? {
        val result: Single<List<LocationData>>? =
            single?.subscribeOn(Schedulers.io())?.flatMap { t ->
                getMaxPage(t)
                Single.just(t.results) }
                ?.flatMap { t ->
                    Single.just(
                        transformRepoLocsListInRecLocsList(t)
                    )
                }?.subscribeOn(Schedulers.computation())
        return result
    }

    private fun transformRepoLocsListInRecLocsList(list: List<LocationRetrofitModel>?): List<LocationData> {
        val l = ArrayList<LocationData>()
        list?.forEach { l.add(LocationData(it)) }
        return l
    }

    fun hasMoreData(): Boolean = currentPageNumber + 1 <= maxPageNumber

    private fun getMaxPage(container: AllLocationsContainer) {
        maxPageNumber = container.info.pages
    }
}