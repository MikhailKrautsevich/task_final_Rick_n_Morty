package com.mk.rickmortyappbykrautsevich.presentation.fragments.utils

import androidx.appcompat.widget.SearchView
import io.reactivex.rxjava3.subjects.PublishSubject

class SearchViewUtil {

    companion object {
        fun fromView(search: SearchView): PublishSubject<String> {
            val subject: PublishSubject<String> = PublishSubject.create()
            search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {
                        subject.onNext(newText)
                    }
                    return true
                }
            })
            return subject
        }
    }
}