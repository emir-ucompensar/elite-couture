package com.elitecouture.app.ui.feature.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Shared ViewModel that retains the currently selected store filter so it survives navigation
 * changes and keeps the store screen "warm" between transitions.
 */
class StoreViewModel : ViewModel() {
    private val _selectedFilter = MutableLiveData(StoreFilter.all())
    val selectedFilter: LiveData<StoreFilter> = _selectedFilter

    fun setFilter(filter: StoreFilter) {
        if (_selectedFilter.value != filter) {
            _selectedFilter.value = filter
        }
    }
}
