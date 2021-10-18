package com.example.checkins.Actividades

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.checkins.Foursquare.Foursquare
import com.example.checkins.Foursquare.Rejilla
import com.example.checkins.Foursquare.User
import com.example.checkins.GridViewDetalleVenue.AdaptadorGridView
import com.example.checkins.Interfaces.UsuariosInterface
import com.example.checkins.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.NumberFormat
import java.util.*

class Perfil : AppCompatActivity() {

    var foursquare: Foursquare? = null

    var toolbar: Toolbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val tvNombrePerfil = findViewById<TextView>(R.id.tvNombre_Perfil)
        val tvFriendsPerfil = findViewById<TextView>(R.id.tvFriends_Perfil)
        val tvTipsPerfil = findViewById<TextView>(R.id.tvTips_Perfil)
        val tvPhotosPerfil = findViewById<TextView>(R.id.tvPhotos_Perfil)
        val tvCheckinsPerfil = findViewById<TextView>(R.id.tvCheckins_Perfil)
        val ivFotoPerfil = findViewById<CircleImageView>(R.id.ivFoto_Perfil)
        val gvRejilla = findViewById<GridView>(R.id.gvRejilla_Perfil)

        val listaRejilla = ArrayList<Rejilla>()

        foursquare = Foursquare(this,this)

        initToolbar("Hola")


        if(foursquare?.hayToken()!!){
            foursquare?.obtenerUsuarioActual(object :UsuariosInterface{
                override fun obtenerUsuarioActual(usuario: User) {
                    initToolbar("${usuario.firtsName} ${usuario.lastName}")
                    tvNombrePerfil.text = usuario.firtsName
                    //tvFriendsPerfil.text = "${usuario.friends?.count.toString()} ${R.string.app_perfil_amigos}"
                    tvFriendsPerfil.text = String.format("%d %s",usuario.friends?.count,R.string.app_perfil_amigos)
                    tvTipsPerfil.text = String.format("%d %s",usuario.tips?.count,R.string.app_perfil_tips)
                    tvPhotosPerfil.text = String.format("%d %s",usuario.photos?.count,R.string.app_perfil_photos)
                    tvCheckinsPerfil.text = String.format("%d %s",usuario.checkins?.count,R.string.app_perfil_checkins)
                    Picasso.get().load(usuario.photo?.urlIcono).into(ivFotoPerfil)


                    listaRejilla.add(
                        Rejilla(String.format("%s fotos",
                            NumberFormat.getNumberInstance(Locale.US).format(usuario.photos?.count)),R.drawable.icono_photo,R.color.secondaryColor)
                    )
                    listaRejilla.add(
                        Rejilla(String.format("%s checkins",
                            NumberFormat.getNumberInstance(Locale.US).format(usuario.checkins?.count)),R.drawable.icono_lugar,R.color.secondaryColor)
                    )
                    listaRejilla.add(
                        Rejilla(String.format("%s amigos",
                            NumberFormat.getNumberInstance(Locale.US).format(usuario.friends?.count)),R.drawable.icono_users,R.color.secondaryColor)
                    )
                    listaRejilla.add(
                        Rejilla(String.format("%s tips",
                            NumberFormat.getNumberInstance(Locale.US).format(usuario.tips?.count)),R.drawable.icono_tips,R.color.secondaryColor)
                    )

                    val adaptador = AdaptadorGridView(applicationContext,listaRejilla)
                    gvRejilla.adapter = adaptador


                }

            })
        }else{
            foursquare?.mandarIniciarSesion()
        }
    }

    fun initToolbar(nombrePerfil:String){
        toolbar=findViewById(R.id.toolbar)
        toolbar?.setTitle(nombrePerfil)
        setSupportActionBar(toolbar)

        val actioBar = supportActionBar
        actioBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.setNavigationOnClickListener{finish()}
    }
}