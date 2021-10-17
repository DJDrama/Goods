package com.goods.www.datasource.model

import com.goods.www.domain.model.ShopItem
import com.goods.www.utils.Brands

data class ShopDto(
    var documentId: String = "",
    var name: String = "",
    var type: String = "",
    var latitude: String = "",
    var longitude: String = "",
)

fun ShopDto.toDomainModel(): ShopItem {
    return ShopItem(
        documentId = documentId,
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

fun List<ShopDto>.toDomainList(): List<ShopItem> {
    return map {
        it.toDomainModel()
    }
}
