package com.example.checkins.Foursquare

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.checkins.Actividades.Login
import com.example.checkins.Interfaces.*
import com.example.checkins.Mensajes.Errores
import com.example.checkins.Mensajes.Mensaje
import com.example.checkins.Mensajes.Mensajes
import com.example.checkins.Utilidades.Network
import com.foursquare.android.nativeoauth.FoursquareOAuth
import com.google.gson.Gson

class Foursquare(var activity: AppCompatActivity,var activityDestino:AppCompatActivity) {
    private val CODIGO_CONEXION = 200
    private val CODIGO_INTERCAMBIO_TOKEN = 201

    private val CLIENT_ID = "4BVS1AJUP4HIOHC4XTVBNVNFWNMPOQFCNJ1BP04N1WERMIB3"
    private val CLIENT_SECRET = "OFR3UIDTYOM15UPKBRRPJ5RTCT2QLTUY30IDOHANJQTFJHM0"

    private val SETTINGS = "settings"
    private val ACCESS_TOKEN = "accessToken"

    private val URL_BASE = "https://api.foursquare.com/v2/"
    private val VERSION = "v=20180117"

    init {

    }

    fun iniciarSesion(){
        val intent = FoursquareOAuth.getConnectIntent(activity.applicationContext,CLIENT_ID)

        if(FoursquareOAuth.isPlayStoreIntent(intent)){
            Mensaje.mensajeError(activity.applicationContext, Errores.HTTP_ERROR)
            activity.startActivity(intent)
        }else{
            activity.startActivityForResult(intent,CODIGO_CONEXION)
        }
    }

    fun validarActivityResult(requesCode:Int, resultCode:Int, data:Intent?){
        when(requesCode){
            CODIGO_CONEXION->{conexionCompleta(resultCode,data)}

            CODIGO_INTERCAMBIO_TOKEN->{intercambioTokenCompleta(resultCode,data)}
        }
    }

