package com.example.checkins.GridViewDetalleVenue

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.checkins.Foursquare.Rejilla
import com.example.checkins.Foursquare.Venue
import com.example.checkins.R

class AdaptadorGridView(var context: Context, items:ArrayList<Rejilla>): BaseAdapter() {

    var items:ArrayList<Rejilla>? = null

    init {
        this.items = items
    }

    override fun getCount(): Int {
        return items?.count()!!
    }

    override fun getItem(p0: Int): Any {
        return items?.get(p0)!!
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var vista = p1
        var holder:ViewHolder? = null

        if(vista==null){
            vista= LayoutInflater.from(context).inflate(R.layout.template_grid_detalle_venue,null)
            holder = ViewHolder(vista)
            vista?.tag=holder
        }else{
            holder = vista.tag as? ViewHolder
        }

        val item = items?.get(p0) as? Rejilla
        holder?.nombre?.text = item?.nombre
        holder?.imagen?.setImageResource(item?.icono!!)

        holder?.container?.setBackgroundColor(item?.colorFondo!!)

        return vista!!
    }



     class ViewHolder(vista: View){
         var nombre: TextView? = null
         var imagen: ImageView? = null
         var container:LinearLayout? = null

         init{
            nombre=vista.findViewById(R.id.nombre)
            imagen=vista.findViewById(R.id.imagen)
             container=vista.findViewById(R.id.container)
         }

    }
}