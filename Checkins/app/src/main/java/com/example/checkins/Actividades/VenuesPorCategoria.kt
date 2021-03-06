package com.example.checkins.Actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.Foursquare.Category
import com.example.checkins.Foursquare.Foursquare
import com.example.checkins.Foursquare.Venue
import com.example.checkins.Interfaces.ObtenerVenuesInterface
import com.example.checkins.Interfaces.UbicacionListener
import com.example.checkins.R
import com.example.checkins.RecyclerViewPrincipal.AdaptadorCustom
import com.example.checkins.RecyclerViewPrincipal.ClickListener
import com.example.checkins.RecyclerViewPrincipal.LongClinckListener
import com.example.checkins.Utilidades.Ubicacion
import com.google.android.gms.location.LocationResult
import com.google.gson.Gson

class VenuesPorCategoria : AppCompatActivity() {

    var ubicacion: Ubicacion? = null
    var foursquare: Foursquare? = null

    var lista: RecyclerView? = null
    var adaptador: AdaptadorCustom? = null
    var layoutManager: RecyclerView.LayoutManager? = null

    var toolbar: Toolbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venues_por_categoria)

        foursquare = Foursquare(this,this)

        lista = findViewById(R.id.rvLugares)
        lista?.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(this)
        lista?.layoutManager = layoutManager

        val categoriaActualString = intent.getStringExtra(Categorias.CATEGORIA_ACTUAL)
        val gson = Gson()
        val categoriaActual = gson.fromJson(categoriaActualString, Category::class.java)

        initToolbar(categoriaActual.name)




        if(foursquare?.hayToken()!!){
            ubicacion = Ubicacion(this,object : UbicacionListener {
                override fun ubicacionResponse(locationResult: LocationResult) {
                    val lat = locationResult.lastLocation.latitude.toString()
                    val lon = locationResult.lastLocation.longitude.toString()
                    val categoryId = categoriaActual.id
                    foursquare?.obtenerVenues(lat,lon,categoryId,object : ObtenerVenuesInterface {
                        override fun venuesGenerados(venues: ArrayList<Venue>) {

                            implementacionRecyclerView(venues)

                        }

                    })
                }

            })
        }else{
            foursquare?.mandarIniciarSesion()
        }
    }

    private fun implementacionRecyclerView(lugares:ArrayList<Venue>){
        adaptador = AdaptadorCustom(lugares, object: ClickListener {
            override fun onClick(vista: View, index: Int) {
                //Toast.makeText(applicationContext,lugares.get(index).name,Toast.LENGTH_SHORT).show()
                val venueToJson = Gson()
                val venueActualString = venueToJson.toJson(lugares.get(index))
                val intent = Intent(applicationContext, DetallesVenue::class.java)
                intent.putExtra(PantallaPrincipal.VENUE_ACTUAL,venueActualString)
                startActivity(intent)
            }
        },object: LongClinckListener {
            override fun longClick(vista: View, index: Int) {
                /* if(!isActionMOde){
                     startSupportActionMode(callback)
                     isActionMOde=true
                     adaptador?.seleccionarItem(index)
                 }else{
                     //hacer selecciones o deselecciones
                     adaptador?.seleccionarItem(index)
                 }
                 actionMode?.title = adaptador?.obtenerNumeroElementosSeleccionados().toString() + " seleccionados"*/
            }

        })
        lista?.adapter = adaptador
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ubicacion?.onRequestPermissionResult(requestCode,permissions,grantResults)
    }

    fun initToolbar(categoria:String){
        toolbar=findViewById(R.id.toolbar)
        toolbar?.setTitle(categoria)
        setSupportActionBar(toolbar)

        var actioBar = supportActionBar
        actioBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.setNavigationOnClickListener{finish()}
    }
    override fun onStart() {
        super.onStart()
        ubicacion?.inicializarUbicacion()
    }

    override fun onPause() {
        super.onPause()
        ubicacion?.detenerActualizacionUbicacion()
    }

}