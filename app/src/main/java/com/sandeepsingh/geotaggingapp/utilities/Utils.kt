package com.sandeepsingh.geotaggingapp.utilities

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sandeepsingh.geotaggingapp.StaticConstants
import com.sandeepsingh.geotaggingapp.model.MarkerData
import com.sandeepsingh.geotaggingapp.repo.Prefs

/**
 * Created by Sandeep on 9/23/18.
 */
object Utils {
    fun fetchAllMarkerData(context: Context) : MutableList<MarkerData>{
        var arrayItems = mutableListOf<MarkerData>()
        val serializedObject = Prefs.getString(context, StaticConstants.MY_MARKER_DATA,"")
        if (serializedObject != null && serializedObject.isNotEmpty()) {
            val gson = Gson()
            val type = object : TypeToken<List<MarkerData>>() {
            }.type
            arrayItems = gson.fromJson(serializedObject, type)
        }

        return arrayItems
    }
}