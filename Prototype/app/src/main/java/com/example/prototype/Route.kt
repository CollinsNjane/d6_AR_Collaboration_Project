package com.example.prototype

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


@SuppressLint("MissingPermission")
class Route(val map: GoogleMap, val sites:Sites, private val fusedLocationClient: FusedLocationProviderClient, val mapsActivity: MapsActivity) {

    private  var options:PolylineOptions = PolylineOptions()
    private  var routeLine: Polyline = map.addPolyline(options)
    private  var externalOptions:PolylineOptions = PolylineOptions()
    private var externalRouteLine:Polyline = map.addPolyline(externalOptions)


    private var externalPoints= ArrayList<LatLng?>()

    var data = ""
    private val D6 = LatLng(-33.930839, 18.431435)

    var routePoints = linkedMapOf<String,LatLng>(
        "test loc 1" to LatLng(-33.938725,18.472302),
        "test loc 2" to LatLng(-33.938707,18.472173),
        "test loc 3" to LatLng(-33.938739,18.472055),
        "St Marks Church" to LatLng(-33.930839, 18.431435),
        "intersection 1" to LatLng(-33.930754, 18.431478),
        "Star Cinema" to LatLng(-33.930607, 18.431505),
        "intersection 2" to LatLng(-33.930460, 18.431189),
        "intersection 3" to LatLng(-33.930149, 18.431361),
        "Hanover Street" to LatLng(-33.930166, 18.431458),
        "Seven Steps" to LatLng(-33.929877, 18.430728),
        "Wash House" to LatLng(-33.929815, 18.430497)
    )
    var routeToD6 = listOf<LatLng>()

    @SuppressLint("MissingPermission")
    public fun updateNavWithinD6(currentLocation: Location,prev:Site,dest: Site) {
        Log.d("UpdateNav", "navigation is updated ")
        routeLine.remove()
        val points = getPointsOnRoute(prev,dest,LatLng(currentLocation.latitude,currentLocation.longitude))
        options = PolylineOptions()
        options.add(LatLng(currentLocation.latitude,currentLocation.longitude))
        Log.d("Points", points.size.toString())
        options.addAll(points)
//        options.add(LatLng(currentLocation.latitude,currentLocation.longitude))
//        options.add(dest.getLocation())
        options.width(30f)
        options.color(Color.BLUE)
        routeLine = map.addPolyline(options)
    }

    private fun getPointsOnRoute(prev:Site,next:Site, currentLocation: LatLng): MutableList<LatLng> {
        var points = mutableListOf<LatLng>()
//        points.add(next.getLocation())

        var startPos:Int = 0
        var endPos:Int = 0

        var counter = 0
        for(key in routePoints.keys)
        {
            if(key == prev.getName())startPos = counter
            if(key == next.getName())endPos = counter
            counter++
        }


        points  = routePoints.values.toList().slice(startPos..endPos) as MutableList<LatLng>

        for(point in points)
        {
            if(this.isCloser(currentLocation,point,next.getLocation()))
            {
                points.remove(point)
            }
            else break
        }


        for (p in points)
        {
            for((key,value) in routePoints)
            {
                if(p==value)
                {
                    Log.d("locations", key.toString())
                }
            }
        }

        return points
        //add all points between previous site and current site if they are closer than the user
    }

    private fun isCloser(point1:LatLng, point2: LatLng, dest:LatLng): Boolean
    {//returns true if point1 is closer than point2 to the destination
        if(SphericalUtil.computeDistanceBetween(point1,dest)<SphericalUtil.computeDistanceBetween(point2,dest))return true
        return false
    }

    public fun initRouteToD6()
    {//gets the path from current location to D6
        val fullRoute = FullRoute()
        fullRoute.plotRoute(this)
    }

    public fun updateNavToD6(currentLocation: Location)
    {
        externalRouteLine.remove()
        externalOptions.addAll(getUpdatedPoints(currentLocation,externalPoints))
        externalOptions.width(20f)
        externalOptions.color(Color.BLUE)
        map.addPolyline(externalOptions)
    }

