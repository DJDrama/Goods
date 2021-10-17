package com.goods.www.datasource.model

import com.goods.www.domain.model.Item

data class ItemDto(
    var documentId: String="",
    var img: String = "",
    var location: String = "",
    var name: String = "",
    var price: String = "",
)

fun ItemDto.toDomainModel(): Item {
    return Item(
        documentId = documentId,
        img = img,
        location = location,
        name = name,
        price = price
    )
}

fun List<ItemDto>.toDomainList(): List<Item> {
    return map {
        it.toDomainModel()
    }
}

