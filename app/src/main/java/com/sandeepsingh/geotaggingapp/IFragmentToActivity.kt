package com.sandeepsingh.geotaggingapp

import com.google.android.gms.maps.model.LatLng
import com.sandeepsingh.geotaggingapp.model.MarkerData

/**
 * Created by Sandeep on 9/23/18.
 */
interface IFragmentToActivity {
    fun newValueAdded(markerData: MarkerData)
    fun onListItemClicked(latLng: LatLng)
    fun onMarkerClickedListener(latLng: LatLng)
}