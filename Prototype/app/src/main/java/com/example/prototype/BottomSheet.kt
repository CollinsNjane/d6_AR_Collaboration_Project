package com.example.prototype

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class BottomSheet: Fragment(R.layout.bottom_sheet_dialog_layout) {
    // Initializing the ImageView
    var rImage: ImageView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.bottom_sheet_dialog_layout, container, false)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var btnPrev = getView()?.findViewById<Button>(R.id.btn_prev)
        var btnNext = getView()?.findViewById<Button>(R.id.btn_next)
        // getting ImageView by its id
        rImage = getView()?.findViewById(R.id.stMarksImage1);
        // we will get the default FirebaseDatabase instance
        val firebaseDatabase = FirebaseDatabase.getInstance()
        // we will get a DatabaseReference for the database root node
        val databaseReference = firebaseDatabase.reference
        // Here "image" is the child node value we are getting
        // child node data in the getImage variable
        val getImage = databaseReference.child("imageLanding")
        // Adding listener for a single change
        // in the data at this location.
        // this listener will triggered once
        // with the value of the data at the location
        getImage.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                // getting a DataSnapshot for the location at the specified
                // relative path and getting in the link variable
                val link = dataSnapshot.getValue(String::class.java)

                // loading that data into rImage
                // variable which is ImageView
                Picasso.get().load(link).into(rImage)
            }

            // this will called when any problem
            // occurs in getting data
            override fun onCancelled(@NonNull databaseError: DatabaseError) {
                // we are showing that error message in toast
                //Toast.makeText(this, "Error Loading Image", Toast.LENGTH_SHORT).show()
            }
        })


    }
}


