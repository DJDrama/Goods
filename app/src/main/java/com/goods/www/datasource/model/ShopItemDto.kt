package com.goods.www.datasource.model

import com.goods.www.domain.model.ShopItem
import com.goods.www.utils.Brands

data class ShopItemDto(
    var name: String="",
    var type: String="",
    var latitude: String="",
    var longitude: String="",
)

fun ShopItemDto.toDomainModel(): ShopItem {
    return ShopItem(
        name = name,
        type = when (type) {
            "daiso" -> Brands.DAISO
            "nobrand" -> Brands.NOBRAND
            "emart" -> Brands.EMART
            "costco" -> Brands.COSTCO
            "homeplus" -> Brands.HOMEPLUS
            "lottemart" -> Brands.LOTTEMART
            else -> Brands.DAISO
        },
        latitude = latitude.toDouble(),
        longitude = longitude.toDouble()
    )
}

fun List<ShopItemDto>.toDomainList(): List<ShopItem> {
    return map {
        it.toDomainModel()
    }
}
