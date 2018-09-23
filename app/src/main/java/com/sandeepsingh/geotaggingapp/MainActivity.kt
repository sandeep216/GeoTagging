package com.sandeepsingh.geotaggingapp

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import com.sandeepsingh.geotaggingapp.model.MarkerData
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity(),IFragmentToActivity {

    @BindView(R.id.tabs)
    lateinit var tabLayout : TabLayout

    @BindView(R.id.viewpager)
    lateinit var viewPager : ViewPager

    private var tabFragmentList = ArrayList<Fragment>()

    private var tabFragmentTitle = ArrayList<String>()

    lateinit var listFragment : ListFragment

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
        tabFragmentList.add(MapViewFragment())
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


     override fun askForPermission() {
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions( this, Array(1){  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    StaticConstants.MY_PERMISSION_ACCESS_COURSE_LOCATION)

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            val fragment = MapViewFragment()
            if (fragment != null) {
              //  fragment.getCurrentLocation()
            }
        }
    }

    override fun newValueAdded(markerData: MarkerData) {
       listFragment.newValueAdded(markerData)
    }
}
