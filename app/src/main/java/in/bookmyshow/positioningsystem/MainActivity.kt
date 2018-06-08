package `in`.bookmyshow.positioningsystem
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btWiFi.setOnClickListener { _ ->
            if ( !checkForProviders() ) {
                Toast.makeText(this,"Please enable wifi and GPS", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, WiFiScanActivity::class.java ))

        } }

        btLoc.setOnClickListener { _ ->
            if ( !checkForProviders() ) {
                Toast.makeText(this,"Please enable wifi and GPS", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, LocationActivity::class.java ))

            } }
    }

    private fun checkForProviders() : Boolean {
        return (applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager).isWifiEnabled &&
                (applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager ).isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


}
