package com.sandeepsingh.geotaggingapp.views

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.maps.model.LatLng
import com.sandeepsingh.geotaggingapp.IFragmentToActivity
import com.sandeepsingh.geotaggingapp.R
import com.sandeepsingh.geotaggingapp.StaticConstants
import com.sandeepsingh.geotaggingapp.model.MarkerData
import com.sandeepsingh.geotaggingapp.repo.Prefs
import com.sandeepsingh.geotaggingapp.utilities.Utils

class MainActivity : FragmentActivity(), IFragmentToActivity {

    @BindView(R.id.tabs)
    lateinit var tabLayout : TabLayout

    @BindView(R.id.viewpager)
    lateinit var viewPager : ViewPager

    private var tabFragmentList = ArrayList<Fragment>()

    private var tabFragmentTitle = ArrayList<String>()

    lateinit var listFragment : ListFragment

    lateinit var mapFragment : MapViewFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        setupToolbar()
        setupViewPager()
        setupTabLayout()
       // askForPermission()
    }

    fun setupToolbar(){

        listFragment = ListFragment()
        mapFragment = MapViewFragment()
        tabFragmentList.add(mapFragment)
        tabFragmentList.add(listFragment)

        tabFragmentTitle.add("Map")
        tabFragmentTitle.add("List")
    }

    fun setupViewPager(){

        val mAdapter = object : FragmentStatePagerAdapter(supportFragmentManager){
            override fun getItem(p0: Int): Fragment {
                return tabFragmentList[p0]
            }

            override fun getCount(): Int {
                return tabFragmentList.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return tabFragmentTitle[position]
            }
        }

        viewPager.adapter = mAdapter

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(p0: Int) {

            }

        })
    }

    fun setupTabLayout(){
        tabLayout.tabMode = TabLayout.MODE_FIXED
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun newValueAdded(markerData: MarkerData) {
       listFragment.newValueAdded(markerData)
    }

    override fun onListItemClicked(latLng: LatLng) {
        mapFragment.moveToMarker(latLng)
        viewPager.setCurrentItem(0)
    }

    override fun onMarkerClickedListener(latLng: LatLng) {
        val markerDataList = Utils.fetchAllMarkerData(this)
        for (item in markerDataList.indices){
            val markerData = markerDataList[item]
            if (markerData.latitude == latLng.latitude && markerData.longitude == latLng.longitude){
                markerDataList.removeAt(item)
                markerDataList.add(0,markerData)
            }
        }

        Prefs.setList(this, StaticConstants.MY_MARKER_DATA,markerDataList)
        listFragment.newValueAdded(null)
        viewPager.setCurrentItem(1)
    }

}
