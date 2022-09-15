package com.example.prototype

import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.NonNull
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
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

    }




}