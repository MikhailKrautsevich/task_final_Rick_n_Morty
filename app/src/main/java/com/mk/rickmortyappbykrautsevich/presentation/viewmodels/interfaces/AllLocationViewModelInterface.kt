package com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces

import androidx.lifecycle.LiveData
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.queries.LocationQuery
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.LocationData

interface AllLocationViewModelInterface: UsesPagination, WithSearchView {

    fun getLoadingLiveData(): LiveData<Boolean>

    fun getPaginationLiveData(): LiveData<Boolean>

    fun getLocationsList(): LiveData<List<LocationData>>

    fun getData(query: LocationQuery?)
}