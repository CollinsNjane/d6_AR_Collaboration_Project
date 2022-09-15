package com.example.prototype

import com.google.android.gms.maps.model.LatLng
import java.io.File

class Sites {

    private var sites = mutableListOf<Site>()//holds the information for each site
    //Database object

    init {
        sites.add(createSite("St Marks Church", LatLng(-33.93080278, 18.43131389),downloadtext("St Marks Church"),downloadFile("St Marks Church")))
        sites.add(createSite("Star Cinema", LatLng(-33.93061389, 18.43151944),downloadtext("St Marks Church"),downloadFile("St Marks Church"),))
        sites.add(createSite("Hanover Street", LatLng(-33.93021389, 18.43151111),downloadtext("St Marks Church"),downloadFile("St Marks Church")))
        sites.add(createSite("Seven Steps", LatLng(-33.92990556, 18.43067778),downloadtext("St Marks Church"),downloadFile("St Marks Church")))
        sites.add(createSite( "Wash House", LatLng(-33.92975278, 18.43036667),downloadtext("St Marks Church"),downloadFile("St Marks Church")))

    }

    public fun createSite(name:String, location:LatLng, text:ArrayList<String>,images:ArrayList<File>): Site
    {
        return Site(name,location,text,images)
    }

    public fun getNames(): MutableList<String>
    {
        val names = mutableListOf<String>()
        for (site in sites)
        {
            names.add(site.getName())
        }
        return names
    }


    public fun getSites(): MutableList<Site> {
        return sites
    }

    public fun getNumSites(): Int {
        return sites.size
    }

    fun downloadtext(name:String): ArrayList<String> {
        val temp = ArrayList<String>()
        return temp
    }

    fun downloadFile(name:String):ArrayList<File>
    {
        val temp  = ArrayList<File>()
        return temp
    }




}