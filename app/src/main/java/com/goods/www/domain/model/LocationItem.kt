package com.goods.www.domain.model

import android.os.Parcelable
import com.google.android.libraries.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationItem(
    val id: String,
    val name: String,
    val latLng: LatLng?,
    val address: String,
    val phone: String?=null,
    val image: String?=null
): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocationItem

        if (latLng != other.latLng) return false

        return true
    }

    override fun hashCode(): Int {
        return latLng?.hashCode() ?: 0
    }
}