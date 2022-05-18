package com.mk.rickmortyappbykrautsevich.data.dataproviders.interfaces

import com.mk.rickmortyappbykrautsevich.data.retrofit.models.queries.EpisodeQuery
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.EpisodeData
import io.reactivex.rxjava3.core.Single

interface ListEpisodesProviderInterface {

    fun loadEpisodes(query: EpisodeQuery?): Single<List<EpisodeData>>?

    fun loadNewPage(): Single<List<EpisodeData>>?

    fun hasMoreData(): Boolean
}