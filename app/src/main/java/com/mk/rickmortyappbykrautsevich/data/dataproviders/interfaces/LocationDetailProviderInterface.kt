package com.mk.rickmortyappbykrautsevich.data.dataproviders.interfaces

import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.LocationData
import io.reactivex.rxjava3.core.Single

interface LocationDetailProviderInterface {

    fun loadData(id: Int): Single<LocationData>?

    fun loadList(list: List<String>): Single<List<CharacterData>>
}