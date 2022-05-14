package com.mk.rickmortyappbykrautsevich.dataproviders

import android.util.Log
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.EpisodeData
import com.mk.rickmortyappbykrautsevich.retrofit.RetrofitHelper
import com.mk.rickmortyappbykrautsevich.retrofit.api.GetTheEpisodeApi
import com.mk.rickmortyappbykrautsevich.retrofit.models.CharacterRetrofitModel
import com.mk.rickmortyappbykrautsevich.retrofit.models.EpisodeRetrofitModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.regex.Pattern

class EpisodeDetailProvider {
    companion object {
        const val TO_REPLACE = "https://rickandmortyapi.com/api/character/"
        const val REGEX = "https://rickandmortyapi.com/api/character/[0-9]+"
    }

    private var api: GetTheEpisodeApi? = null

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getTheEpApi(retrofit)
    }

    fun loadData(id: Int): Single<EpisodeData>? {
        val single = api!!.getEpisode(id).subscribeOn(Schedulers.io())
        return handleSingle(single)
    }

    fun loadList(list: List<String>) : Single<List<CharacterData>> {
        Log.d("112211", "loadList")
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

        val charsRetrofitModels: Single<List<CharacterRetrofitModel>> = single.flatMap {
            api!!.getList(it)
        }.subscribeOn(Schedulers.io())

        val result: Single<List<CharacterData>> = charsRetrofitModels.toObservable()
            .flatMap { it ->
                Observable.fromIterable(it)
            }.map { it ->
                CharacterData(it)
            }.toList()
            .subscribeOn(Schedulers.computation())
        return result
    }

    private fun handleSingle(single: Single<EpisodeRetrofitModel>?): Single<EpisodeData>? {
        val result = single?.map {
                t -> transform(t)
        }?.subscribeOn(Schedulers.computation())
        return result
    }

    private fun transform(ep: EpisodeRetrofitModel): EpisodeData {
        return EpisodeData(ep)
    }
}


