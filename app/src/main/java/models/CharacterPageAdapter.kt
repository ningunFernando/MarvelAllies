package models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marvelallies.R

class CharactersPageAdapter(
    //valor de las diferentes pages que se van a crear
    private val pages: List<CharactersBanner>,
    //Valor
    private val onItemClickListener: (CharactersItem) -> Unit
) : RecyclerView.Adapter<CharactersPageAdapter.PageViewHolder>() {



    //Se llama cada vez que el carrusel necesita crear una nuevo item.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.carrusel_item, parent, false)
        return PageViewHolder(view)
    }
    //Se llama para asignar los datos a cada vista
    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(pages[position], onItemClickListener)
    }
    //Devuelve el número total de ítems
    override fun getItemCount() = pages.size



    class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Encontrar el recicler View
        private val gridRecycler: RecyclerView = itemView.findViewById(R.id.gridRecyclerView)

        fun bind(page: CharactersBanner, listener: (CharactersItem) -> Unit) {
            //Hacemos que sea una cuadricula de 3x3
            gridRecycler.layoutManager = GridLayoutManager(itemView.context, 3)

            //Agarramos los personajes del character adapter
            gridRecycler.adapter = CharacterAdapter(page.characters, listener)        }
    }
}

