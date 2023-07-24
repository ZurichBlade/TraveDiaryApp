package com.berry.traveldiary

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.berry.traveldiary.data.MyDatabase
import com.berry.traveldiary.databinding.ActivityDiaryEntryBinding
import com.berry.traveldiary.model.DiaryEntries
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale


class DiaryEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiaryEntryBinding
    private lateinit var myDatabase: MyDatabase
    private var currentLocation: Location? = null
    lateinit var locationManager: LocationManager

    private var locationByGps: Location? = null
    private var locationByNetwork: Location? = null
    private var currentLocationString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryEntryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        myDatabase = MyDatabase.getDatabase(this@DiaryEntryActivity)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


//        if (isLocationPermissionGranted()) {
        if (hasGps) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0F,
                gpsLocationListener
            )
        }

        if (hasNetwork) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                networkLocationListener
            )
        }
//        }


        val lastKnownLocationByGps =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        lastKnownLocationByGps?.let {
            locationByGps = lastKnownLocationByGps
        }
//------------------------------------------------------//
        val lastKnownLocationByNetwork =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        lastKnownLocationByNetwork?.let {
            locationByNetwork = lastKnownLocationByNetwork
        }
//------------------------------------------------------//
        if (locationByGps != null && locationByNetwork != null) {
            if (locationByGps!!.accuracy > locationByNetwork!!.accuracy) {
                currentLocation = locationByGps
                val latitude = currentLocation?.latitude
                val longitude = currentLocation?.longitude
                currentLocationString =
                    getCompleteAddressString(latitude!!.toDouble(), longitude!!.toDouble())
                // use latitude and longitude as per your need
            } else {
                currentLocation = locationByNetwork
                val latitude = currentLocation?.latitude
                val longitude = currentLocation?.longitude
                currentLocationString =
                    getCompleteAddressString(latitude!!.toDouble(), longitude!!.toDouble())
                // use latitude and longitude as per your need
            }
        } else {
            /*temp code*/

//            currentLocation = locationByGps
//            val latitude = currentLocation?.latitude
//            val longitude = currentLocation?.longitude
//            currentLocationString =
//                getCompleteAddressString(latitude!!.toDouble(), longitude!!.toDouble())
        }

        if (currentLocationString != null) {
            binding.edtLocation.setText(currentLocationString)
        }





        binding.btnSave.setOnClickListener {
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 400) }
            saveDiaryEntry(it)
        }


    }


    val gpsLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationByGps = location
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    val networkLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationByNetwork = location
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }


    private fun saveDiaryEntry(view: View) {
        if (isValid(view)) {
            insertTOdb()
        }

    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String? {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.w("My Current loction address", strReturnedAddress.toString())
            } else {
                Log.w("My Current loction address", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("My Current loction address", "Canont get Address!")
        }
        return strAdd
    }

    private fun insertTOdb() {
        val title = binding.edtTitle.text.toString()
        val date = binding.edtDate.text.toString()
        val location = binding.edtLocation.text.toString()
        val desc = binding.edtDesc.text.toString()


        val diaryEntries = DiaryEntries(0, title, date, location, desc)

        CoroutineScope(Dispatchers.IO).launch {
            myDatabase.diaryEntryDao().addDiaryEntry(diaryEntries)
        }

        val returnIntent = Intent()
        returnIntent.putExtra("result", 122)
        setResult(RESULT_OK, returnIntent)
        finish()



    }

    private fun isValid(view: View): Boolean {
        val title = binding.edtTitle.text.toString()
        val date = binding.edtDate.text.toString()
        val location = binding.edtLocation.text.toString()
        val desc = binding.edtDesc.text.toString()
        return if (TextUtils.isEmpty(title)) {
            Snackbar.make(view, "Please enter title", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            false
        } else if (TextUtils.isEmpty(date)) {
            Snackbar.make(view, "Please enter date", Snackbar.LENGTH_LONG).setAction("Action", null)
                .show()
            false
        } else if (TextUtils.isEmpty(location)) {
            Snackbar.make(view, "Please enter location", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            false
        } else if (TextUtils.isEmpty(desc)) {
            Snackbar.make(view, "Please enter description", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            false
        } else {
            true
        }


    }

    private fun isLocationPermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                121
            )
            false
        } else {
            true
        }
    }


}