    private fun getUpdatedPoints(currentLocation: Location,points:ArrayList<LatLng?>): MutableList<LatLng?> {
        //get a list of the points between you and D6 to update route line
        var num = 0
        for(point in points)
        {
            val yourDistance = FloatArray(1)
            val pointDistance = FloatArray(1)
            if (point != null) {
                Location.distanceBetween(currentLocation.latitude,currentLocation.longitude,D6.latitude,D6.longitude,yourDistance)
                Location.distanceBetween(point.latitude,point.longitude,D6.latitude,D6.longitude,pointDistance)
            }
            if(yourDistance[0]<pointDistance[0])//you are closer than this point, therefore do not plot this point
            {
                num++
            }
            else break
        }

        if(points.size>0) return points.subList(num,points.size-1)
        else return points.subList(num,points.size)

    }

    @SuppressLint("MissingPermission")
    suspend fun downloadJson() {
        return withContext(Dispatchers.IO)
        {
            val currentLocation = mapsActivity.getLocation()
            val loc = currentLocation?.let { LatLng(it.latitude,currentLocation.longitude) }
            var url = URL(getURL(loc,D6,"driving"))
            val connection = url.openConnection()

            BufferedReader(InputStreamReader(connection.getInputStream())).use{ inp ->
                var line: String?
                while (inp.readLine().also { line = it } != null)
                {
                    data+=line
//                    Log.d("Dataline", line.toString())
                }
            }
            Log.d("PathData", data)
        }
    }

    private fun getURL(from: LatLng?, to: LatLng?, mode:String): String {
        val origin = "origin=" + from?.latitude + "," + from?.longitude
        val dest = "destination=" + to?.latitude + "," + to?.longitude
        val sensor = "sensor=false"
        val mode2 = "mode=$mode"
        val key = "key=AIzaSyBVWGDmi4MY5uw3sKaGdyZlw_2_bZ8vaB4"
        val params = "$origin&$dest&$sensor&$mode2&$key"
        Log.d("URL", "https://maps.googleapis.com/maps/api/directions/json?$params" )
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }

    fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>> {
       //https://www.specbee.com/blogs/android-tutorials-google-map-drawing-routes-between-two-points
        val jObject: JSONObject
        var routes: List<List<HashMap<String, String>>> = emptyList()
        try {
            jObject = JSONObject(jsonData[0])
            Log.d("ParserTask", jsonData[0])
            val parser = DataParser()
            Log.d("ParserTask", parser.toString())

            // Starts parsing data
            routes = parser.parse(jObject)
            Log.d("ParserTask", "Executing routes")
            Log.d("ParserTask", routes.toString())
        } catch (e: Exception) {
            Log.d("ParserTask", e.toString())
            e.printStackTrace()
        }
        return routes
    }

    // Executes in UI thread, after the parsing process
     fun onPostExecute(result: List<List<HashMap<String, String>>>) {
        var points: ArrayList<LatLng?>
        var lineOptions: PolylineOptions? = null

        // Traversing through all the routes
        for (i in result.indices) {
            points = ArrayList()
            lineOptions = PolylineOptions()

            // Fetching i-th route
            val path = result[i]

            // Fetching all the points in i-th route
            for (j in path.indices) {
                val point = path[j]
                val lat = point["lat"]!!.toDouble()
                val lng = point["lng"]!!.toDouble()
                val position = LatLng(lat, lng)
                points.add(position)
            }

            externalPoints = points
            externalOptions.addAll(points)
            externalOptions.width(20f)
            externalOptions.color(Color.BLUE)
            Log.d("onPostExecute", "onPostExecute lineoptions decoded")
        }

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null) {
            map.addPolyline(externalOptions)
        } else {
            Log.d("onPostExecute", "without Polylines drawn")
        }
    }

}
    class FullRoute(): ViewModel() {
        fun plotRoute(route: com.example.prototype.Route){
            viewModelScope.launch(Dispatchers.IO) {
                route.downloadJson()
                var output = route.doInBackground(route.data)
                withContext (Dispatchers.Main) {
                    route.onPostExecute(output)
                }

            }

        }

    }



