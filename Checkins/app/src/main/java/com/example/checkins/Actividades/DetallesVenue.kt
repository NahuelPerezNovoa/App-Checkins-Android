package com.example.checkins.Actividades

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.checkins.Foursquare.Foursquare
import com.example.checkins.Foursquare.Rejilla
import com.example.checkins.R
import com.example.checkins.Foursquare.Venue
import com.example.checkins.GridViewDetalleVenue.AdaptadorGridView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class DetallesVenue : AppCompatActivity() {

    var toolbar: Toolbar? = null
    var bCheckin:Button? = null
    var bLike:Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_venue)

        bCheckin=findViewById(R.id.bCheckin)
        bLike=findViewById(R.id.bLike)


        val ivFoto = findViewById<ImageView>(R.id.ivFoto_Detalles)
        val tvNombre = findViewById<TextView>(R.id.tvNombre_Detalle)
        val tvState = findViewById<TextView>(R.id.tvState_Detalle)
        val tvCountry = findViewById<TextView>(R.id.tvCountry_Detalle)
        /*val tvCategory = findViewById<TextView>(R.id.tvCategory_Detalle)
        val tvCheckins = findViewById<TextView>(R.id.tvCheckins_Detalle)
        val tvUsers = findViewById<TextView>(R.id.tvUsers_Detalle)
        val tvTips = findViewById<TextView>(R.id.tvTips_Detalle)*/
        val gvRejilla = findViewById<GridView>(R.id.gvRejilla_Perfil)

        val venueActualString = intent.getStringExtra(PantallaPrincipal.VENUE_ACTUAL)
        val gson = Gson()
        val venueActual = gson.fromJson(venueActualString, Venue::class.java)

        val listaRejilla = ArrayList<Rejilla>()

        initToolbar(venueActual.name)

        Picasso.get().load(venueActual.imagePreview).placeholder(R.drawable.placeholder_venue).into(ivFoto)
        tvNombre.text = venueActual.name
        tvState.text = venueActual.location?.state
        tvCountry.text = venueActual.location?.country
        /*tvCategory.text = venueActual.categories?.get(0)?.name
        tvCheckins.text = venueActual.stats?.checkinsCount.toString()
        tvUsers.text = venueActual.stats?.usersCount.toString()
        tvTips.text = venueActual.stats?.tipCount.toString()*/

        listaRejilla.add(Rejilla(venueActual.categories?.get(0)?.name!!,R.drawable.icono_categories,R.color.secondaryColor))
        listaRejilla.add(Rejilla(String.format("%s checkins",NumberFormat.getNumberInstance(Locale.US).format(venueActual.stats?.checkinsCount)),R.drawable.icono_lugar,R.color.secondaryColor))
        listaRejilla.add(Rejilla(String.format("%s usuarios",NumberFormat.getNumberInstance(Locale.US).format(venueActual.stats?.usersCount)),R.drawable.icono_users,R.color.secondaryColor))
        listaRejilla.add(Rejilla(String.format("%s tips",NumberFormat.getNumberInstance(Locale.US).format(venueActual.stats?.tipCount)),R.drawable.icono_tips,R.color.secondaryColor))

        val adaptador = AdaptadorGridView(this,listaRejilla)
        gvRejilla.adapter = adaptador

        val foursquare = Foursquare(this,this)



        bCheckin?.setOnClickListener {
            if(foursquare.hayToken()){
                val etMensaje = EditText(this)
                etMensaje.hint="Hola!"
                AlertDialog.Builder(this)
                    .setTitle("Nuevo Check.in")
                    .setMessage("Ingresa un mensaje")
                    .setView(etMensaje)
                    .setNegativeButton("Cancelar",DialogInterface.OnClickListener { dialogInterface, i ->  })
                    .setPositiveButton("Check-in", DialogInterface.OnClickListener {
                            dialogInterface, i ->
                        val mensaje = URLEncoder.encode(etMensaje.text.toString(),"UTF-8")
                        foursquare.nuevoCheckin(venueActual.id,venueActual.location!!,mensaje)
                    })
                    .show()
                //foursquare.nuevoCheckin(venueActual.id,venueActual.location!!,"Hola%20Mundo")

            }else{
                foursquare?.mandarIniciarSesion()
            }
        }

        bLike?.setOnClickListener {
            if(foursquare.hayToken()){
                foursquare.nuevoLike(venueActual.id)}
        }
    }

    fun initToolbar(categoria:String){
        toolbar=findViewById(R.id.toolbar)
        toolbar?.setTitle(categoria)
        setSupportActionBar(toolbar)

        var actioBar = supportActionBar
        actioBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.setNavigationOnClickListener{finish()}
    }
}