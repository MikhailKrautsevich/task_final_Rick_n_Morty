package com.mk.rickmortyappbykrautsevich.data.dataproviders

import com.mk.rickmortyappbykrautsevich.data.app.App
import com.mk.rickmortyappbykrautsevich.data.utils.NetworkChecker
import com.mk.rickmortyappbykrautsevich.data.dataproviders.interfaces.EpisodeDetailProviderInterface
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.EpisodeData
import com.mk.rickmortyappbykrautsevich.data.retrofit.RetrofitHelper
import com.mk.rickmortyappbykrautsevich.data.retrofit.api.GetTheEpisodeApi
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.CharacterRetrofitModel
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.EpisodeRetrofitModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.regex.Pattern
import javax.inject.Inject

class EpisodeDetailProvider : EpisodeDetailProviderInterface {
    companion object {
        const val TO_REPLACE = "https://rickandmortyapi.com/api/character/"
        const val REGEX = "https://rickandmortyapi.com/api/character/[0-9]+"
    }

    private val db = App.instance?.getDataBase()
    @Inject
    lateinit var checker : NetworkChecker
    private val episodeDao = db?.getEpisodeDao()
    private val characterDao = db?.getCharacterDao()
    private var api: GetTheEpisodeApi? = null

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getTheEpApi(retrofit)
        App.instance!!.component.inject(this)
    }

    override fun loadData(id: Int): Single<EpisodeData>? {
        val isNetworkAvailable = checker.isNetworkAvailable()
        return if (isNetworkAvailable) {
            val single = api!!.getEpisode(id).subscribeOn(Schedulers.io())
            return handleSingle(single)
        } else {
            val fromCash = episodeDao!!.getTheEpisode(id).subscribeOn(Schedulers.io())
            fromCash.map { t -> EpisodeData(t) }.subscribeOn(Schedulers.computation())
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

            val epsRetrofitModels: Single<List<CharacterRetrofitModel>> = single.flatMap {
                api!!.getList(it)
            }.subscribeOn(Schedulers.io())

            val result: Single<List<CharacterData>> = epsRetrofitModels.toObservable()
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

    private fun handleSingle(single: Single<EpisodeRetrofitModel>?): Single<EpisodeData>? {
        val result = single?.map { t ->
            transform(t)
        }?.subscribeOn(Schedulers.computation())
        return result
    }

    private fun transform(ep: EpisodeRetrofitModel): EpisodeData {
        return EpisodeData(ep)
    }
}


