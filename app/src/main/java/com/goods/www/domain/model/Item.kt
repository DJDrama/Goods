package com.goods.www.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val documentId: String,
    val img: String? = null,
    val position: Int,
    val name: String,
    val price: String,
    val type: String
) : Parcelable