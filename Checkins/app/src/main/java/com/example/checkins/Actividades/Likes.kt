package com.example.checkins.Actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.Foursquare.Foursquare
import com.example.checkins.Foursquare.Venue
import com.example.checkins.Interfaces.ObtenerVenuesInterface
import com.example.checkins.Interfaces.UbicacionListener
import com.example.checkins.Interfaces.VenuesPorLikeInterface
import com.example.checkins.R
import com.example.checkins.RecyclerViewPrincipal.AdaptadorCustom
import com.example.checkins.RecyclerViewPrincipal.ClickListener
import com.example.checkins.RecyclerViewPrincipal.LongClinckListener
import com.example.checkins.Utilidades.Ubicacion
import com.google.android.gms.location.LocationResult
import com.google.gson.Gson

class Likes : AppCompatActivity() {

    var foursquare: Foursquare? = null

    var lista: RecyclerView? = null
    var adaptador: AdaptadorCustom? = null
    var layoutManager: RecyclerView.LayoutManager? = null

    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_likes)

        foursquare = Foursquare(this,this)

        lista = findViewById(R.id.rvLugares)
        lista?.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(this)
        lista?.layoutManager = layoutManager

        initToolbar()


        if(foursquare?.hayToken()!!){
            foursquare?.obtenerVenuesdeLike(object :VenuesPorLikeInterface{
                override fun venuesGenerados(venues: ArrayList<Venue>) {
                    implementacionRecyclerView(venues)
                }

            })
        }else{
            foursquare?.mandarIniciarSesion()
        }


    }
    private fun implementacionRecyclerView(lugares:ArrayList<Venue>){
        adaptador = AdaptadorCustom(lugares, object: ClickListener {
            override fun onClick(vista: View, index: Int) {
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

    fun initToolbar(){
        toolbar=findViewById(R.id.toolbar)
        toolbar?.setTitle(R.string.app_likes)
        setSupportActionBar(toolbar)

        var actioBar = supportActionBar
        actioBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.setNavigationOnClickListener{finish()}
    }

}