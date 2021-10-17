package com.goods.www.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goods.www.domain.model.BrandItem
import com.goods.www.domain.model.GoogleMapSettings
import com.goods.www.domain.model.LocationItem
import com.goods.www.domain.model.ShopItem
import com.goods.www.repository.FirestoreRepository
import com.goods.www.utils.Brands
import com.google.android.libraries.maps.GoogleMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val _locationItem: MutableLiveData<LocationItem> = MutableLiveData()

    private val _infoWindowClicked = MutableLiveData<Boolean>()
    val infoWindowClicked: LiveData<Boolean>
        get() = _infoWindowClicked

    private val _isMapReady = MutableLiveData<Boolean>()
    private val _googleMap = MutableLiveData<GoogleMap>()
    val googleMap: LiveData<GoogleMap>
        get() = _googleMap

    private val _mapReadyAndLocationMediatorLiveData = MediatorLiveData<GoogleMapSettings>()
    val mapReadyAndLocationMediatorLiveData: LiveData<GoogleMapSettings>
        get() = _mapReadyAndLocationMediatorLiveData

    private val _mapZoomLevel = MutableLiveData<Float>()
    private val _searchMyLocation = MutableLiveData<Boolean>()

    private val _brandItem = MutableLiveData<BrandItem>()
    val brandItem: LiveData<BrandItem>
        get() = _brandItem

    private val _shops = MutableLiveData<List<ShopItem>>()
    val shops: LiveData<List<ShopItem>>
        get() = _shops

    init {
        _mapZoomLevel.value = 13f //default zoom level
        _searchMyLocation.value = false
        _locationItem.value = null
        _isMapReady.value = false
        _mapReadyAndLocationMediatorLiveData.addSource(_isMapReady) {
            _mapReadyAndLocationMediatorLiveData.value =
                GoogleMapSettings(it, _locationItem.value, _googleMap.value)
        }
        _mapReadyAndLocationMediatorLiveData.addSource(_locationItem) {
            _mapReadyAndLocationMediatorLiveData.value =
                GoogleMapSettings(_isMapReady.value!!, it, _googleMap.value)
        }
        _mapReadyAndLocationMediatorLiveData.addSource(_googleMap) {
            _mapReadyAndLocationMediatorLiveData.value =
                GoogleMapSettings(_isMapReady.value!!, _locationItem.value, it)
        }
    }

    fun setCurrentLocation(it: LocationItem) {
        _searchMyLocation.value?.let { isPressed ->
            if (isPressed) {
                _locationItem.value = it
                _searchMyLocation.value = false
            } else {
                if (_locationItem.value != it)
                    _locationItem.value = it
            }
        }
    }

    fun setSearchMyLocationClicked(value: Boolean) {
        _searchMyLocation.value = value
    }

    fun getLocationItem() = _locationItem.value

    fun setInfoWindowClicked(bool: Boolean) {
        _infoWindowClicked.value = bool
    }

    fun setMapReady(value: Boolean) {
        _isMapReady.value = value
    }

    fun setGoogleMap(map: GoogleMap) {
        _googleMap.value = map
    }

    fun setZoomLevel(zoomLevel: Float) {
        _mapZoomLevel.value = zoomLevel
    }

    fun getZoomLevel() = _mapZoomLevel.value

    fun clearGoogleMap() {
        _isMapReady.value = false
        _googleMap.value?.clear()
    }

    fun setBrand(brandItem: BrandItem?) {
        _brandItem.value = brandItem
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getMarts(brandItem: BrandItem?) {
        val brand = when (brandItem?.name) {
            "다이소" -> Brands.DAISO
            "노브랜드" -> Brands.NOBRAND
            "이마트" -> Brands.EMART
            "코스트코" -> Brands.COSTCO
            "홈플러스" -> Brands.HOMEPLUS
            "롯데마트" -> Brands.LOTTEMART
            else -> Brands.ALL
        }
        viewModelScope.launch {
            FirestoreRepository.getMarts(brand).collect {
                _shops.value = it
            }
        }
    }
}