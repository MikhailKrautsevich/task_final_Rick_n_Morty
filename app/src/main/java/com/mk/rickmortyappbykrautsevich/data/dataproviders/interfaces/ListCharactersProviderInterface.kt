package com.mk.rickmortyappbykrautsevich.data.dataproviders.interfaces

import com.mk.rickmortyappbykrautsevich.data.retrofit.models.queries.CharacterQuery
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.EpisodeData
import io.reactivex.rxjava3.core.Single

interface ListCharactersProviderInterface {

    fun loadCharacters(query: CharacterQuery?): Single<List<CharacterData>>?

    fun loadNewPage(): Single<List<CharacterData>>?

    fun hasMoreData(): Boolean
}