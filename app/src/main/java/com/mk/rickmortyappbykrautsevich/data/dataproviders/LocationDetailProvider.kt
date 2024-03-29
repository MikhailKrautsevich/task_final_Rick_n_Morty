package com.mk.rickmortyappbykrautsevich.data.dataproviders

import com.mk.rickmortyappbykrautsevich.data.app.App
import com.mk.rickmortyappbykrautsevich.data.utils.NetworkChecker
import com.mk.rickmortyappbykrautsevich.data.dataproviders.interfaces.LocationDetailProviderInterface
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.LocationData
import com.mk.rickmortyappbykrautsevich.data.retrofit.api.GetTheLocationApi
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.CharacterRetrofitModel
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.LocationRetrofitModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.regex.Pattern
import javax.inject.Inject

class LocationDetailProvider : LocationDetailProviderInterface {
    companion object {
        const val TO_REPLACE = "https://rickandmortyapi.com/api/character/"
        const val REGEX = "https://rickandmortyapi.com/api/character/[0-9]+"
    }

    private val db = App.instance?.getDataBase()

    @Inject
    lateinit var checker: NetworkChecker
    private val locationDao = db?.getLocationDao()
    private val characterDao = db?.getCharacterDao()

    @Inject
    lateinit var api: GetTheLocationApi

    init {
        App.instance!!.component.inject(this)
    }

    override fun loadData(id: Int): Single<LocationData>? {
        val isNetworkAvailable = checker.isNetworkAvailable()
        return if (isNetworkAvailable) {
            val single = api.getLocation(id).subscribeOn(Schedulers.io())
            handleSingle(single)
        } else {
            val fromCash = locationDao!!.getTheLocation(id).subscribeOn(Schedulers.io())
            fromCash.map { t -> LocationData(t) }.subscribeOn(Schedulers.computation())
        }
    }

    override fun loadList(list: List<String>): Single<List<CharacterData>> {
        val isNetworkAvailable = checker.isNetworkAvailable()
        if (isNetworkAvailable) {
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
                api.getList(it)
            }.subscribeOn(Schedulers.io())

            val result: Single<List<CharacterData>> = locsRetrofitModels.toObservable()
                .flatMap { it ->
                    Observable.fromIterable(it)
                }.map { it ->
                    CharacterData(it)
                }.toList()
                .subscribeOn(Schedulers.computation())
            return result
        } else {
            return characterDao!!.getCharacters(list).subscribeOn(Schedulers.io())
                .toObservable().flatMap { Observable.fromIterable(it) }
                .map { it -> CharacterData(it) }.toList().subscribeOn(Schedulers.computation())
        }
    }

    private fun handleSingle(single: Single<LocationRetrofitModel>?): Single<LocationData>? {
        val result = single?.map { t ->
            transform(t)
        }?.subscribeOn(Schedulers.computation())
        return result
    }

    private fun transform(loc: LocationRetrofitModel): LocationData {
        return LocationData(loc)
    }
}