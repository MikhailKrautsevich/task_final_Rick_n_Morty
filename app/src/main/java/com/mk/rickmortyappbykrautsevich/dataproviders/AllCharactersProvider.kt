package com.mk.rickmortyappbykrautsevich.dataproviders

import com.mk.rickmortyappbykrautsevich.retrofit.RetrofitHelper
import com.mk.rickmortyappbykrautsevich.retrofit.api.GetCharactersApi
import com.mk.rickmortyappbykrautsevich.retrofit.models.AllCharactersContainer
import io.reactivex.rxjava3.core.Single

class AllCharactersProvider {

    private var api: GetCharactersApi? = null

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getCharsApi(retrofit)
    }

    fun loadCharacters(): Single<AllCharactersContainer>? = api?.getCharacters()
}