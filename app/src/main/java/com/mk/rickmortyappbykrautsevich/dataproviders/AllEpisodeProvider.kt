package com.mk.rickmortyappbykrautsevich.dataproviders

import android.util.Log
import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.EpisodeData
import com.mk.rickmortyappbykrautsevich.retrofit.RetrofitHelper
import com.mk.rickmortyappbykrautsevich.retrofit.api.GetEpisodesApi
import com.mk.rickmortyappbykrautsevich.retrofit.models.AllEpisodesContainer
import com.mk.rickmortyappbykrautsevich.retrofit.models.queries.EpisodeQuery
import com.mk.rickmortyappbykrautsevich.retrofit.models.EpisodeRetrofitModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class AllEpisodeProvider {
    private var api: GetEpisodesApi? = null
    private var currentPageNumber = 1

    // при запросе по умолчанию число страниц 3
    private var maxPageNumber = 3
    private var currentQuery: EpisodeQuery? = null

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getEpsApi(retrofit)
    }

    fun loadEpisodes(query: EpisodeQuery?): Single<List<EpisodeData>>? {
        currentPageNumber = 1
        currentQuery = query
        val single: Single<AllEpisodesContainer>? = if (query == null) {
            api?.getEpisodes()
        } else api?.getEpisodes(
            name = query.name,
            episode = query.code
        )
        return handleSingle(single)
    }

    fun loadNewPage(): Single<List<EpisodeData>>? {
        return if (hasMoreData()) {
            Log.d("12347", "Provider $maxPageNumber $currentQuery")
            val single = api?.getEpisodes(
                page = ++currentPageNumber,
                name = currentQuery?.name,
                episode = currentQuery?.code
            )
            handleSingle(single)
        } else Single.just(emptyList())
    }

    private fun handleSingle(single: Single<AllEpisodesContainer>?): Single<List<EpisodeData>>? {
        val result: Single<List<EpisodeData>>? =
            single?.subscribeOn(Schedulers.io())?.flatMap { t ->
                getMaxPage(t)
                Single.just(t.results)
            }
                ?.flatMap { t ->
                    Single.just(
                        transformRepoEpsListInRecEpsList(t)
                    )
                }?.subscribeOn(Schedulers.computation())
        return result
    }

    private fun transformRepoEpsListInRecEpsList(list: List<EpisodeRetrofitModel>?): List<EpisodeData> {
        val l = ArrayList<EpisodeData>()
        list?.forEach { l.add(EpisodeData(it)) }
        return l
    }

    fun hasMoreData(): Boolean = currentPageNumber + 1 <= maxPageNumber

    private fun getMaxPage(container: AllEpisodesContainer) {
        maxPageNumber = container.info.pages
    }
}
