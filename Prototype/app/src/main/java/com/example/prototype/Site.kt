package com.example.prototype

import com.google.android.gms.maps.model.LatLng
import java.io.File

class Site(private val name:String,private var location:LatLng, private var text:ArrayList<String>, private var images:ArrayList<File>) {

    private var videos = ArrayList<File>()

    @JvmName("getName1")
    public fun getName(): String {
        return name
    }

    @JvmName("getLocation1")
    public fun getLocation(): LatLng {
        return location
    }

    public fun updateLocation(newLocation:LatLng)
    {
        location = newLocation
    }

    fun setVideos(videoFiles:Array<File>)
    {//adds all the videos to the array
        for (file in videoFiles)
        {
            videos[videos.size] = file
        }
    }

    fun getImages(): ArrayList<File> {
        return images
    }
    fun getText(): ArrayList<String> {
        return text
    }
    fun getVideos(): ArrayList<File> {
        return videos
    }

}