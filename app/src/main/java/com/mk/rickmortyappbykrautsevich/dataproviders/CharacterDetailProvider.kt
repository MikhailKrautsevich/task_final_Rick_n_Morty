package com.mk.rickmortyappbykrautsevich.dataproviders

import com.mk.rickmortyappbykrautsevich.App
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.EpisodeData
import com.mk.rickmortyappbykrautsevich.retrofit.RetrofitHelper
import com.mk.rickmortyappbykrautsevich.retrofit.api.GetTheCharacterApi
import com.mk.rickmortyappbykrautsevich.retrofit.models.CharacterRetrofitModel
import com.mk.rickmortyappbykrautsevich.retrofit.models.EpisodeRetrofitModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.regex.Pattern

class CharacterDetailProvider {
    companion object {
        const val TO_REPLACE = "https://rickandmortyapi.com/api/episode/"
        const val REGEX = "https://rickandmortyapi.com/api/episode/[0-9]+"
    }

    private val app = App.instance
    private val episodeDao = app?.getDataBase()?.getEpisodeDao()
    private val characterDao = app?.getDataBase()?.getCharacterDao()
    private var api: GetTheCharacterApi? = null

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getTheCharApi(retrofit)
    }

    fun loadData(id: Int): Single<CharacterData>? {
        val isNetworkAvailable = app!!.isNetworkAvailable()
        return if (isNetworkAvailable) {
            val single = api!!.getCharacter(id).subscribeOn(Schedulers.io())
            return handleSingle(single)
        } else {
            val fromCash = characterDao!!.getTheCharacter(id).subscribeOn(Schedulers.io())
            fromCash.map { t -> CharacterData(t) }.subscribeOn(Schedulers.computation())
        }
    }

    fun loadList(list: List<String>): Single<List<EpisodeData>> {
        val isNetworkAvailable = app!!.isNetworkAvailable()
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

            val epsRetrofitModels: Single<List<EpisodeRetrofitModel>> = single.flatMap {
                api!!.getList(it)
            }.subscribeOn(Schedulers.io())

            val result: Single<List<EpisodeData>> = epsRetrofitModels.toObservable()
                .flatMap { it ->
                    Observable.fromIterable(it)
                }.map { it ->
                    EpisodeData(it)
                }.toList()
                .subscribeOn(Schedulers.computation())
            return result
        } else {
            return episodeDao!!.getEpisodes(list).subscribeOn(Schedulers.io())
                .toObservable().flatMap { Observable.fromIterable(it) }
                .map { it -> EpisodeData(it) }.toList().subscribeOn(Schedulers.computation())
        }
    }

    private fun handleSingle(single: Single<CharacterRetrofitModel>?): Single<CharacterData>? {
        val result = single?.map { t ->
            transform(t)
        }?.subscribeOn(Schedulers.computation())
        return result
    }

    private fun transform(ch: CharacterRetrofitModel): CharacterData {
        return CharacterData(ch)
    }
}
