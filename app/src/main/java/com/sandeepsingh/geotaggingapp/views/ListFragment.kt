package com.sandeepsingh.geotaggingapp.views

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.maps.model.LatLng
import com.sandeepsingh.geotaggingapp.IFragmentToActivity
import com.sandeepsingh.geotaggingapp.R
import com.sandeepsingh.geotaggingapp.adapter.ListItemAdapter
import com.sandeepsingh.geotaggingapp.model.MarkerData
import com.sandeepsingh.geotaggingapp.utilities.Utils

/**
 * Created by Sandeep on 9/22/18.
 */
class ListFragment : Fragment() {

    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    lateinit var mAdapter: ListItemAdapter

    lateinit var mActivity : FragmentActivity

    lateinit var iFragmentActivity: IFragmentToActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.list_fragment, container, false)
        ButterKnife.bind(this, rootView)
        initView()
        return rootView
    }

    fun initView() {
        recyclerView.layoutManager = LinearLayoutManager(activity!!)
        mAdapter = ListItemAdapter(activity!!, Utils.fetchAllMarkerData(activity!!))
        recyclerView.adapter = mAdapter
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as FragmentActivity
        iFragmentActivity = context as IFragmentToActivity
    }

    fun newValueAdded(markerData: MarkerData?) {
        mAdapter = ListItemAdapter(mActivity, Utils.fetchAllMarkerData(mActivity))
        recyclerView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }

    fun onListItemClickListen(latLng: LatLng){
        iFragmentActivity.onListItemClicked(latLng)
    }
}