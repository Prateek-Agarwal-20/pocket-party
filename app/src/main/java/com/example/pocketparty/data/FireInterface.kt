package com.example.pocketparty.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireInterface(context: Context, userID: String) {

    private val db = FirebaseFirestore.getInstance()
    private val appContext = context
    private val userDataReference = db.collection("users").document(userID)

    init {
        getUserData()
    }

    private fun getUserData() {
        userDataReference.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.i("letsSee", "here: ${document.data}")
                } else {
                    notifyFailure()
                }
            }
    }

    fun addFakeCueList() {
        val projectName = "Hella lit light cues for Pure Imagination Remix"
        val songName = "Pure Imagination"
        val cueList = ArrayList<LightingCueItem>()
        for (i in 0 until 20) {
            cueList.add(LightingCueItem(i * 1000, (i + 1) * 1000))
        }

        var dataList = arrayListOf<Any>(songName)

        for (i in 1 until 20 step 2) {
            dataList.add(cueList[i].startTime)
            dataList.add(cueList[i].endTime)
        }

        val docData = HashMap<String, Any>()
        docData[projectName] = dataList

        userDataReference.set(docData, SetOptions.merge())
            .addOnSuccessListener { Log.i("updateTag", "success!") }
            .addOnFailureListener { Log.i("updateTag", "failure :(") }
    }

    private fun notifyFailure() {
        Toast.makeText(appContext, "Failed to get user data, please log in again", Toast.LENGTH_LONG).show()
    }

}

