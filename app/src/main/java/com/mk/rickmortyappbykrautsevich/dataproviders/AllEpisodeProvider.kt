package com.mk.rickmortyappbykrautsevich.dataproviders

import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.EpisodeRecData
import com.mk.rickmortyappbykrautsevich.retrofit.RetrofitHelper
import com.mk.rickmortyappbykrautsevich.retrofit.api.GetEpisodesApi
import com.mk.rickmortyappbykrautsevich.retrofit.models.AllEpisodesContainer
import com.mk.rickmortyappbykrautsevich.retrofit.models.EpisodeRetrofitModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class AllEpisodeProvider {
    private var api: GetEpisodesApi? = null
    private var currentPageNumber = 1
    private val maxPageNumber = 3

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getEpsApi(retrofit)
    }

    fun loadEpisodes(): Single<List<EpisodeRecData>>? {
        val single = api?.getEpisodes()
        return handleSingle(single)
    }

    fun loadNewPage(): Single<List<EpisodeRecData>>? {
        return if (hasMoreData()) {
            val single = api?.getEpisodes(page = ++currentPageNumber)
            handleSingle(single)
        } else Single.just(emptyList())
    }

    private fun handleSingle(single: Single<AllEpisodesContainer>?): Single<List<EpisodeRecData>>? {
        val result: Single<List<EpisodeRecData>>? =
            single?.subscribeOn(Schedulers.io())?.flatMap { t -> Single.just(t.results) }
                ?.flatMap { t ->
                    Single.just(
                        transformRepoEpsListInRecEpsList(t)
                    )
                }?.subscribeOn(Schedulers.computation())
        return result
    }

    private fun transformRepoEpsListInRecEpsList(list: List<EpisodeRetrofitModel>?): List<EpisodeRecData> {
        val l = ArrayList<EpisodeRecData>()
        list?.forEach { l.add(EpisodeRecData(it)) }
        return l
    }

    fun hasMoreData(): Boolean = currentPageNumber + 1 <= maxPageNumber
}