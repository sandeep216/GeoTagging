package com.sandeepsingh.geotaggingapp.adapter

import android.content.Context
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView



/**
 * Created by Sandeep on 9/23/18.
 */
class MapItemAdapter(context: Context) : GoogleMap.InfoWindowAdapter {

    var textView : TextView = TextView(context)

    init {
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        textView.setLayoutParams(lp)
        textView.setGravity(Gravity.CENTER)
    }

    override fun getInfoContents(p0: Marker?): View {
        return textView
    }

    override fun getInfoWindow(p0: Marker?): View {
        textView.setText(p0!!.title)
        return textView
    }
}