package com.suraj.tracklocationapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.*
import com.google.maps.android.clustering.ClusterManager
import com.suraj.tracklocationapp.R
import com.suraj.tracklocationapp.model.UserCluster
import com.suraj.tracklocationapp.model.UserLocation
import com.suraj.tracklocationapp.utils.General
import com.suraj.tracklocationapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_location_history.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.clustering.Cluster
import com.google.android.gms.maps.model.LatLngBounds
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.suraj.tracklocationapp.adapter.LocationAdapter
import androidx.recyclerview.widget.PagerSnapHelper
import com.blankj.utilcode.util.ToastUtils

class LocationHistory : AppCompatActivity(), OnMapReadyCallback,
    ClusterManager.OnClusterClickListener<UserCluster>,
    ClusterManager.OnClusterItemClickListener<UserCluster> {

    private var mMap: GoogleMap?=null
    private lateinit var mainViewModel: MainViewModel
    lateinit var mClusterManager : ClusterManager<UserCluster>
    private var adapter: LocationAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_history)
        General.setToolbar(this, toolbar_history)
        init()
    }

    private fun init(){
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_history) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        mainViewModel.getLocations().observe(this, Observer<List<UserLocation>> { user_locations ->
            if (user_locations.isEmpty()){
                ToastUtils.showLong("Location not found, please start your location")
                return@Observer
            }
            adapter?.setList(user_locations)
            for (i in user_locations.indices){
                mClusterManager = ClusterManager(this, mMap)
                mainViewModel.setUpClusterer(mMap, mClusterManager, user_locations[i].lat,
                    user_locations[i].lng, user_locations, this)
            }
        })

        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_map_card.layoutManager = linearLayoutManager
        adapter = LocationAdapter(this)
        rv_map_card.adapter = adapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rv_map_card)

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClusterClick(cluster: Cluster<UserCluster>?): Boolean {
        val builder = LatLngBounds.builder()
        for (item in cluster?.items!!) {
            builder.include(item.position)
        }
        val bounds = builder.build()
        mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

        return true
    }

    override fun onClusterItemClick(cluster: UserCluster?): Boolean {
        sliding_layout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        return true
    }
}
