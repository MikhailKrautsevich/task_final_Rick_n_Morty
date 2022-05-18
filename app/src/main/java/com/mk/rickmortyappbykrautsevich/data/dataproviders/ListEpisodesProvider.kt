package com.mk.rickmortyappbykrautsevich.data.dataproviders

import android.util.Log
import com.mk.rickmortyappbykrautsevich.data.app.App
import com.mk.rickmortyappbykrautsevich.data.utils.NetworkChecker
import com.mk.rickmortyappbykrautsevich.data.dataproviders.interfaces.ListEpisodesProviderInterface
import com.mk.rickmortyappbykrautsevich.data.db.entities.EpisodeEntity
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.EpisodeData
import com.mk.rickmortyappbykrautsevich.data.retrofit.RetrofitHelper
import com.mk.rickmortyappbykrautsevich.data.retrofit.api.GetEpisodesApi
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.AllEpisodesContainer
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.queries.EpisodeQuery
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.EpisodeRetrofitModel
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class ListEpisodesProvider : ListEpisodesProviderInterface {
    private var api: GetEpisodesApi? = null
    private var currentPageNumber = 1

    // при запросе по умолчанию число страниц 3
    private var maxPageNumber = 3
    private var currentQuery: EpisodeQuery? = null

    @Inject
    lateinit var checker : NetworkChecker
    val dao = App.instance?.getDataBase()?.getEpisodeDao()

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getEpsApi(retrofit)
        App.instance!!.component.inject(this)
    }

    override fun loadEpisodes(query: EpisodeQuery?): Single<List<EpisodeData>>? {
        val isNetworkAvailable = checker.isNetworkAvailable()
        if (isNetworkAvailable) {
            currentPageNumber = 1
            currentQuery = query
            val single: Single<AllEpisodesContainer>? = if (query == null) {
                api?.getEpisodes()
            } else api?.getEpisodes(
                name = query.name,
                episode = query.code
            )
            return handleSingle(single)
        } else {
            // отключаем пагинацию
            maxPageNumber = -1
            val fromCash = if (query == null) {
                dao!!.getAllEpisodes()
            } else dao!!.getEpisodes(
                "%${query.name}%",
                "%${query.code}%"
            )
            return fromCash.subscribeOn(Schedulers.io())
                .toObservable()
                .flatMap { t ->
                    Log.d("1222", t.toString())
                    Observable.fromIterable(t)
                }
                .map { t ->
                    Log.d("1222", t.toString())
                    EpisodeData(t)
                }
                .toList()
        }
    }

    override fun loadNewPage(): Single<List<EpisodeData>>? {
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
            single?.subscribeOn(Schedulers.io())?.map { t ->
                getMaxPage(t)
                t.results
            }
                ?.map { t ->
                    saveListToBD(t)
                    t
                }
                ?.map { t ->
                    transformRepoEpsListInRecEpsList(t)
                }?.subscribeOn(Schedulers.computation())
        return result
    }

    private fun transformRepoEpsListInRecEpsList(list: List<EpisodeRetrofitModel>?): List<EpisodeData> {
        val l = ArrayList<EpisodeData>()
        list?.forEach { l.add(EpisodeData(it)) }
        return l
    }

    override fun hasMoreData(): Boolean = currentPageNumber + 1 <= maxPageNumber

    private fun getMaxPage(container: AllEpisodesContainer) {
        maxPageNumber = container.info.pages
    }

    private fun saveListToBD(listLRM: List<EpisodeRetrofitModel>?) {
        if (listLRM != null) {
            Flowable.fromIterable(listLRM)
                .map { t -> transformRepoEpInEntity(t) }
                .toList()
                .observeOn(Schedulers.newThread())
                .subscribeWith(object : SingleObserver<List<EpisodeEntity>> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d("112211", "AllLocationProvider: onSubscribe")
                    }

                    override fun onSuccess(t: List<EpisodeEntity>) {
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

    private fun transformRepoEpInEntity(model: EpisodeRetrofitModel): EpisodeEntity {
        return EpisodeEntity(model)
    }
}
