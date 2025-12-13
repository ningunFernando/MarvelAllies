package models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.marvelallies.R
import Skin


class SkinAdapter(
    private val skins: List<Skin>
) : RecyclerView.Adapter<SkinAdapter.CharacterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.skin_item, parent, false)
        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val skin = skins[position]
        holder.bind(skin)
    }

    override fun getItemCount(): Int {
        return skins.size
    }

    class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val skinImage: ImageView = itemView.findViewById(R.id.SkinImage)

        fun bind(skin: Skin) {
            Glide.with(itemView.context)
                .load("https://marvelrivalsapi.com/rivals${skin.icon}")
                .placeholder(R.drawable.frame_1)
                .fitCenter()
                .into(skinImage)
        }
    }
}
