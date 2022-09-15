package com.example.prototype


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.data.Layer


class MapsActivity : AppCompatActivity(),OnMapReadyCallback, LocationListener {


    private lateinit var mMap: GoogleMap

    var locationManager: LocationManager? = null
    lateinit var locationProvider: String
    lateinit var kmlLayer: Layer

    var startTour = false


    private val sites = Sites()
    private val D6 = LatLng(-33.930839, 18.431435)

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationPermissionGranted = false

    private lateinit var route:Route
    private var prev:Site = sites.getSites()[0]
    private var next:Site = sites.getSites()[0]

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)




    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap)
    {

//        val btnStart = findViewById<Button>(R.id.btn_tour)
//        Log.d("listenerSet", "button should work afyer this")
//        btnStart.setOnClickListener(View.OnClickListener()
//        {
//            Log.d("listenerSet", "button is clicked")
//            fun onClick(v:View)
//            {
//                Log.d("ButtonClick", "button is clicked")
//                startTour()
//            }
//        })



        mMap = googleMap
        route =  Route(mMap,sites,fusedLocationClient,this)
        this.initializeLocationManager();
        enableMyLocation()
        createD6()
        setCorrectZoom()
        controlNextPrevButtons(route)
        route.initRouteToD6()//navigate to d6
    }

    @SuppressLint("MissingPermission")
    private fun setCorrectZoom()
    {
        val builder = LatLngBounds.Builder()
        var currentLoc: LatLng

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                val currentLoc = LatLng(location.latitude, location.longitude) //gets user current location
                Log.d("location", currentLoc.toString())
                builder.include(currentLoc) //adds the two locations to the builder
                builder.include(D6)
                val bounds: LatLngBounds = builder.build()
                val width = resources.displayMetrics.widthPixels
                val height = resources.displayMetrics.heightPixels
                val padding =
                    (width * 0.10).toInt() // offset from edges of the map 10% of screen
                val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
                mMap.animateCamera(cu)
            }
        }

    }

    private fun setD6Markers()//sets the markers for each d6 site
    {
        for (site in sites.getSites())
        {
            mMap.addMarker(MarkerOptions().position(site.getLocation()).title(site.getName()))
        }
    }

    private fun createD6()
    {
        //creates the polygon around D6
        mMap.addMarker(MarkerOptions().position(D6).title("District 6"))
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            locationPermissionGranted = true
            return
        }
        val perms = arrayOf("android.permission.ACCESS_FINE_LOCATION")
        // 2. Otherwise, request location permissions from the user.
        ActivityCompat.requestPermissions(this, perms, 200)
    }

    @SuppressLint("MissingPermission")
    private fun startTour()
    {
        Log.d("StartTour", "Tour has started")
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap.clear()//removes all previous points and polylines
        startTour = true
        setD6Markers()
    }

    private fun controlNextPrevButtons(route:Route)
    {
        var counter = 0
        val siteObjects = sites.getSites()

        val btnNext = findViewById<Button>(R.id.btn_next)
        val btnPrev = findViewById<Button>(R.id.btn_prev)
        val tvSite = findViewById<TextView>(R.id.tv_site)

        btnPrev.setVisibility(View.INVISIBLE)
        tvSite.setText(siteObjects[counter].getName())

        if (btnNext != null) {
            btnNext.setOnClickListener {
                counter++
                setButtonVisibility(counter,btnNext,btnPrev)
                prev = siteObjects[counter-1]
                next = siteObjects[counter]
                tvSite.setText(siteObjects[counter].getName())
                setSiteContent(siteObjects[counter])
            }
        }
        if (btnPrev != null) {
            btnPrev.setOnClickListener {
                counter--
                setButtonVisibility(counter,btnNext,btnPrev)
                if(counter != 0)prev = siteObjects[counter-1]
                next = siteObjects[counter]
                tvSite.setText(siteObjects[counter].getName())
                setSiteContent(siteObjects[counter])
            }
        }

    }

    fun setButtonVisibility(counter: Int, btnNext:Button, btnPrev:Button)
    {
        val numSites = sites.getNumSites()
        when(counter)
        {
            0->btnPrev.setVisibility(View.INVISIBLE)
            numSites-1->btnNext.setVisibility(View.INVISIBLE)
            else -> {btnPrev.setVisibility(View.VISIBLE)
                btnNext.setVisibility(View.VISIBLE)}
        }
    }

    private fun setSiteContent(site:Site)
    {
        val images = site.getImages()
        if(images.size>=1)
        {
            val myBitmap = BitmapFactory.decodeFile(images[0].getAbsolutePath())//creates bitmap of local file
            findViewById<ImageView>(R.id.stMarksImage1).setImageBitmap(myBitmap)
        }

        val text = site.getText()
        if(text.size>=2)
        {
            findViewById<TextView>(R.id.intro).setText(text[0])
            findViewById<TextView>(R.id.mainText).setText(text[1])
        }

        //get bottom sheet
        //get info that must be displayed on site
        //update bottom sheet
    }


    override fun onLocationChanged(loc: Location) {
        Log.d("LocationChanges", "onLocationChanged: ")
        var btnStart = findViewById<Button>(R.id.btn_tour)
        if(atD6())//they have arrived at d6 make start tour button visible
        {
            btnStart.visibility = View.VISIBLE
        }
        else if(btnStart.visibility == View.VISIBLE )//they have left D6 make start tour button invisible
        {
            btnStart.visibility = View.GONE
        }

        if(startTour)//navigation within district 6
        {
            route.updateNavWithinD6(loc,prev,next)
        }
        else
        {
            route.updateNavToD6(loc)
        }

    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationManager() {

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        locationProvider = locationManager!!.getBestProvider(criteria, false)!!
        val location = locationManager!!.getLastKnownLocation(locationProvider)
        location?.let { onLocationChanged(it) }
        this.locationManager?.requestLocationUpdates(locationProvider, 100, 0.1.toFloat(), this)
    }


   @SuppressLint("MissingPermission")
   fun getLocation(): Location? {
       val criteria = Criteria()
       locationProvider = locationManager!!.getBestProvider(criteria, false)!!
       val location = locationManager!!.getLastKnownLocation(locationProvider)
       return location
   }

    private fun atD6():Boolean
    {
        val currentLocation = getLocation()
        val locLatLng = currentLocation?.let { LatLng(it.latitude,currentLocation.longitude) }
        val result = FloatArray(1)
        if (locLatLng != null) {
            Location.distanceBetween(locLatLng.latitude,locLatLng.longitude,D6.latitude,D6.longitude,result)
        }
        if(result[0]>10)//you are outside d6x
        {
            return false
        }

        return true
    }


}






