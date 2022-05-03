package com.mk.rickmortyappbykrautsevich.dataproviders

import com.mk.rickmortyappbykrautsevich.retrofit.RetrofitHelper
import com.mk.rickmortyappbykrautsevich.retrofit.api.GetEpisodesApi
import com.mk.rickmortyappbykrautsevich.retrofit.models.AllEpisodesContainer
import io.reactivex.rxjava3.core.Single

class AllEpisodeProvider {
    var api: GetEpisodesApi? = null

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getEpsApi(retrofit)
    }

    fun loadAllEpisodes(): Single<AllEpisodesContainer>? = api?.getAllEpisodes()
}