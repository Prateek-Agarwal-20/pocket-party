package com.example.pocketparty

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.pocketparty.data.FireInterface

class FirebaseTestingActivity : AppCompatActivity() {

    private lateinit var fbInterface: FireInterface
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_testing)

        setUpFireInterface()
    }

    private fun setUpFireInterface(){
        userID = intent.getStringExtra("userID")
        if(userID.equals(null)){
            Toast.makeText(this, "failed to get userID", Toast.LENGTH_LONG).show()
        }
        fbInterface = FireInterface(this, userID)
    }

    fun createFakeCuesClick(view: View){
        fbInterface.addFakeCueList()
    }
}
