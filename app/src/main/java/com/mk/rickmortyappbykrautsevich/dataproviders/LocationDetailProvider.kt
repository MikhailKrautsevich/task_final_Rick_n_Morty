package com.mk.rickmortyappbykrautsevich.dataproviders

import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.LocationData
import com.mk.rickmortyappbykrautsevich.retrofit.RetrofitHelper
import com.mk.rickmortyappbykrautsevich.retrofit.api.GetTheLocationApi
import com.mk.rickmortyappbykrautsevich.retrofit.models.CharacterRetrofitModel
import com.mk.rickmortyappbykrautsevich.retrofit.models.LocationRetrofitModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.regex.Pattern

class LocationDetailProvider {
    companion object {
        const val TO_REPLACE = "https://rickandmortyapi.com/api/character/"
        const val REGEX = "https://rickandmortyapi.com/api/character/[0-9]+"
    }

    private var api: GetTheLocationApi? = null

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getTheLocApi(retrofit)
    }

    fun loadData(id: Int): Single<LocationData>? {
        val single = api!!.getLocation(id).subscribeOn(Schedulers.io())
        return handleSingle(single)
    }

    fun loadList(list: List<String>): Single<List<CharacterData>> {
        val s = Single.just(list)
        val length = TO_REPLACE.length
        val single = s.toObservable()
            .flatMap { Observable.fromIterable(list) }.filter { t ->
                val p = Pattern.compile(REGEX)
                val matcher = p.matcher(t)
                matcher.matches()
            }
            .map { t -> t.substring(length until t.length) }
            .map { t -> t.toInt() }
            .toList()
            .map { t -> t.toString() }
            .subscribeOn(Schedulers.computation())

        val locsRetrofitModels: Single<List<CharacterRetrofitModel>> = single.flatMap {
            api!!.getList(it)
        }.subscribeOn(Schedulers.io())

        val result: Single<List<CharacterData>> = locsRetrofitModels.toObservable()
            .flatMap { it ->
                Observable.fromIterable(it)
            }.map { it ->
                CharacterData(it)
            }.toList()
            .subscribeOn(Schedulers.computation())
        return result
    }

    private fun handleSingle(single: Single<LocationRetrofitModel>?): Single<LocationData>? {
        val result = single?.map {
            t -> transform(t)
        }?.subscribeOn(Schedulers.computation())
        return result
    }

    private fun transform(loc: LocationRetrofitModel): LocationData {
        return LocationData(loc)
    }
}