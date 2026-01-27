package models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.marvelallies.R

class CommentaryAdapter : RecyclerView.Adapter<CommentaryAdapter.CommentaryViewHolder>() {

    private val commentaries = mutableListOf<CommentaryItem>()

    fun submitList(newList: List<CommentaryItem>) {
        commentaries.clear()
        commentaries.addAll(newList)
        notifyDataSetChanged()
    }

    //Se llama cada vez que el recycler necesita crear una nuevo item.
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.commentary_item, parent, false)
        return CommentaryViewHolder(view)
    }

    //Se llama para asignar los datos a cada vista
    override fun onBindViewHolder(holder: CommentaryViewHolder, position: Int) {
        holder.bind(commentaries[position])
    }

    //Devuelve el número total de ítems
    override fun getItemCount(): Int {
        return commentaries.size
    }

    class CommentaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val commentaryName: TextView = itemView.findViewById(R.id.PlayerName)
        private val commentaryText: TextView = itemView.findViewById(R.id.PlayerCommentary)
        private val playerImage: ImageView = itemView.findViewById(R.id.PlayerImage)



        //bindear el nombre y la description
        fun bind(commentary: CommentaryItem) {
            commentaryName.text = commentary.name
            commentaryText.text = commentary.commentary

            if (commentary.imageURL.isBlank())
            {
                // Si no hay imagen (ya no usamos API), usamos placeholder fijo
                playerImage.setImageResource(R.drawable.frame_1)
            }
            else
            {
                Glide.with(itemView.context)
                    .load("https://marvelrivalsapi.com/rivals" + commentary.imageURL)
                    .placeholder(R.drawable.frame_1)
                    .fitCenter()
                    .into(playerImage)
            }
        }
    }

}
