package com.mk.rickmortyappbykrautsevich.data.dataproviders

import com.mk.rickmortyappbykrautsevich.data.app.App
import com.mk.rickmortyappbykrautsevich.data.app.NetworkChecker
import com.mk.rickmortyappbykrautsevich.data.dataproviders.interfaces.CharacterDetailProviderInterface
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.EpisodeData
import com.mk.rickmortyappbykrautsevich.data.retrofit.RetrofitHelper
import com.mk.rickmortyappbykrautsevich.data.retrofit.api.GetTheCharacterApi
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.CharacterRetrofitModel
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.EpisodeRetrofitModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.regex.Pattern

class CharacterDetailProvider : CharacterDetailProviderInterface {
    companion object {
        const val TO_REPLACE = "https://rickandmortyapi.com/api/episode/"
        const val REGEX = "https://rickandmortyapi.com/api/episode/[0-9]+"
    }

    private val db = App.instance?.getDataBase()
    private val checker : NetworkChecker = App.instance as NetworkChecker
    private val episodeDao = db?.getEpisodeDao()
    private val characterDao = db?.getCharacterDao()
    private var api: GetTheCharacterApi? = null

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getTheCharApi(retrofit)
    }

    override fun loadData(id: Int): Single<CharacterData>? {
        val isNetworkAvailable = checker.isNetworkAvailable()
        return if (isNetworkAvailable) {
            val single = api!!.getCharacter(id).subscribeOn(Schedulers.io())
            return handleSingle(single)
        } else {
            val fromCash = characterDao!!.getTheCharacter(id).subscribeOn(Schedulers.io())
            fromCash.map { t -> CharacterData(t) }.subscribeOn(Schedulers.computation())
        }
    }

    override fun loadList(list: List<String>): Single<List<EpisodeData>> {
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
