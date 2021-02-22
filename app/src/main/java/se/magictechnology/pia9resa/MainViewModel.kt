package se.magictechnology.pia9resa

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class MainViewModel : ViewModel(), CoroutineScope by MainScope() {

    private var travelString = MutableLiveData<String>()

    fun getTravelString() : LiveData<String>
    {
        return travelString
    }

    private var errorString = MutableLiveData<String>()

    fun getErrorString() : LiveData<String>
    {
        return errorString
    }

    fun loadTravel(fromText : String, toText : String)
    {
        launch {
            Log.d("pia9debug", "Starta hämtning")

            val fromStop = loadapi(fromText)
            Log.d("pia9debug", fromStop.name)

            if(fromStop.id == "")
            {
                errorString.value = "Felaktig start"
                return@launch
            }

            val toStop = loadapi(toText)
            Log.d("pia9debug", toStop.name)

            if(toStop.id == "")
            {
                errorString.value = "Felaktig slut"
                return@launch
            }

            Log.d("pia9debug", "Hämtning klar")

            val timetable = findTrip(fromStop, toStop)

            var traveltext = ""

            for (leg in timetable.Trip[0].LegList.Leg)
            {

                traveltext += leg.Origin.name
                traveltext += " "
                traveltext += leg.Origin.time
                traveltext += "\n"
                traveltext += leg.Destination.name
                traveltext += " "
                traveltext += leg.Destination.time
                traveltext += "\n"
                traveltext += "*******"
                traveltext += "\n"


            }

            travelString.value = traveltext
        }
    }





    private suspend fun loadapi(searchstring : String) : StopLoc
    {
        return withContext(Dispatchers.IO) {
            val theurl = URL("https://api.resrobot.se/location.name.json?key=47898ae7-fbde-4641-a2bd-d5842a0eb67d&input="+searchstring)
            /*
            var resultString = theurl.readText()
            Log.d("pia9debug", resultString)
            */

            val theConnection = (theurl.openConnection() as? HttpURLConnection)!!.apply {
                requestMethod = "GET"
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
            }

            val reader = BufferedReader(theConnection.inputStream.reader())

            val theResultString = reader.readText()

            val theStops = Gson().fromJson(theResultString, ApiData::class.java)

            return@withContext theStops.StopLocation[0]
            /*
            withContext(Dispatchers.Main) {
                Log.d("pia9debug", "RESULTAT")
                Log.d("pia9debug", theResultString)

                for(somestop in theStops.StopLocation)
                {
                    Log.d("pia9debug", somestop.name)
                    findViewById<TextView>(R.id.resultTextView).text = somestop.id
                }
            }
            */

        }

    }


    private suspend fun findTrip(fromStop : StopLoc, toStop : StopLoc) : TravelInfo
    {
        Log.d("pia9debug", "Nu hitta resa")

        return withContext(Dispatchers.IO) {

            val theurl = URL("https://api.resrobot.se/v2/trip?key=47898ae7-fbde-4641-a2bd-d5842a0eb67d&originId=" + fromStop.id + "&destId="+ toStop.id +"&format=json")

            Log.d("pia9debug", theurl.toString())

            val theConnection = (theurl.openConnection() as? HttpURLConnection)!!.apply {
                requestMethod = "GET"
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
            }

            val reader = BufferedReader(theConnection.inputStream.reader())

            val theResultString = reader.readText()

            val thetravel = Gson().fromJson(theResultString, TravelInfo::class.java)

            Log.d("pia9debug", thetravel.Trip[0].LegList.Leg[0].Origin.time)
            Log.d("pia9debug", thetravel.Trip[0].LegList.Leg[0].Destination.time)

            return@withContext thetravel

        }


    }



}