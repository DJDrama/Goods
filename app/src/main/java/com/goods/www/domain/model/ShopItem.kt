package com.goods.www.domain.model

import com.goods.www.utils.Brands

data class ShopItem(
    val name: String,
    val type: Brands,
    val latitude: Double,
    val longitude: Double,
)
