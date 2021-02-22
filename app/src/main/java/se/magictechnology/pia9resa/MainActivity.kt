package se.magictechnology.pia9resa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var viewmodel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewmodel = ViewModelProvider(this).get(MainViewModel::class.java)

        findViewById<Button>(R.id.searchButton).setOnClickListener {

            val fromEditText = findViewById<EditText>(R.id.fromEditText)
            val toEditText = findViewById<EditText>(R.id.toEditText)

            viewmodel.loadTravel(fromEditText.text.toString(), toEditText.text.toString())

        }

        viewmodel.getTravelString().observe(this) {
            findViewById<TextView>(R.id.resultTextView).text = it
        }

        viewmodel.getErrorString().observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }



}