package `in`.bookmyshow.positioningsystem

import android.opengl.Matrix
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_location.*
import java.lang.Exception

class LocationActivity : AppCompatActivity(), Callback {

    override fun onError(e: Exception?) {

    }

    override fun onSuccess() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_location)
//        Picasso.Builder(this)
//                .listener(Picasso.Listener { picasso, uri, exception -> Log.d("Picasso Error", exception.message) })
//                .build()
//                .load("https://maps.googleapis.com/maps/api/staticmap?center=Dev+Plaza,Mumbai&size=500x300&zoom=20")
//                .error(resources.getDrawable(R.drawable.ic_launcher_background))
//                .into(ivMapImage, this)
    }
}
