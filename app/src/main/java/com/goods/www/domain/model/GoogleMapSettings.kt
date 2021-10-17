package com.goods.www.domain.model

import com.google.android.libraries.maps.GoogleMap

data class GoogleMapSettings(
    val isMapReady: Boolean,
    val location: LocationItem?,
    val googleMap: GoogleMap?
)