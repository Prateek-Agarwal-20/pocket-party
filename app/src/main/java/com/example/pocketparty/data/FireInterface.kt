package com.example.pocketparty.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.model.Document
import com.google.firebase.firestore.model.value.IntegerValue

class FireInterface(context: Context, userID: String) {

    private val db = FirebaseFirestore.getInstance()
    private val appContext = context
    private val userDataReference = db.collection("users").document(userID)
    private var namesList = ArrayList<String>()
    private lateinit var doc: DocumentSnapshot

    init {
        getUserData()
    }

    private fun getUserData() {
        userDataReference.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.i("letsSee", "here: ${document.data}")
                    doc = document
                    parseRecievedData()
                } else {
                    notifyFailure()
                }
            }
    }

    private fun parseRecievedData() {
        val totalList = doc.data
        totalList?.forEach { (key, value) ->
            namesList.add(key)
        }
        getCues("Checking")
    }

    fun getProjectNames(): List<String> {
        return namesList
    }

    fun getCues(projectName: String): ArrayList<LightingCueItem> {
        //Todo - figure out wtf is going on with this typing
        var unparsedString = doc.get(projectName).toString()
        unparsedString = unparsedString.removeSuffix("]")
        var stringList = unparsedString.split(",")
        var cutList = stringList.subList(1, stringList.size)
        var numList = ArrayList<Int>()
        cutList.forEach { x ->
            val trimmedNum = x.trim()
            numList.add(Integer.parseInt(trimmedNum))
        }
        var cueList = ArrayList<LightingCueItem>()
        for (x in 0..numList.size-1 step 2) {
            cueList.add(LightingCueItem(numList.get(x), numList.get(x + 1)))
        }
        Log.i("we got it LAG", cueList.toString())
        return cueList
    }

    fun saveProject(projectName: String, songName: String, cueList: ArrayList<LightingCueItem>) {
        var dataList = arrayListOf<Any>(songName)
        for (i in 0 until cueList.size) {
            dataList.add(cueList.get(i))
        }

        val docData = HashMap<String, Any>()
        docData[projectName] = dataList

        userDataReference.set(docData, SetOptions.merge())
            .addOnSuccessListener { Log.i("updateTag", "success!") }
            .addOnFailureListener { Log.i("updateTag", "failure :(") }
    }

    fun deleteProject(projectName: String) {
        val updates = HashMap<String, Any>()
        updates[projectName] = FieldValue.delete()
        userDataReference.update(updates).addOnCompleteListener {
            Toast.makeText(appContext, "Delete completed!", Toast.LENGTH_SHORT).show()
        }
    }

    fun addFakeCueList() {
        val projectName = "Checking"
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