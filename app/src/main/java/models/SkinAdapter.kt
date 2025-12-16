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
) : RecyclerView.Adapter<SkinAdapter.SkinViewHolder>() {

    //Se llama cada vez que el recycler necesita crear una nuevo item.
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SkinViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.skin_item, parent, false)
        return SkinViewHolder(view)
    }

    //Se llama para asignar los datos a cada vista
    override fun onBindViewHolder(holder: SkinViewHolder, position: Int) {
        val skin = skins[position]
        holder.bind(skin)
    }

    //Devuelve el número total de ítems
    override fun getItemCount(): Int {
        return skins.size
    }

    class SkinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val skinImage: ImageView = itemView.findViewById(R.id.SkinImage)

        fun bind(skin: Skin) {
            //usar glide para meter la imagen de cada skin
            Glide.with(itemView.context)
                .load("https://marvelrivalsapi.com/rivals" + skin.icon)
                .placeholder(R.drawable.frame_1)
                .fitCenter()
                .into(skinImage)
        }
    }
}
