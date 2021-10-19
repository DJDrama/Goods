package com.goods.www.datasource.model

import com.goods.www.domain.model.Item

data class ItemDto(
    var documentId: String = "",
    var img: String = "",
    var position: Int = 0,
    var name: String = "",
    var price: String = "",
    var type: String = "",
)

fun ItemDto.toDomainModel(): Item {
    return Item(
        documentId = documentId,
        img = img,
        position = position,
        name = name,
        price = price,
        type = type
    )
}

fun List<ItemDto>.toDomainList(): List<Item> {
    return map {
        it.toDomainModel()
    }
}

