package com.mk.rickmortyappbykrautsevich.data.dataproviders

import android.util.Log
import com.mk.rickmortyappbykrautsevich.App
import com.mk.rickmortyappbykrautsevich.data.db.entities.LocationEntity
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.LocationData
import com.mk.rickmortyappbykrautsevich.data.retrofit.RetrofitHelper
import com.mk.rickmortyappbykrautsevich.data.retrofit.api.GetLocationsApi
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.AllLocationsContainer
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.LocationRetrofitModel
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.queries.LocationQuery
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ListLocationsProvider {
    private var api: GetLocationsApi? = null
    private var currentPageNumber = 1

    // при запросе по умолчанию число страниц 7
    private var maxPageNumber = 7
    private var currentQuery: LocationQuery? = null

    private val app = App.instance
    private val dao = app?.getDataBase()?.getLocationDao()

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getLocsApi(retrofit)
    }

    fun loadLocations(query: LocationQuery?): Single<List<LocationData>>? {
        val isNetworkAvailable = app!!.isNetworkAvailable()
        if (isNetworkAvailable) {
            currentPageNumber = 1
            currentQuery = query
            val single: Single<AllLocationsContainer>? = if (query == null) {
                api?.getLocations()
            } else {
                api?.getLocations(
                    name = query.name,
                    type = query.type,
                    dimension = query.dimension
                )
            }
            return handleSingle(single)
        } else {
            // отключаем пагинацию
            maxPageNumber = -1
            val fromCash = if (query == null) {
                dao!!.getAllLocations()
            } else dao!!.getLocations(
                "%${query.name}%",
                "%${query.type}%",
                "%${query.dimension}%"
            )
            return fromCash.subscribeOn(Schedulers.io())
                .toObservable()
                .flatMap { t ->
                    Log.d("1222", t.toString())
                    Observable.fromIterable(t)
                }
                .map { t ->
                    Log.d("1222", t.toString())
                    LocationData(t)
                }
                .toList()
        }
    }

    fun loadNewPage(): Single<List<LocationData>>? {
        return if (hasMoreData()) {
            val single = api?.getLocations(
                page = ++currentPageNumber,
                name = currentQuery?.name,
                type = currentQuery?.type,
                dimension = currentQuery?.dimension
            )
            handleSingle(single)
        } else Single.just(emptyList())
    }

    private fun handleSingle(single: Single<AllLocationsContainer>?): Single<List<LocationData>>? {
        val result: Single<List<LocationData>>? =
            single?.subscribeOn(Schedulers.io())?.map { t ->
                getMaxPage(t)
                t.results
            }
                ?.map { t ->
                    saveListToBD(t)
                    t
                }
                ?.map { t ->
                    Log.d("112211", "map after saving to BD")
                    transformRepoLocsListInRecLocsList(t)
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

    private fun saveListToBD(listLRM: List<LocationRetrofitModel>?) {
        if (listLRM != null) {
            Flowable.fromIterable(listLRM)
                .map { t -> transformRepoLocInEntity(t) }
                .toList()
                .observeOn(Schedulers.newThread())
                .subscribeWith(object : SingleObserver<List<LocationEntity>> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d("112211", "AllLocationProvider: onSubscribe")
                    }

                    override fun onSuccess(t: List<LocationEntity>) {
                        Log.d("112211", "AllLocationProvider: onSuccess - start")
                        dao?.insertList(t)
                        Log.d("112211", "AllLocationProvider: onSuccess - finish")
                    }

                    override fun onError(e: Throwable) {
                        Log.d("112211", "AllLocationProvider: onError ${e.localizedMessage}")
                    }
                })
        }
    }

    private fun transformRepoLocInEntity(model: LocationRetrofitModel): LocationEntity {
        return LocationEntity(model)
    }
}