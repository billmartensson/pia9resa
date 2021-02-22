package se.magictechnology.pia9resa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL


data class ApiData(val StopLocation: List<StopLoc>)

data class StopLoc(val id : String, val name: String)



data class TravelInfo(val Trip : List<Tripinfo>)
data class Tripinfo(val LegList : LegListInfo)
data class LegListInfo(val Leg : List<LegInfo>)
data class LegInfo(val Origin : StopTravelInfo, val Destination : StopTravelInfo)
data class StopTravelInfo(val name : String, val time : String)


class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.searchButton).setOnClickListener {

            val fromEditText = findViewById<EditText>(R.id.fromEditText)
            val toEditText = findViewById<EditText>(R.id.toEditText)

            launch {
                Log.d("pia9debug", "Starta hämtning")

                val fromStop = loadapi(fromEditText.text.toString())
                Log.d("pia9debug", fromStop.name)

                val toStop = loadapi(toEditText.text.toString())
                Log.d("pia9debug", toStop.name)

                Log.d("pia9debug", "Hämtning klar")

                findTrip(fromStop, toStop)


            }
        }

    }

    suspend fun loadapi(searchstring : String) : StopLoc
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


    suspend fun findTrip(fromStop : StopLoc, toStop : StopLoc)
    {
        Log.d("pia9debug", "Nu hitta resa")

        withContext(Dispatchers.IO) {

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
        }


    }

}