    private fun conexionCompleta(resultCode: Int,data: Intent?){
        val codigoRespuesta = FoursquareOAuth.getAuthCodeFromResult(resultCode,data)
        val excepcion = codigoRespuesta.exception

        if(excepcion == null){
            val codigo = codigoRespuesta.code
            realizarIntercambioToken(codigo)
        }else{
            Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_CONEXION_FSQR)
        }
    }

    private fun realizarIntercambioToken(codigo: String){
        val intent = FoursquareOAuth.getTokenExchangeIntent(activity.applicationContext,CLIENT_ID,CLIENT_SECRET,codigo)
        activity.startActivityForResult(intent,CODIGO_INTERCAMBIO_TOKEN)
    }

    private fun intercambioTokenCompleta(resultCode: Int,data: Intent?){
        val respuestaToken = FoursquareOAuth.getTokenFromResult(resultCode, data)
        val excepcion = respuestaToken.exception

        if(excepcion == null){
            val accessToken = respuestaToken.accessToken
            if(!guardarToken(accessToken)){
                Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_AL_GUARDAR_TOKEN)
            }else{
                navegarSiguienteActividad()
            }
        }else{
            Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_INTERCAMBIO_TOKEN)
        }
    }

    fun hayToken():Boolean{
        if(obtenerToken()==""){
            return false
        }else{
            return true
        }
    }

    fun obtenerToken():String?{
        val settings = activity.getSharedPreferences(SETTINGS,0)
        val token = settings.getString("accessToken","")
        return token
    }

    private fun guardarToken(token:String):Boolean{
        if(token.isEmpty()){
            return false
        }
        val settings = activity.getSharedPreferences(SETTINGS,0)
        val editor = settings.edit()

        editor.putString(ACCESS_TOKEN,token)

        editor.apply()
        return true
    }

    fun cerrarSesion(){
        val settings = activity.getSharedPreferences(SETTINGS,0)
        val editor = settings.edit()

        editor.putString(ACCESS_TOKEN,"")

        editor.apply()
    }

    fun mandarIniciarSesion(){
        activity.startActivity(Intent(this.activity,Login::class.java))
        activity.finish()
    }

    fun navegarSiguienteActividad(){
        activity.startActivity(Intent(this.activity,activityDestino::class.java))
        activity.finish()
    }

    fun obtenerVenues(lat:String, lon:String, obtenerVenuesInterface: ObtenerVenuesInterface){
        val network = Network(activity)
        val seccion = "venues/"
        val metodo = "search/"
        val ll = "ll=$lat, $lon"
        val token = "oauth_token=" + obtenerToken()

        val url = "$URL_BASE$seccion$metodo?$ll&$token&$VERSION"

        network.httpRequest(activity.applicationContext,url, object : HttpResponse {
            override fun httpResponseSuccess(response: String) {
                var gson = Gson()
                var objetoRespuesta = gson.fromJson(response, FoursquareAPIRequestVenues::class.java)

                var meta = objetoRespuesta.meta
                var venues = objetoRespuesta.response?.venues!!

                if(meta?.code == 200){
                    //mensaje de que se completo el query correctamente
                    for(venue in venues){
                        obtenerImagePreview(venue.id,object:ImagePreviewInterface{
                            override fun obtenerImagePreview(photos: ArrayList<Photo>) {
                                if(photos.count()>0){
                                    val urlImagen = photos.get(0).construirURLImagen(obtenerToken()!!,VERSION,"original")
                                    venue.imagePreview = urlImagen

                                    if(venue.categories?.count()!!>0) {
                                        val urlIcono = venue.categories?.get(0)?.icon?.construirURLImagen(obtenerToken()!!, VERSION,"64")
                                        venue.iconCategory = urlIcono
                                    }
                                }
                            }
                        })
                    }
                    obtenerVenuesInterface.venuesGenerados(venues)
                }else{
                    if(meta?.code==400){
                        //mostrar problema al usuarios
                        Mensaje.mensajeError(activity.applicationContext, meta.errorDetail)
                    }else{
                        //mostrar un mensaje generico
                        Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_QUERY)
                    }
                }
            }

        })
    }

    private fun obtenerImagePreview(venueId:String,imagePreviewInterface:ImagePreviewInterface){
        val network = Network(activity)
        val seccion = "venues/"
        val metodo = "photos/"
        val token = "oauth_token=" + obtenerToken()
        val parametros = "limit=1"

        val url = "$URL_BASE$seccion$venueId/$metodo?$token&$VERSION"

        network.httpRequest(activity.applicationContext,url, object : HttpResponse {
            override fun httpResponseSuccess(response: String) {
                var gson = Gson()
                var objetoRespuesta = gson.fromJson(response, ImagePreviewVenueResponse::class.java)

                var meta = objetoRespuesta.meta
                var photos = objetoRespuesta.response?.photos?.items

                if(meta?.code == 200){
                    //mensaje de que se completo el query correctamente
                    imagePreviewInterface.obtenerImagePreview(photos!!)
                }else{
                    if(meta?.code==400){
                        //mostrar problema al usuarios
                        Mensaje.mensajeError(activity.applicationContext, meta.errorDetail)
                    }else{
                        //mostrar un mensaje generico
                        Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_QUERY)
                    }
                }
            }

        })
    }



    fun nuevoCheckin(id:String, location: Location, mensaje:String){
        val network = Network(activity)
        val seccion = "checkins/"
        val metodo = "add"
        val token = "oauth_token=" + obtenerToken()
        val query = "?venueId=$id&shout=$mensaje&ll=${location.lat.toString()},${location.lng.toString()}&$token&$VERSION"


        val url= "$URL_BASE$seccion$metodo$query"

        network.httpPOSTRequest(activity.applicationContext,url,object : HttpResponse {
            override fun httpResponseSuccess(response: String) {
                Log.d("nuevoCheckin",response)
                val gson = Gson()
                val objetoRespuesta = gson.fromJson(response, FoursquareAPInuevoCheckin::class.java)

                var meta = objetoRespuesta.meta

                if(meta?.code == 200){
                    Mensaje.mensajeSuccess(activity.applicationContext, Mensajes.CHECKIN_SUCCESS)
                }else{
                    if(meta?.code==400){
                        //mostrar problema al usuarios
                        Mensaje.mensajeError(activity.applicationContext, meta.errorDetail)
                    }else{
                        //mostrar un mensaje generico
                        Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_QUERY)
                    }
                }
            }

        })
    }

    fun obtenerUsuarioActual(usuarioActualInterface: UsuariosInterface){
        val network = Network(activity)
        val seccion = "users/"
        val metodo = "self"
        val token = "oauth_token=" + obtenerToken()
        val query = "?$token&$VERSION"


        val url= "$URL_BASE$seccion$metodo$query"

        network.httpRequest(activity.applicationContext,url,object : HttpResponse {
            override fun httpResponseSuccess(response: String) {
                val gson = Gson()
                val objetoRespuesta = gson.fromJson(response, FoursquareAPISelfUser::class.java)

                var meta = objetoRespuesta.meta

                if(meta?.code == 200){
                    //mensaje de que se completo el query correctamente
                        val usuario = objetoRespuesta?.response?.user!!
                    usuario.photo?.construirURLImagen(obtenerToken()!!,VERSION,"128x128")
                    usuarioActualInterface.obtenerUsuarioActual(usuario)
                }else{
                    if(meta?.code==400){
                        //mostrar problema al usuarios
                        Mensaje.mensajeError(activity.applicationContext, meta.errorDetail)
                    }else{
                        //mostrar un mensaje generico
                        Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_QUERY)
                    }
                }
            }

        })
    }

    fun cargarCategorias(categoriasInterface: CategoriasVenuesInterface){
        val network = Network(activity)
        val seccion = "venues/"
        val metodo = "categories/"
        val token = "oauth_token=" + obtenerToken()
        val query = "?$token&$VERSION"


        val url= "$URL_BASE$seccion$metodo$query"

        network.httpRequest(activity.applicationContext,url,object : HttpResponse {
            override fun httpResponseSuccess(response: String) {
                val gson = Gson()
                val objetoRespuesta = gson.fromJson(response, FoursquareAPICategorias::class.java)

                var meta = objetoRespuesta.meta

                if(meta?.code == 200){
                    //mensaje de que se completo el query correctamente
                        val categories = objetoRespuesta!!.response?.categories!!
                        for (categoria in categories){
                            categoria.icon?.construirURLImagen(obtenerToken()!!,VERSION,"bg_64")!!
                        }
                    categoriasInterface.categoriasVenues(objetoRespuesta.response?.categories!!)
                }else{
                    if(meta?.code==400){
                        //mostrar problema al usuarios
                        Mensaje.mensajeError(activity.applicationContext, meta.errorDetail)
                    }else{
                        //mostrar un mensaje generico
                        Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_QUERY)
                    }
                }
            }

        })
    }

    fun obtenerVenues(lat:String, lon:String, categoryId:String, obtenerVenuesInterface: ObtenerVenuesInterface){
        val network = Network(activity)
        val seccion = "venues/"
        val metodo = "search/"
        val ll = "ll=$lat, $lon"
        val categoria = "categoryId=$categoryId"
        val token = "oauth_token=" + obtenerToken()

        val url = "$URL_BASE$seccion$metodo?$ll&$categoria&$token&$VERSION"

        network.httpRequest(activity.applicationContext,url, object : HttpResponse {
            override fun httpResponseSuccess(response: String) {
                var gson = Gson()
                var objetoRespuesta = gson.fromJson(response, FoursquareAPIRequestVenues::class.java)

                var meta = objetoRespuesta.meta
                var venues = objetoRespuesta.response?.venues!!

                if(meta?.code == 200){
                    //mensaje de que se completo el query correctamente
                    obtenerVenuesInterface.venuesGenerados(venues)
                }else{
                    if(meta?.code==400){
                        //mostrar problema al usuarios
                        Mensaje.mensajeError(activity.applicationContext, meta?.errorDetail)
                    }else{
                        //mostrar un mensaje generico
                        Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_QUERY)
                    }
                }
            }

        })
    }

    fun nuevoLike(id:String){
        val network = Network(activity)
        val seccion = "venues/"
        val metodo = "like/"
        val token = "oauth_token=" + obtenerToken()
        val query = "?$token&$VERSION"


        val url= "$URL_BASE$seccion$id/$metodo$query"

        network.httpPOSTRequest(activity.applicationContext,url,object : HttpResponse {
            override fun httpResponseSuccess(response: String) {
                val gson = Gson()
                val objetoRespuesta = gson.fromJson(response, LikesResponse::class.java)

                var meta = objetoRespuesta.meta

                if(meta?.code == 200){
                    Mensaje.mensajeSuccess(activity.applicationContext, Mensajes.LIKE_SUCCESS)
                }else{
                    if(meta?.code==400){
                        //mostrar problema al usuarios
                        Mensaje.mensajeError(activity.applicationContext, meta.errorDetail)
                    }else{
                        //mostrar un mensaje generico
                        Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_QUERY)
                    }
                }
            }

        })
    }

    fun obtenerVenuesdeLike(venuesPorLikeInterface: VenuesPorLikeInterface){
        val network = Network(activity)
        val seccion = "users/"
        val metodo = "self/"
        val token = "oauth_token=" + obtenerToken()

        val url = "$URL_BASE$seccion${metodo}venuelikes?limit=10&$token&$VERSION"

        network.httpRequest(activity.applicationContext,url, object : HttpResponse {
            override fun httpResponseSuccess(response: String) {
                var gson = Gson()
                var objetoRespuesta = gson.fromJson(response, VenuesDeLikes::class.java)

                var meta = objetoRespuesta.meta
                var venues = objetoRespuesta.response?.venues?.items!!

                if(meta?.code == 200){
                    //mensaje de que se completo el query correctamente
                    venuesPorLikeInterface.venuesGenerados(venues)
                }else{
                    if(meta?.code==400){
                        //mostrar problema al usuarios
                        Mensaje.mensajeError(activity.applicationContext, meta?.errorDetail)
                    }else{
                        //mostrar un mensaje generico
                        Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_QUERY)
                    }
                }
            }

        })
    }
}