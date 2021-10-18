package com.example.checkins.RecyclerViewLikes

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.Foursquare.Category
import com.example.checkins.R
import com.example.checkins.Foursquare.Venue

class AdaptadorCustom(items:ArrayList<Venue>, var listener: ClickListener, var longClinckListener: LongClinckListener):RecyclerView.Adapter<AdaptadorCustom.ViewHolder>() {

    var items:ArrayList<Venue>? = null
    var multiseleccion = false

    var itemsSeleccionados:ArrayList<Int>? = null
    var viewHolder:ViewHolder? = null

    init {
        this.items = items
        itemsSeleccionados = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptadorCustom.ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.template_venues_likes,parent,false)
        viewHolder = ViewHolder(vista, listener, longClinckListener)

        return viewHolder!!
    }

    override fun onBindViewHolder(holder: AdaptadorCustom.ViewHolder, position: Int) {//permite mapear los elementos graficos con los datos
        val item = items?.get(position)
        //holder.foto?.setImageResource(item?.foto!!)
        holder.nombre?.text =item?.name
        //holder.precio?.text ="$" + item?.precio.toString()
        //holder.rating?.rating = item?.rating!!

        if(itemsSeleccionados?.contains(position)!!){
            holder.vista.setBackgroundColor(Color.DKGRAY)
        }else{
            holder.vista.setBackgroundColor(Color.BLACK)
        }
    }

    fun iniciarActionMode(){
        multiseleccion = true
    }

    fun destruirActionMode(){
        multiseleccion = false
        itemsSeleccionados?.clear()
        notifyDataSetChanged()
    }

    fun terminarActionMode(){
        //eliminar elementos seleccionados
        multiseleccion = false
        for (item in itemsSeleccionados!!){
            itemsSeleccionados?.remove(item)
        }
        notifyDataSetChanged()
    }

    fun seleccionarItem(index:Int){
        if(multiseleccion){
            if(itemsSeleccionados?.contains(index)!!){
                itemsSeleccionados?.remove(index)
            }else{
                itemsSeleccionados?.add(index)
            }
            notifyDataSetChanged()
        }
    }

    fun obtenerNumeroElementosSeleccionados():Int{
        return itemsSeleccionados?.count()!!
    }

    fun eliminarSeleccionados(){
        if(itemsSeleccionados?.count()!!>0){
            var itemsEliminados = ArrayList<Venue>()

            for(index in itemsSeleccionados!!){
                itemsEliminados.add(items?.get(index)!!)
            }

            items?.removeAll(itemsEliminados)
            itemsSeleccionados?.clear()
        }
    }

    override fun getItemCount(): Int {
        return items?.count()!!
    }

    class ViewHolder(vista:View, listener: ClickListener, longClinckListener: LongClinckListener):RecyclerView.ViewHolder(vista),View.OnClickListener, View.OnLongClickListener{
        var vista = vista
        //var foto:ImageView? = null
        var nombre:TextView? = null
        //var precio:TextView? = null
        //var rating:RatingBar? = null

        var listener:ClickListener? = null
        var longListener:LongClinckListener? = null

        init {
            //foto=vista.findViewById(R.id.ivFoto)
            nombre=vista.findViewById(R.id.tvNombre_Categorias)
            //precio=vista.findViewById(R.id.tvPrecio)
            //rating=vista.findViewById(R.id.tvRating)

            this.listener = listener
            this.longListener = longClinckListener

            vista.setOnClickListener(this)
            vista.setOnLongClickListener(this)
        }

        override fun onClick(p0: View?) {
            this.listener?.onClick(p0!!,adapterPosition)
        }

        override fun onLongClick(p0: View?): Boolean {
            this.longListener?.longClick(p0!!,adapterPosition)
            return true
        }
    }
}