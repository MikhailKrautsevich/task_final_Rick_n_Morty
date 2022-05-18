package com.mk.rickmortyappbykrautsevich.data.dataproviders.interfaces

import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.EpisodeData
import io.reactivex.rxjava3.core.Single

interface CharacterDetailProviderInterface {

    fun loadData(id: Int): Single<CharacterData>?

    fun loadList(list: List<String>): Single<List<EpisodeData>>
}