package `in`.bookmyshow.positioningsystem

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity : AppCompatActivity() {
    class GPS( var lat: Double, var lon : Double )
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var errorMargin: Float? = null
    private var userLocation: GPS = GPS(0.0,0.0)
    private val topRight: GPS = GPS(19.112722, 72.841706)
    private val topLeft: GPS = GPS(19.112388, 72.841696)
    private val botRight: GPS = GPS(19.112722, 72.842341)
    private val botLeft: GPS = GPS(19.112388, 72.842341)
    var pixelPerMeter: Float? = null

    private val LOCATION_REQUEST_CODE : Int = 1

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_location)

        ivMapImage.post({
            getPixelScale()
        })

        locationRequest = LocationRequest.create()
                .setInterval(1000)
                .setFastestInterval(0)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                userLocation.lat = locationResult!!.lastLocation.latitude
                userLocation.lon = locationResult.lastLocation.longitude
                errorMargin = locationResult.lastLocation.accuracy * pixelPerMeter!!
                updateUserLocation()
            }

        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        // We can use this task to check for success or failure, for now working under the assumption that it is successful
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@LocationActivity)
        if ( ContextCompat.checkSelfPermission(this@LocationActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            // check permission here
            ActivityCompat.requestPermissions(this@LocationActivity,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            LOCATION_REQUEST_CODE -> {
                if ( grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
                } else if ( grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(this@LocationActivity,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
                }
            }
        }
    }

    /**
     * getPixelScale
     * Calculates how many pixels per meter is being used in rendering the map image
     */
    private fun getPixelScale() {
        val pixelDistance = ivMapImage.width
        val meterDistance = getMeterDistance(topLeft, topRight)
        pixelPerMeter = Math.round(pixelDistance / meterDistance).toFloat()
        Log.d("PixelPerMeter", ""+pixelPerMeter)
    }

    /**
     * getMeterDistance
     * @param source
     * @param destination
     * @return distance between source and destination
     */
    private fun getMeterDistance(source: GPS, destination :GPS): Double {
        val R = 6371000 // Earth radius in meters
        // Haversines formula
        val dLat: Double = Math.abs(Math.toRadians(destination.lat) - Math.toRadians(source.lat))
        val dLon: Double = Math.abs(Math.toRadians(destination.lon) - Math.toRadians(source.lon))
        val a = (Math.sin(dLat/2) * Math.sin(dLat/2)) + (Math.cos(destination.lat)) * (Math.cos(source.lat)) * (Math.sin(dLon/2) * Math.sin(dLon/2))
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return Math.abs(R * c)
    }

    /**
     * updateUserLocation
     * calculates relative position of user and updates position on map
     */
    private fun updateUserLocation() {
        val dLat: Double = Math.abs(topRight.lat - userLocation.lat)
        val dLon: Double = Math.abs(botLeft.lon - userLocation.lon)

        val dLatM: Double = dLat * 111000
        val dLonM: Double = dLon * 111000

        val dLatP: Float = dLatM.toFloat() * pixelPerMeter!!
        val dLonP: Float = dLonM.toFloat() * pixelPerMeter!!

        // x and y for the user view
        val x = (ivMapImage.x + ivMapImage.width) - dLatP
        val y = (ivMapImage.y + ivMapImage.height) - dLonP

        ivUserLoc.x = x - ivUserLoc.width/2
        ivUserLoc.y = y - ivUserLoc.height/2

        ivErrorMargin.requestLayout()
        ivErrorMargin.layoutParams.width = Math.round(errorMargin!!)
        ivErrorMargin.layoutParams.height = Math.round(errorMargin!!)

        ivErrorMargin.x = ivUserLoc.x - ivErrorMargin.width/2
        ivErrorMargin.y = ivUserLoc.y - ivErrorMargin.height/2

        ivUserLoc.visibility = View.VISIBLE
        ivErrorMargin.visibility = View.VISIBLE
    }
}
