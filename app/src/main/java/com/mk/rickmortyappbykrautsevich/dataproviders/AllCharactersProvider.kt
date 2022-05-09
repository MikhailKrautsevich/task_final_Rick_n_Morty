package com.mk.rickmortyappbykrautsevich.dataproviders

import com.mk.rickmortyappbykrautsevich.fragments.recyclers_data.CharacterRecData
import com.mk.rickmortyappbykrautsevich.retrofit.RetrofitHelper
import com.mk.rickmortyappbykrautsevich.retrofit.api.GetCharactersApi
import com.mk.rickmortyappbykrautsevich.retrofit.models.AllCharactersContainer
import com.mk.rickmortyappbykrautsevich.retrofit.models.CharacterRetrofitModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class AllCharactersProvider {

    private var api: GetCharactersApi? = null
    private var currentPageNumber = 1
    private val maxPageNumber = 42

    init {
        val retrofit = RetrofitHelper.getRetrofit(RetrofitHelper.getOkHttpClient())
        api = RetrofitHelper.getCharsApi(retrofit)
    }

    fun loadCharacters(): Single<List<CharacterRecData>>? {
        val single = api?.getCharacters()
        return handleSingle(single)
    }

    fun loadNewPage(): Single<List<CharacterRecData>>? {
        return if (hasMoreData()) {
            val single = api?.getCharacters(page = ++currentPageNumber)
            handleSingle(single)
        } else Single.just(emptyList())
    }

    private fun handleSingle(single: Single<AllCharactersContainer>?): Single<List<CharacterRecData>>? {
        val result: Single<List<CharacterRecData>>? =
            single?.subscribeOn(Schedulers.io())?.flatMap { t -> Single.just(t.results) }
                ?.flatMap { t ->
                    Single.just(
                        transformRepoCharsListInRecCharsList(t)
                    )
                }?.subscribeOn(Schedulers.computation())
        return result
    }

    private fun transformRepoCharsListInRecCharsList(list: List<CharacterRetrofitModel>): List<CharacterRecData> {
        val l = ArrayList<CharacterRecData>()
        list.forEach { l.add(CharacterRecData(it)) }
        return l
    }

    fun hasMoreData(): Boolean = currentPageNumber + 1 <= maxPageNumber
}