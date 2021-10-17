package com.goods.www.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShopItem(
    val name: String,
    val img: Int,
    val content: String
) : Parcelable