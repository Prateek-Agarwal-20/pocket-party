package com.example.pocketparty.data

import com.google.firebase.auth.FirebaseUser

object FirestoreSingleton  {
    var fstore : FireInterface? = null
    var user: FirebaseUser? = null
}