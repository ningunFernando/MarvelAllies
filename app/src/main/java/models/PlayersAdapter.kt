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

class PlayersAdapter(
    private val _players: List<PlayersItem>,
    private val _onItemClickListener: (PlayersItem) -> Unit
): RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder>()
{
    //Se llama cada vez que el recycler necesita crear una nuevo item.
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayersAdapter.PlayerViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.players_item, parent, false)
        return PlayerViewHolder(view)
    }

    //Se llama para asignar los datos a cada vista
    override fun onBindViewHolder(holder: PlayersAdapter.PlayerViewHolder, position: Int)
    {
        val player = _players[position]
        holder.bind(player, _onItemClickListener)
    }

    //Devuelve el número total de ítems
    override fun getItemCount(): Int
    {
        return _players.size
    }

    class PlayerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        private val _playerName: TextView = itemView.findViewById(R.id.PlayerName)
        private val _playerUID: TextView = itemView.findViewById(R.id.PlayerUID)
        private val _playerRank: TextView = itemView.findViewById(R.id.Rank)
        private val _playerScore: TextView = itemView.findViewById(R.id.Score)
        private val _playerImage: ImageView = itemView.findViewById(R.id.PlayerImage)
        private val _playerLayout: LinearLayout = itemView.findViewById(R.id.PlayerLayout)

        //bindea los datos de los jugadores
        fun bind(player: PlayersItem, listener: (PlayersItem) -> Unit)
        {
            _playerName.text = player.name
            _playerUID.text = player.query.toString()
            _playerRank.text = player.rank
            _playerScore.text = player.score.toString()
            Glide.with(itemView.context)
                //Si carga toma la imagen de este URL
                .load("https://marvelrivalsapi.com/rivals"+player.player_icon)
                //Si no pone una de placeholder
                .placeholder(R.drawable.frame_1)
                //Acomoda la imagen en el centro del item
                .fitCenter()
                //En la imagen del item
                .into(_playerImage)

            _playerLayout.setOnClickListener {
                // Llama al callback
                listener(player)
            }
        }
    }
}