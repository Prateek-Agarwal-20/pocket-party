package com.example.pocketparty.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.*

class FireInterface {

    private val db = FirebaseFirestore.getInstance()
    private var appContext: Context
    private var userDataReference: DocumentReference
    private var namesList = ArrayList<String>()
    private lateinit var doc: DocumentSnapshot
    private var ready = false

    constructor(context: Context, userID: String) {
        appContext = context
        userDataReference = db.collection("users").document(userID)
    }

    fun getUserData() {
        userDataReference.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.i("letsSee", "here: ${document.data}")
                    doc = document
                    parseRecievedData()
                    ready = true
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
        Log.i("NAMES", "${namesList}")
    }

    fun getSingleCue(cueName: String) : LightingCue {
        return parseLightingCue(cueName)
    }

    fun getAllLightingCues():List<LightingCue> {
        while(!ready) {
            Log.i("WAITING", "for user initialization")
        }
        var projectNames = getProjectNames()
        Log.i("IN ALL CUES", "${projectNames}")
        var lightingCues = mutableListOf<LightingCue>()
        projectNames.forEach {project ->
            lightingCues.add(parseLightingCue(project))
        }
        Log.i("IN ALL CUES", lightingCues.size.toString())
        return lightingCues
    }

    fun getProjectNames(): List<String> {
        return namesList
    }

    fun parseLightingCue(projectName: String): LightingCue {
        var unparsedString = doc.get(projectName).toString()
        unparsedString = unparsedString.removeSuffix("]")
        var stringList = unparsedString.split(",")
        val trackName = stringList[1]
        val artistName = stringList[2]
        val trackImageLink = stringList[3]
        val spotifyUri = stringList[4]
        val cueArtistName = stringList[5]
        val cueArtistImageLink = stringList[6]
        val numPhones = stringList[7].trim().toInt()
        var cutList = stringList.subList(8, stringList.size)
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

        return LightingCue(projectName, Track(trackName, artistName, trackImageLink, spotifyUri), cueArtistName,
                            cueArtistImageLink, numPhones, mutableListOf<List<LightingCueItem>>(cueList))
    }

    // TODO: change cueList to a 2D array when supporting more than one phone
    fun saveProject(cueName: String, trackName: String, artistName:String,
                    numPhones: Int, cueArtistName: String, trackImageLink: String,
                    spotifyUri: String, cueArtistImageLink: String, cueList: ArrayList<LightingCueItem>) {
        var dataList = arrayListOf<Any>(cueName, trackName, artistName, trackImageLink, spotifyUri,
                                                        cueArtistName, cueArtistImageLink, numPhones)
        for (i in 0 until cueList.size) {
            dataList.add(cueList.get(i).startTime)
            dataList.add(cueList.get(i).endTime)
        }

        val docData = HashMap<String, Any>()
        docData[cueName] = dataList

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
        val cueName = "Checking"
        val cueArtistName = "Boi"
        val cueArtistImageLink = "none"
        val trackName = "Pure Imagination"
        val artistName = "artist"
        val trackImageLink = "none"
        val spotifyUri = "none"
        val numPhones = 1

        val cueList = ArrayList<LightingCueItem>()
        for (i in 0 until 20) {
            cueList.add(LightingCueItem(i * 1000, (i + 1) * 1000))
        }

        var dataList = arrayListOf<Any>(cueName, trackName, artistName, trackImageLink, spotifyUri,
                                                        cueArtistName, cueArtistImageLink, numPhones)

        for (i in 1 until 20 step 2) {
            dataList.add(cueList[i].startTime)
            dataList.add(cueList[i].endTime)
        }

        val docData = HashMap<String, Any>()
        docData[cueName] = dataList

        userDataReference.set(docData, SetOptions.merge())
            .addOnSuccessListener { Log.i("updateTag", "success!") }
            .addOnFailureListener { Log.i("updateTag", "failure :(") }
    }


    private fun notifyFailure() {
        Toast.makeText(appContext, "Failed to get user data, please log in again", Toast.LENGTH_LONG).show()
    }

}