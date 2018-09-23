package com.sandeepsingh.geotaggingapp

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.mapview_fragment.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.sandeepsingh.geotaggingapp.adapter.MapItemAdapter
import com.sandeepsingh.geotaggingapp.model.MarkerData
import com.sandeepsingh.geotaggingapp.repo.Prefs
import com.sandeepsingh.geotaggingapp.utilities.Utils

/**
 * Created by Sandeep on 9/22/18.
 */
class MapViewFragment : Fragment(), OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener {

    lateinit var iFragmentToActivity : IFragmentToActivity

    lateinit var mGoogleMap: GoogleMap

    lateinit var googleApiClient : GoogleApiClient

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    lateinit var locationRequest: LocationRequest

    var mLastLocation: Location? = null

    var mCurrLocationMarker: Marker? = null

    var latitude : Double = 0.0

    var longitude : Double = 0.0

    companion object {
        internal val TAG = "MainActivity"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.mapview_fragment, container, false)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initGoogleApiClient()
        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        iFragmentToActivity = context as IFragmentToActivity
    }

    override fun onStart() {
        googleApiClient.connect()
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
        if (fusedLocationProviderClient != null){
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
        }
    }

    override fun onStop() {
        super.onStop()
        googleApiClient.disconnect()
    }


    fun initGoogleApiClient(){
        googleApiClient = GoogleApiClient.Builder(context!!).addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
    }

    fun getCurrentLocation(){
        if (ContextCompat.checkSelfPermission(context!!,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
           return
        }

        LocationServices.getFusedLocationProviderClient(context!!).lastLocation.addOnSuccessListener {
            if (it != null){
                latitude = it.latitude
                longitude = it.longitude

                moveMap()
            }
        }

    }

    fun moveMap(){
        val latLng = LatLng(latitude, longitude)
        mGoogleMap.addMarker(MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Marker in India"))

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
        mGoogleMap.uiSettings.isZoomControlsEnabled = true
    }

    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.getLocations()
            if (locationList.size > 0) {
                //The last location in the list is the newest
                val location = locationList.get(locationList.size - 1)
                mLastLocation = location
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker!!.remove()
                }

                //Place current location marker

              /*  latitude = location.latitude
                longitude = location.longitude

                moveMap()*/
                val latLng = LatLng(location.getLatitude(), location.getLongitude())
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title("Current Position")
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions)

                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11f))
            }
        }
    }

    fun requestForPermission(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity!!,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.isMyLocationEnabled = true
            } else {
                //Request Location Permission
                checkLocationPermission()
            }
        }
        else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper())
            mGoogleMap.isMyLocationEnabled = true
        }
    }

    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(activity!!, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                 AlertDialog.Builder(activity!!)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK") { p0, p1 ->
                            ActivityCompat.requestPermissions(activity!!,
                                    Array(1){android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    StaticConstants.MY_PERMISSION_ACCESS_COURSE_LOCATION )
                        }
                         .create()
                        .show()

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity!!,
                        Array(1){android.Manifest.permission.ACCESS_FINE_LOCATION},
                        StaticConstants.MY_PERMISSION_ACCESS_COURSE_LOCATION )
            }
        }
    }

    fun storeMarkerValue(latLng: LatLng){
        val markerData = MarkerData("Test", latLng.latitude,latLng.longitude,"")
        val list = Utils.fetchAllMarkerData(activity!!)
        list.add(markerData)
        Prefs.setList(context!!,StaticConstants.MY_MARKER_DATA,list)
        iFragmentToActivity.newValueAdded(markerData)
    }

    /*fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE)
        Log.d(TAG, "onClick: $imageUri")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, StaticConstants.CROP_PIC)
    }*/

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            StaticConstants.MY_PERMISSION_ACCESS_COURSE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(activity!!,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.isMyLocationEnabled = true
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(activity, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    override fun onMapReady(p0: GoogleMap?) {
        checkLocationPermission()
        mGoogleMap = p0!!
        mGoogleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        val india = LatLng(-34.0, 151.0)
        mGoogleMap.addMarker(MarkerOptions().position(india).title("Marker in India"))
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(india))
        mGoogleMap.setOnMarkerDragListener(this)
        mGoogleMap.setOnMapLongClickListener(this)
        mGoogleMap.setInfoWindowAdapter(MapItemAdapter(activity!!))

        locationRequest = LocationRequest.create()
        locationRequest.setInterval(120000); // two minute interval
        locationRequest.setFastestInterval(120000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)

        getCurrentLocation()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onConnected(p0: Bundle?) {
        getCurrentLocation()
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onMapLongClick(p0: LatLng?) {
        storeMarkerValue(p0!!)
        mGoogleMap.addMarker(MarkerOptions().position(p0!!).draggable(true))
    }

    override fun onMarkerDragEnd(p0: Marker?) {
        latitude = p0!!.position.latitude
        longitude = p0.position.longitude

        moveMap()
    }

    override fun onMarkerDragStart(p0: Marker?) {

    }

    override fun onMarkerDrag(p0: Marker?) {

    }


}


