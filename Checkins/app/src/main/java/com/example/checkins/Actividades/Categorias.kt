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
import com.example.checkins.Interfaces.CategoriasVenuesInterface
import com.example.checkins.R
import com.example.checkins.RecyclerViewCategorias.AdaptadorCustom
import com.example.checkins.RecyclerViewCategorias.ClickListener
import com.example.checkins.RecyclerViewCategorias.LongClinckListener
import com.google.gson.Gson

class Categorias : AppCompatActivity() {

    var lista: RecyclerView? = null
    var adaptador: AdaptadorCustom? = null
    var layoutManager: RecyclerView.LayoutManager? = null

    var toolbar: Toolbar? = null

    companion object {
        val CATEGORIA_ACTUAL = "checkins.Categorias"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)

        initToolbar()
        initReciclerView()

        val fsqr = Foursquare(this, Categorias())

        if(fsqr.hayToken()){
            fsqr.cargarCategorias(object:CategoriasVenuesInterface{
                override fun categoriasVenues(categorias: ArrayList<Category>) {

                    implementacionRecyclerView(categorias)
                }

            })
        }else{
            fsqr?.mandarIniciarSesion()
        }

    }

    private fun initReciclerView(){
        lista = findViewById(R.id.rvCategorias)
        lista?.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(this)
        lista?.layoutManager = layoutManager
    }

    private fun implementacionRecyclerView(categorias:ArrayList<Category>){
        adaptador = AdaptadorCustom(categorias, object: ClickListener {
            override fun onClick(vista: View, index: Int) {
                val categoriaToJson = Gson()
                val categoriaActualString = categoriaToJson.toJson(categorias.get(index))
                val intent = Intent(applicationContext, VenuesPorCategoria::class.java)

                intent.putExtra(Categorias.CATEGORIA_ACTUAL,categoriaActualString)
                startActivity(intent)
            }
        },object: LongClinckListener {
            override fun longClick(vista: View, index: Int) {}

        })
        lista?.adapter = adaptador
    }

    fun initToolbar(){
        toolbar=findViewById(R.id.toolbar)
        toolbar?.setTitle(R.string.app_categories)
        setSupportActionBar(toolbar)

        var actioBar = supportActionBar
        actioBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.setNavigationOnClickListener{finish()}
    }
}