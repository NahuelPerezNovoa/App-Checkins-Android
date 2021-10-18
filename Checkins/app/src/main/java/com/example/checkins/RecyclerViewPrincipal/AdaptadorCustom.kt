package com.example.checkins.RecyclerViewPrincipal

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.checkins.R
import com.example.checkins.Foursquare.Venue
import com.squareup.picasso.Picasso

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
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.template_venues,parent,false)
        viewHolder = ViewHolder(vista, listener, longClinckListener)

        return viewHolder!!
    }

    override fun onBindViewHolder(holder: AdaptadorCustom.ViewHolder, position: Int) {//permite mapear los elementos graficos con los datos
        val item = items?.get(position)
        Picasso.get().load(item?.imagePreview).placeholder(R.drawable.placeholder_venue).into(holder.foto)
        Picasso.get().load(item?.iconCategory).placeholder(R.drawable.icono_categorias).into(holder.iconoCategoria)

        holder.nombre?.text =item?.name
        holder.state?.text = String.format("%s, %s",item?.location?.state , item?.location?.country)
        if(item?.categories?.size!!>0) {
            holder.category?.text = item.categories?.get(0)?.name
        }else{
            holder.category?.setText(R.string.app_pantalla_principal_categories)
        }
        holder.checkins?.text = item.stats?.checkinsCount.toString()

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
        var foto: ImageView? = null
        var iconoCategoria: ImageView? = null
        var nombre:TextView? = null
        var state:TextView? = null
        var category:TextView? = null
        var checkins:TextView? = null
        //var precio:TextView? = null
        //var rating:RatingBar? = null

        var listener:ClickListener? = null
        var longListener:LongClinckListener? = null

        init {
            foto=vista.findViewById(R.id.ivFoto_Venues)
            iconoCategoria=vista.findViewById(R.id.ivCategory)
            nombre=vista.findViewById(R.id.tvNombre)
            state=vista.findViewById(R.id.tvState)
            category=vista.findViewById(R.id.tvCategory)
            checkins=vista.findViewById(R.id.tvCheckins)

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