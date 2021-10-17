package com.goods.www.domain.model

data class Item(
    val documentId: String,
    val img: String? = null,
    val location: String,
    val name: String,
    val price: String,
)