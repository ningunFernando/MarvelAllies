package models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.marvelallies.R

class CharacterAdapter(
    private val items: List<CharactersItem>
) : RecyclerView.Adapter<CharacterAdapter.GridViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.character_item, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.characterName)
        private val image: ImageView = itemView.findViewById(R.id.characterImage)

        fun bind(character: CharactersItem) {

            if (character.name.isBlank()) {
                name.visibility = View.INVISIBLE
                image.visibility = View.INVISIBLE
                return
            }

            name.visibility = View.VISIBLE
            image.visibility = View.VISIBLE
            name.text = character.name

            Glide.with(itemView.context)
                .load(character.imageUrl)
                .placeholder(R.drawable.frame_1)
                .into(image)

        }
    }
}
