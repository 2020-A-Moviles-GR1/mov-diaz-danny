package com.diaz_danny.DeusGallet

import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraIdleListener,
    GoogleMap.OnPolylineClickListener,
    GoogleMap.OnPolygonClickListener{

    private lateinit var mMap: GoogleMap
    private var tienePermisos = false

    companion object {
        val TAG = "MAPSActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        solicitarPermisos()

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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        establecerConfiguracionMapa(mMap)
        establecerListeners(mMap)

        val lat_lng = LatLng(-0.178867, -78.485594)
        val titulo = "CCI"
        val zoom = 17f

        anadirMarcador(lat_lng, titulo)
        moverCamaraConZoom(lat_lng, zoom)


        val polilineUno = googleMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .add(
                    LatLng(-0.177193, -78.488126),
                    LatLng(-0.176893, -78.484564),
                    LatLng(-0.179639, -78.484650),
                    LatLng(-0.179897, -78.487568)
                )
        )

        val poligonoUno = googleMap.addPolygon(
            PolygonOptions()
                .clickable(true)
                .add(
                    LatLng(-0.180069, -78.484907),
                    LatLng(-0.179768, -78.484264),
                    LatLng(-0.180584, -78.483320),
                    LatLng(-0.180627, -78.485165)

                )
        )
        poligonoUno.fillColor = Color.YELLOW


    }

    private fun establecerListeners(mMap: GoogleMap) {

        with(mMap) {
            setOnCameraIdleListener(this@MapsActivity)
            setOnCameraMoveStartedListener(this@MapsActivity)
            setOnCameraMoveListener(this@MapsActivity)
            setOnPolylineClickListener(this@MapsActivity)
            setOnPolygonClickListener(this@MapsActivity)
        }
    }

    private fun anadirMarcador(latLng: LatLng, title:String) {
        mMap.addMarker(
            MarkerOptions().position(latLng).title(title)
        )
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

    override fun onCameraMoveStarted(p0: Int) {
        Log.i(TAG, "Empezando a mover onCameraMoveStarted")
    }

    override fun onCameraMove() {
        Log.i(TAG, "Moviendo on camera onCameraMove")
    }

    override fun onCameraIdle() {
        Log.i(TAG, "Camara quieta, onCameraIdle")
    }

    override fun onPolylineClick(p0: Polyline?) {
        Log.i(TAG, "Tocando polilinea ${p0.toString()}")
    }

    override fun onPolygonClick(p0: Polygon?) {
        Log.i(TAG, "Tocando polígono ${p0.toString()}")
    }
}