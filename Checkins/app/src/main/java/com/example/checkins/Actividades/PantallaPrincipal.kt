package com.example.checkins.Actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.Foursquare.Foursquare
import com.example.checkins.RecyclerViewPrincipal.AdaptadorCustom
import com.example.checkins.RecyclerViewPrincipal.ClickListener
import com.example.checkins.RecyclerViewPrincipal.LongClinckListener
import com.example.checkins.Interfaces.ObtenerVenuesInterface
import com.example.checkins.Interfaces.UbicacionListener
import com.example.checkins.R
import com.example.checkins.Utilidades.Ubicacion
import com.example.checkins.Foursquare.Venue
import com.example.checkins.Interfaces.VenuesPorLikeInterface
import com.google.android.gms.location.LocationResult
import com.google.gson.Gson

class PantallaPrincipal : AppCompatActivity() {

    var ubicacion: Ubicacion? = null
    var foursquare: Foursquare? = null

    var lista: RecyclerView? = null
    var adaptador:AdaptadorCustom? = null
    var layoutManager: RecyclerView.LayoutManager? = null

    var toolbar:Toolbar? = null

    companion object{
        val VENUE_ACTUAL = "checkins.PantallaPrincipal"
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)

        foursquare = Foursquare(this,this)

        lista = findViewById(R.id.rvLugares)
        lista?.setHasFixedSize(true)

        initToolbar()

        layoutManager = LinearLayoutManager(this)
        lista?.layoutManager = layoutManager

        if(foursquare?.hayToken()!!){
            ubicacion = Ubicacion(this,object : UbicacionListener {
                override fun ubicacionResponse(locationResult: LocationResult) {
                    val lat = locationResult.lastLocation.latitude.toString()
                    val lon = locationResult.lastLocation.longitude.toString()
                    //Toast.makeText(applicationContext,locationResult.lastLocation.latitude.toString(),Toast.LENGTH_SHORT).show()
                    foursquare?.obtenerVenues(lat,lon,object : ObtenerVenuesInterface {
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
                intent.putExtra(VENUE_ACTUAL,venueActualString)
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
        toolbar?.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {//nos permite mapear elementos mientras se crea el menu
        menuInflater.inflate(R.menu.menu_principal,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.iconoCategorias->{
                val intent = Intent(this, Categorias::class.java)
                startActivity(intent)
                return true
            }
            R.id.iconoFavoritos->{
                val intent = Intent(this, Likes::class.java)
                startActivity(intent)
                return true
            }
            R.id.iconoPerfil->{
                val intent = Intent(this,Perfil::class.java)
                startActivity(intent)
                return true
            }
            R.id.iconoCerrarSesion->{
                foursquare?.cerrarSesion()
                val intent = Intent(this,Login::class.java)
                startActivity(intent)
                finish()
                return true
            }

            else->{return super.onOptionsItemSelected(item)}
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ubicacion?.onRequestPermissionResult(requestCode,permissions,grantResults)
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