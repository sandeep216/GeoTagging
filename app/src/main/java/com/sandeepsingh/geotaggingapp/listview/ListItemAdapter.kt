package com.sandeepsingh.geotaggingapp.listview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.sandeepsingh.geotaggingapp.R
import com.sandeepsingh.geotaggingapp.model.MarkerData
import com.sandeepsingh.geotaggingapp.utilities.Utils
import org.w3c.dom.Text

/**
 * Created by Sandeep on 9/23/18.
 */
class ListItemAdapter(val context: Context, var list : MutableList<MarkerData>) : RecyclerView.Adapter<ListItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ItemViewHolder {
        val rootView = LayoutInflater.from(context).inflate(R.layout.layout_listitem,p0,false)
        return ItemViewHolder(rootView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(p0: ItemViewHolder, p1: Int) {
        val markerData = list[p1]
        p0.markerTitle.text = markerData.title
        p0.markerLat.text = "Lat : " + markerData.latitude
        p0.markerLong.text = "Long : " + markerData.longitude
    }

    class ItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var markerTitle : TextView = itemView.findViewById(R.id.tv_marker_title)
        var markerLat : TextView = itemView.findViewById(R.id.tv_marker_lat)
        var markerLong : TextView = itemView.findViewById(R.id.tv_marker_long)
    }

    fun newValueAdded(){
        this.list = Utils.fetchAllMarkerData(context)
        notifyDataSetChanged()
    }
}