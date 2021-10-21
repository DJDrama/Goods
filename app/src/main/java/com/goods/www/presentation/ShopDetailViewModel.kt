package com.goods.www.presentation

import android.util.Log
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

    private val originalList = mutableListOf<Item>()

    private val _categorySet = MutableLiveData<Set<Pair<String, Int>>>()
    val categorySet get() = _categorySet

    private val _currentItem = MutableLiveData<Item>()
    val currentItem: LiveData<Item> get() = _currentItem

    @ExperimentalCoroutinesApi
    fun getItems(documentId: String) {
        FirestoreRepository.getAllItems(documentId).onEach {
            _items.value = it

            // Add Type to set
            val set = mutableSetOf<Pair<String, Int>>()
            it.forEach { item ->
                set.add(item.type to -1)
            }
            _categorySet.value = set

            if (originalList.isNotEmpty())
                originalList.clear()
            originalList.addAll(it)
        }.launchIn(viewModelScope)
    }

    fun search(query: String? = null) {

        query?.let {
            val tempList = originalList
            val newList = tempList.filter { item ->
                item.name.contains(it)
            }
            _items.value = newList
        } ?: run {
            _items.value = originalList
        }
    }

    fun setCurrentItem(item: Item) {
        _currentItem.value = item

        val newSet = mutableSetOf<Pair<String, Int>>()
        val tempSet = _categorySet.value
        tempSet?.forEachIndexed { index, pair ->
            if (pair.first == item.type) {
                val temp = pair.first to item.position
                newSet.add(temp)
            }else {
                newSet.add(pair)
            }
        }
        _categorySet.value = newSet
    }
}