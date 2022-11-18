package com.oborodulin.home.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oborodulin.home.data.local.db.entities.BaseEntity

private const val TAG = "ListViewModel"

open class ListViewModel<T : Any> : ViewModel() {
    private val selItemsCntLiveData = MutableLiveData<Int?>()

    // create set text method
    fun setSelectedItemsCount(i: Int?) {
        selItemsCntLiveData.value = i
    }

    // create get text method
    fun getSelectedItemsCount(): MutableLiveData<Int?> {
        return selItemsCntLiveData
    }

    open fun addItem(entity: T) {}
    open fun deleteItem(entity: T) {}
}