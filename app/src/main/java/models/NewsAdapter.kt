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

class NewsAdapter(private val _news: List<NewsItem>,
                  private val _onItemClickListener: (NewsItem) -> Unit
): RecyclerView.Adapter<NewsAdapter.NewsViewHolder>()
{
    //Se llama cada vez que el recycler necesita crear una nuevo item.

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsAdapter.NewsViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }

    //Se llama para asignar los datos a cada vista
    override fun onBindViewHolder(holder: NewsAdapter.NewsViewHolder, position: Int)
    {
        val player = _news[position]
        holder.bind(player, _onItemClickListener)
    }

    //Devuelve el número total de ítems
    override fun getItemCount(): Int
    {
        return _news.size
    }

    class NewsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        private val _newsTitle: TextView = itemView.findViewById(R.id.NewsTitle)
        private val _newsImage: ImageView = itemView.findViewById(R.id.NewsImage)
        private val _newsLayout: LinearLayout = itemView.findViewById(R.id.NewsLayout)

        //bindea la foto y el titulo de la noticia
        fun bind(new: NewsItem, listener: (NewsItem) -> Unit)
        {
            _newsTitle.text = new.title
            Glide.with(itemView.context)
                //Si carga toma la imagen de este URL
                .load("https://marvelrivalsapi.com/rivals"+new.imageUrl)
                //Si no pone una de placeholder
                .placeholder(R.drawable.frame_1)
                //Acomoda la imagen en el centro del item
                .fitCenter()
                //En la imagen del item
                .into(_newsImage)

            _newsLayout.setOnClickListener {
                // Llama al callback
                listener(new)
            }
        }
    }
}