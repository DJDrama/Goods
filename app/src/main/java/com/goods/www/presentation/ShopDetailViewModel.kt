package com.goods.www.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goods.www.domain.model.Item
import com.goods.www.repository.FirestoreRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ShopDetailViewModel : ViewModel() {

    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>>
        get() = _items

    @ExperimentalCoroutinesApi
    fun getCategories(documentId: String) {
        FirestoreRepository.getAllItems(documentId).onEach {
            _items.value = it
        }.launchIn(viewModelScope)
    }
}