package com.goods.www.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.goods.www.datasource.shopItems
import com.goods.www.domain.model.BrandItem

class ShopListViewModel : ViewModel() {

    private val _shops = MutableLiveData<List<BrandItem>>()
    val shops: LiveData<List<BrandItem>>
        get() = _shops

    init {
        _shops.value = shopItems
    }
}
