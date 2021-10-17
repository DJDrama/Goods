package com.goods.www.domain.model

import com.goods.www.utils.Brands

data class ShopItem(
    val documentId: String,
    val name: String,
    val type: Brands,
    val latitude: Double,
    val longitude: Double,
)
