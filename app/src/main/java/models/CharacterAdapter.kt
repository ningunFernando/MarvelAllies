package models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.marvelallies.R

class CharacterAdapter(
    private val items: List<CharactersItem>,
    private val onItemClickListener: (CharactersItem) -> Unit
) : RecyclerView.Adapter<CharacterAdapter.GridViewHolder>() {

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val characterLayout: LinearLayout = itemView.findViewById(R.id.CharacterLayout)
        fun SetListener(){

        }
    }

    //Se llama cada vez que el carrusel necesita crear una nuevo item.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.character_item, parent, false)
        return GridViewHolder(view)
    }

    //Se llama para asignar los datos a cada vista
    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        holder.bind(items[position], onItemClickListener)
    }

    //Devuelve el número total de ítems
    override fun getItemCount() = items.size

    class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.characterImage)
        private val layoutItem: LinearLayout = itemView.findViewById(R.id.CharacterLayout)


        fun bind(character: CharactersItem, listener: (CharactersItem) -> Unit) {

            //Si un item no recibe ningun dato, hacemos que el texto y la imagen se hagan invisibles
            if (character.name.isBlank()) {
                image.visibility = View.INVISIBLE
                return
            }

            image.visibility = View.VISIBLE
            //toma el nombre del personaje desde la API

            //usamos la libreria de Glide para tomar imagenes de un URL
            Glide.with(itemView.context)
                //Si carga toma la imagen de este URL
                .load("https://marvelrivalsapi.com/"+character.imageUrl)
                //Si no pone una de placeholder
                .placeholder(R.drawable.frame_1)
                .centerCrop()
                //En la imagen del item
                .into(image)

            layoutItem.setOnClickListener {
                listener(character) // Llama al callback
            }

        }

    }

}
