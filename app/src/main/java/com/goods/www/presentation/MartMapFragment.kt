package com.goods.www.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.goods.www.R
import com.goods.www.databinding.FragmentMartMapBinding
import com.goods.www.domain.model.LocationItem
import com.goods.www.domain.model.ShopItem
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.BitmapDescriptorFactory.HUE_GREEN
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.Marker
import com.google.android.libraries.maps.model.MarkerOptions

class MartMapFragment : Fragment(R.layout.fragment_mart_map), OnMapReadyCallback,
    GoogleMap.OnInfoWindowClickListener {
    private var _binding: FragmentMartMapBinding? = null
    private val binding get() = _binding!!

    private val args: MartMapFragmentArgs by navArgs()
    private val mapViewModel: MapViewModel by viewModels()

    private var mHasPermission: Boolean = false
    private var mPermissionRequestCount: Int = 0
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private var requestingLocationUpdates = false

    private var locationCallback: LocationCallback? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMartMapBinding.bind(view)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        mapViewModel.setBrand(args.brandItem)

        requestPermissionsIfNecessary()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data

                    mapViewModel.setCurrentLocation(
                        LocationItem(
                            documentId = "Current Location",
                            name = "현재 위치",
                            latLng = LatLng(location.latitude, location.longitude),
                        )
                    )
                    //just do once so break
                    //stopLocationUpdates()
                    break
                }
            }
        }
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        updateValuesFromBundle(savedInstanceState)

        binding.map.let {
            it.onCreate(savedInstanceState)
            it.getMapAsync(this)
        }

        subscribeToObservers()
    }

    @SuppressLint("MissingPermission")
    private fun subscribeToObservers() {
        mapViewModel.brandItem.observe(viewLifecycleOwner) {
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
            it?.let {
                actionBar?.setTitle(it.name)
            } ?: run {
                actionBar?.setTitle("내 주변 마트")
            }
        }
        mapViewModel.shops.observe(viewLifecycleOwner) {
            it?.let { shops ->
                mapViewModel.googleMap.value?.let { googleMap ->
                    shops.forEach { shopItem->
                        val mo = MarkerOptions().position(
                            LatLng(
                                shopItem.latitude,
                                shopItem.longitude
                            )
                        ).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .title(shopItem.name)
                        googleMap.apply {
                            val marker = addMarker(mo)
                            marker.tag = shopItem
                        }
                    }
                }
            }
        }
        mapViewModel.mapReadyAndLocationMediatorLiveData.observe(viewLifecycleOwner) {
            it?.run {
                if (isMapReady) {
                    this.googleMap?.let { gMap ->
                        this.location?.let { locationItem ->
                            with(gMap) {
                                moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        locationItem.latLng,
                                        mapViewModel.getZoomLevel() ?: 13f
                                    )
                                )
                                clear()
                                val markerOptions =
                                    MarkerOptions().position(locationItem.latLng!!)
                                        .icon(BitmapDescriptorFactory.defaultMarker(HUE_GREEN))
                                        .title(locationItem.name)
                                val marker = addMarker(markerOptions)
                                marker.tag = locationItem
                                marker.showInfoWindow()

                                setOnInfoWindowClickListener(this@MartMapFragment)

                                isMyLocationEnabled = true
                                uiSettings.isZoomControlsEnabled = true
                                setOnCameraMoveListener {
                                    //control zoom level
                                    mapViewModel.setZoomLevel(cameraPosition.zoom)
                                }

                                setOnMyLocationButtonClickListener {
                                    startLocationUpdates()
                                    mapViewModel.setSearchMyLocationClicked(true)
                                    //getLastLocation()
                                    false
                                }

                                // After Google map has been initialized, fetch my visited places
                                //  fetchDangerousPlaces(this)
                                mapViewModel.getMarts(mapViewModel.brandItem.value)
                            }
                        } ?: if (requestingLocationUpdates) {
                            startLocationUpdates()
                        } else {
                            setLocationSettings()
                        }
                    }
                }
            }
        }
        mapViewModel.infoWindowClicked.observe(viewLifecycleOwner) {
            it?.let { boolValue ->
                if (boolValue) {
                    mapViewModel.getLocationItem()?.let { locationItem ->
                        val action = MartMapFragmentDirections.actionMartMapFragmentToShopDetailFragment(
                            documentId = locationItem.documentId
                        )
                        findNavController().navigate(action)
                        mapViewModel.setInfoWindowClicked(false)
                    }
                }
            }
        }
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return
        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                REQUESTING_LOCATION_UPDATES_KEY
            )
        }
        // ...
        // Update UI to match restored state
        //updateUI()
    }

    private fun requestPermissionsIfNecessary() {
        mHasPermission = checkPermission()
        if (!mHasPermission) {
            if (mPermissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                mPermissionRequestCount++
                requestPermissions(
                    arrayOf(locationPermission),
                    REQUEST_CODE_PERMISSION
                )
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.set_permissions_in_settings),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            locationPermission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if permissions were granted after a permissions request flow.
        if (requestCode == REQUEST_CODE_PERMISSION) {
            //just check index 0 since we have only 1 permission
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.set_permissions_in_settings),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(requireContext(),
                getString(R.string.set_permissions_in_settings),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (::fusedLocationProviderClient.isInitialized) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    mapViewModel.setCurrentLocation(
                        LocationItem(
                            documentId = "Current Location",
                            name = "현재 위치",
                            latLng = LatLng(location.latitude, location.longitude),
                        )
                    )
                } ?: setLocationSettings()
            }
        }
    }

    private fun setLocationSettings() {
        // Create the location request to start receiving updates
        // https://developer.android.com/training/location/change-location-settings
        mLocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = UPDATE_INTERVAL
            fastestInterval = FASTEST_INTERVAL
        }

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val task = settingsClient.checkLocationSettings(locationSettingsRequest)
        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            requestingLocationUpdates = true
            startLocationUpdates()
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        requireActivity(),
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Log.e("Error", "SendIntentException:  $sendEx")
                }
            } else {
                Log.e("MapFragment", "$exception")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (checkPermission()) {
            fusedLocationProviderClient.requestLocationUpdates(
                mLocationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (::mLocationRequest.isInitialized)
            startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun setGoogleMap() {
        binding.apply {
            map.getMapAsync(this@MartMapFragment)
        }
    }

    private fun stopLocationUpdates() {
        requestingLocationUpdates = false
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.map.onDestroy()
        mapViewModel.clearGoogleMap()
        if (locationCallback != null) {
            locationCallback = null
        }
        _binding = null
    }

    override fun onMapReady(p0: GoogleMap?) {
        p0?.let {
            mapViewModel.setMapReady(true)
            mapViewModel.setGoogleMap(it)
        }
    }

    override fun onInfoWindowClick(p0: Marker?) {
        p0?.let {
            if (it.title == "현재 위치") return

            var item = it.tag
            if (item is ShopItem) {
                mapViewModel.setCurrentLocation(
                    LocationItem(
                        documentId = item.documentId,
                        name = item.name,
                        latLng = LatLng(item.latitude, item.longitude),
                    )
                )
            } else {
                item = item as LocationItem
                mapViewModel.setCurrentLocation(item)
            }
            mapViewModel.setInfoWindowClicked(true)
        }
    }

    companion object {
        const val REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY"
        const val REQUEST_CODE_PERMISSION = 101
        const val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        const val MAX_NUMBER_REQUEST_PERMISSIONS = 2
        const val UPDATE_INTERVAL = 10 * 1000L
        const val FASTEST_INTERVAL = 2000L
        const val REQUEST_CHECK_SETTINGS = 1011
        const val AUTOCOMPLETE_REQUEST_CODE = 1
    }
}