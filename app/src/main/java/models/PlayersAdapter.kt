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
    private val players: List<PlayersItem>,
    private val onItemClickListener: (PlayersItem) -> Unit
): RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayersAdapter.PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.players_item, parent, false)

        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayersAdapter.PlayerViewHolder, position: Int) {
        val player = players[position]
        holder.bind(player, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return players.size
    }

    class PlayerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val playerName: TextView = itemView.findViewById(R.id.PlayerName)
        private val playerUID: TextView = itemView.findViewById(R.id.PlayerUID)
        private val playerRank: TextView = itemView.findViewById(R.id.Rank)
        private val playerScore: TextView = itemView.findViewById(R.id.Score)
        private val playerImage: ImageView = itemView.findViewById(R.id.PlayerImage)
        private val playerLayout: LinearLayout = itemView.findViewById(R.id.PlayerLayout)

        fun bind(player: PlayersItem, listener: (PlayersItem) -> Unit){
            playerName.text = player.name
            playerUID.text = player.uid.toString()
            playerRank.text = player.rank
            playerScore.text = player.score.toString()
            Glide.with(itemView.context)
                //Si carga toma la imagen de este URL
                .load("https://marvelrivalsapi.com/rivals"+player.player_icon)
                //Si no pone una de placeholder
                .placeholder(R.drawable.frame_1)
                //Acomoda la imagen en el centro del item
                .fitCenter()
                //En la imagen del item
                .into(playerImage)

            playerLayout.setOnClickListener {
                // Llama al callback
                listener(player)
            }

        }


    }


}