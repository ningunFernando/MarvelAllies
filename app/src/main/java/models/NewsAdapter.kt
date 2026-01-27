package models

import com.example.marvelallies.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class NewsAdapter(private val news: List<NewsItem>,
                  private val onItemClickListener: (NewsItem) -> Unit
): RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    //Se llama cada vez que el recycler necesita crear una nuevo item.

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsAdapter.NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)

        return NewsViewHolder(view)
    }

    //Se llama para asignar los datos a cada vista
    override fun onBindViewHolder(holder: NewsAdapter.NewsViewHolder, position: Int) {
        val player = news[position]
        holder.bind(player, onItemClickListener)
    }

    //Devuelve el número total de ítems

    override fun getItemCount(): Int {
        return news.size
    }

    class NewsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            private val newsTitle: TextView = itemView.findViewById(R.id.NewsTitle)
            private val newsImage: ImageView = itemView.findViewById(R.id.NewsImage)
            private val newsLayout: LinearLayout = itemView.findViewById(R.id.NewsLayout)

        //bindear el nombre y a la imagen
        fun bind(new: NewsItem, listener: (NewsItem) -> Unit){

            newsTitle.text = new.title
            Glide.with(itemView.context)
                //Si carga toma la imagen de este URL
                .load("https://marvelrivalsapi.com/rivals"+new.imageUrl)
                //Si no pone una de placeholder
                .placeholder(R.drawable.frame_1)
                //Acomoda la imagen en el centro del item
                .fitCenter()
                //En la imagen del item
                .into(newsImage)

            newsLayout.setOnClickListener {
                // Llama al callback
                listener(new)
            }

        }


    }

}