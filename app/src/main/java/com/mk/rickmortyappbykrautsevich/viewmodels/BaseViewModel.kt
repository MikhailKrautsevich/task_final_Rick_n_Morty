package com.mk.rickmortyappbykrautsevich.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.Disposable

// Класс, от которого должны наследоваться ViewModel
open class BaseViewModel : ViewModel() {

    private val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private var disposable: Disposable? = null

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

    fun getLoadingLiveData() = loadingLiveData as LiveData<Boolean>

    fun getData(id: Int) {}

    // Метод для обработки ошибок, метод нужно вызвать при полном отсутствии данных.
    private fun postEmptyData() {
        loadingLiveData.postValue(false)
    }
}