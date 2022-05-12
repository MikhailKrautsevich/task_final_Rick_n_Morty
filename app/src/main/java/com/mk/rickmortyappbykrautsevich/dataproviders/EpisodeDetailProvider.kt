package com.mk.rickmortyappbykrautsevich.dataproviders

import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.EpisodeData
import com.mk.rickmortyappbykrautsevich.retrofit.RetrofitHelper
import com.mk.rickmortyappbykrautsevich.retrofit.api.GetTheEpisodeApi
import com.mk.rickmortyappbykrautsevich.retrofit.models.EpisodeRetrofitModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class EpisodeDetailProvider {

    private var api: GetTheEpisodeApi?

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getTheEpApi(retrofit)
    }

    fun loadData(id: Int): Single<EpisodeData>? {
        val single = api?.getEpisode(id)
        return handleSingle(single)
    }

//    fun loadList(array: Array<Int>): Single<List<EpisodeData>>{
//        val single = api?.getList(array.toString())
//    }

    private fun handleSingle(single: Single<EpisodeRetrofitModel>?): Single<EpisodeData>? {
        val result = single?.subscribeOn(Schedulers.io())?.flatMap { it ->
            Single.just(transform(it))
        }?.subscribeOn(Schedulers.computation())
        return result
    }

    private fun transform(ep: EpisodeRetrofitModel): EpisodeData {
        return EpisodeData(ep)
    }
}
