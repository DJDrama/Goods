package com.goods.www.domain.model

import android.os.Parcelable
import com.google.android.libraries.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationItem(
    val documentId: String,
    val name: String,
    val latLng: LatLng?,
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