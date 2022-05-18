package com.mk.rickmortyappbykrautsevich.data.dataproviders.interfaces

import com.mk.rickmortyappbykrautsevich.data.retrofit.models.queries.LocationQuery
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.LocationData
import io.reactivex.rxjava3.core.Single

interface ListLocationsProviderInterface {

    fun loadLocations(query: LocationQuery?): Single<List<LocationData>>?

    fun loadNewPage(): Single<List<LocationData>>?

    fun hasMoreData(): Boolean
}