package models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marvelallies.R

class CharactersPagerAdapter(
    private val pages: List<CharactersBanner>
) : RecyclerView.Adapter<CharactersPagerAdapter.PageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carrusel_item, parent, false)
        return PageViewHolder(view)
    }

    override fun getItemCount() = pages.size

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gridRecycler: RecyclerView = itemView.findViewById(R.id.gridRecycler)

        fun bind(page: CharactersBanner) {
            gridRecycler.layoutManager = GridLayoutManager(itemView.context, 3)
            gridRecycler.adapter = CharacterAdapter(page.characters)
        }
    }
}
