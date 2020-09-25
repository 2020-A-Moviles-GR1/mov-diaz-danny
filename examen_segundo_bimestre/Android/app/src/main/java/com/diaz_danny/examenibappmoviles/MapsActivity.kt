package com.diaz_danny.examenibappmoviles

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.diaz_danny.examenibappmoviles.models.GlideApp
import com.diaz_danny.examenibappmoviles.models.Parameters
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import models.ChatGroup
import java.io.InputStream
import java.net.URL


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var tienePermisos = false
    private lateinit var handler: Handler

    companion object {
        val TAG = "MAPSActivity"
    }
    var grupoChatPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        handler = Handler(Looper.getMainLooper())
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        solicitarPermisos()


        grupoChatPosition = intent.getIntExtra("grupoChatPosition", -1)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    var markers: HashMap<String, String> = HashMap<String, String>()

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        establecerConfiguracionMapa(mMap)
        establecerListeners(mMap)

        val lat_lng_inicial = LatLng(-0.107120, -78.449212)
        val zoom = 17f


        moverCamaraConZoom(lat_lng_inicial, zoom)

        mMap.setOnMapLoadedCallback (GoogleMap.OnMapLoadedCallback {
            ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.messages?.forEach {
                anadirMarcadorCustom(this, LatLng(it.latitud, it.longitud), it.url_image, it.sender, it.url_web)
            }
        })



        mMap.setOnInfoWindowClickListener(object : GoogleMap.OnInfoWindowClickListener {
            override fun onInfoWindowClick(marker: Marker) {
                val url_web: String? = markers[marker.id]
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(url_web))
                startActivity(browserIntent)

            }
        })


    }

    fun LoadImageFromWebOperations(url: String): Drawable? {
        return try {
            val input: InputStream = URL(url).getContent() as InputStream
            Drawable.createFromStream(input, "src name")
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            null
        }
    }


    private fun establecerListeners(mMap: GoogleMap) {

        with(mMap) {

        }
    }

    private fun anadirMarcador(latLng: LatLng, title:String) {
        mMap.addMarker(
            MarkerOptions().position(latLng).title(title)
        )
    }

    @SuppressLint("CheckResult")
    private fun anadirMarcadorCustom(context: Context, latLng: LatLng, url_image: String, _name: String, url_web: String){

        Thread(Runnable {

            val marker_view: View =
                (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                    R.layout.layout_marker,
                    null
                )

            Log.d(TAG, url_image.replace("localhost", Parameters.ip, true))



            val url = url_image.replace("localhost", Parameters.ip, true)

            val resource = LoadImageFromWebOperations(url)

            val txt_name = marker_view.findViewById(R.id.name) as TextView
            txt_name.text = _name

            val image = marker_view.findViewById(R.id.image) as ImageView

            val bitm = resource?.let { convertToBitmap(it, 50, 50) }

            image.setImageBitmap(bitm)

            Log.d(TAG, "Resuource: " + resource.toString())
            Log.d(TAG, "Bitmap: " + bitm.toString())

            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            marker_view.setLayoutParams(ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT))
            marker_view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
            marker_view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
            marker_view.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(
                marker_view.getMeasuredWidth(),
                marker_view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            marker_view.draw(canvas)


            handler.post{
                val new_marker: Marker = mMap.addMarker(MarkerOptions().position(latLng).icon(
                    BitmapDescriptorFactory.fromBitmap(
                        bitmap
                    )
                ))

                new_marker.setTitle(url_web)
                markers.put(new_marker.getId(), url_web);
            }


        }).start()



    }


    fun convertToBitmap(drawable: Drawable, widthPixels: Int, heightPixels: Int): Bitmap? {
        val mutableBitmap =
            Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mutableBitmap)
        drawable.setBounds(0, 0, widthPixels, heightPixels)
        drawable.draw(canvas)
        return mutableBitmap
    }

    private fun moverCamaraConZoom(latLng: LatLng, zoom: Float) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    private fun establecerConfiguracionMapa(mMap: GoogleMap) {

        val contexto = this.applicationContext
        with(mMap){
            val permisosFineLocation = ContextCompat
                .checkSelfPermission(
                    contexto,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )

            val tienePermisos = permisosFineLocation == PackageManager.PERMISSION_GRANTED

            if(tienePermisos){
                mMap.isMyLocationEnabled = true
            }

            uiSettings.isZoomControlsEnabled = true
            uiSettings.isMyLocationButtonEnabled = true

        }
    }

    fun solicitarPermisos(){
        val permisosFineLocation = ContextCompat
            .checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )

        val tienePermisos = permisosFineLocation == PackageManager.PERMISSION_GRANTED

        if(tienePermisos){
            Log.i(TAG, "Tiene permisos FINE LOCATION")
            this.tienePermisos = true
        }
        else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ),
                1
            )
        }
    }
}