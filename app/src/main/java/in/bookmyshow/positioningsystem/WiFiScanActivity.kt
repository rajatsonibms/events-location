package `in`.bookmyshow.positioningsystem

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class WiFiScanActivity: AppCompatActivity() {

    var wifiBroadcastReceiver: BroadcastReceiver?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi)

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (wifiManager.isWifiEnabled) {
            wifiManager.startScan()
        } else {
            Toast.makeText(this, "Switch on wifi and restart", Toast.LENGTH_SHORT).show()
            finish()
        }

        wifiBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                when (intent?.action) {
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> {
                        val scanResults = wifiManager.scanResults as List<ScanResult>

                        var listStr = "" + scanResults.size
                        for (scanResult in scanResults) {
                            listStr += "\n\nBSSID: " + scanResult.SSID + " " + scanResult.venueName
                        }

                        textView.setText(listStr)
                    }
                }
            }
        };

    }

    override fun onResume() {
        super.onResume()
        registerReceiver(wifiBroadcastReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(wifiBroadcastReceiver);
    }
}