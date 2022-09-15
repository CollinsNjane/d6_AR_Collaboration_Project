package com.example.prototype

import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class Database(var imageView: ImageView) {

    private lateinit var storage:FirebaseStorage

    init {
//        createRef()
        downloadFile()
    }

    private fun createRef()
    {
        val storage = FirebaseStorage.getInstance()

        val storageRef = storage.reference //reference to local app storage
        val pathReference = storageRef.child("images/stars.jpg")
        val gsReference = storage.getReferenceFromUrl("gs://bucket/images/stars.jpg")// Create a reference to a file from a Google Cloud Storage URI
        val httpsReference = storage.getReferenceFromUrl(
            "https://firebasestorage.googleapis.com/b/bucket/o/images%20stars.jpg")

        var islandRef = storageRef.child("images/island.jpg")
        val localFile = File.createTempFile("images", "jpg")
        islandRef.getFile(localFile).addOnSuccessListener {
            // Local temp file has been created
        }.addOnFailureListener {
            // Handle any errors
        }


        //download straight to imageView
        // Reference to an image file in Cloud Storage
//        val storageReference = Firebase.storage.reference
        // ImageView in your Activity
//        val imageView = findViewById<ImageView>(R.id.imageView)
        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
//        Glide.with(this /* context */)
//            .load(storageReference)
//            .into(imageView)


    }


    private fun downloadFile() {
        Log.d("download","called here")
        val imagesArrayList = ArrayList<File>()
        val videosArrayList = ArrayList<File>()
        val storage = FirebaseStorage.getInstance()
        var storageRef = storage.reference


        // Create a reference to a file from a Google Cloud Storage URI
        //val gsReference = storage.getReferenceFromUrl("gs://bucket/cowboy-bebop-1.jpg")
        var counter = 0
        var imageNumber = 1

        val storageLocations = arrayOf<String>( "SevenSteps","Hanover", "Public Wash House", "StarCinema","stMarks")
        var pathRef = storageRef.child( "SevenSteps/images/")//the path to the image in storage
        var j = 0
        var image = ""
        while (counter != 4) {
            image = "image_$imageNumber"
            var localFile =
                File.createTempFile(image, ".jpg")//the name of the local file we are creating
            Log.d("download",localFile.toString())

            pathRef.getFile(localFile).addOnSuccessListener {
                Log.d("firebase ", ";local file created $localFile")
                val myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath())//creates bitmap of local file
                imagesArrayList.add(localFile)
                imageView.setImageBitmap(myBitmap)//display bitmap in image view
            }.addOnFailureListener { exception ->
                Log.e("firebase ", ";local tem file not created  created $exception")
            }


            image.substring(0,6) + imageNumber.toString()
            imageNumber++
            counter++
            j++
        }

    }




}