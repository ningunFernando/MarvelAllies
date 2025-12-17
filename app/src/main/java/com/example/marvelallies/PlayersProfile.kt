package com.example.marvelallies

import MarvelAPI.MarvelAPIInstance
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PlayersProfile : Fragment() {

    //Declarar todos los elementos que se van a modificar dentro del fragment
    private lateinit var playerImageView: ImageView
    private lateinit var playerName: TextView
    private lateinit var playerUID: TextView
    private lateinit var playerRank: TextView
    private lateinit var playerScore: TextView
    private lateinit var playerTimePlayed: TextView
    private lateinit var playerMatches: TextView
    private lateinit var roleVanguard: TextView
    private lateinit var roleDuelist: TextView
    private lateinit var roleStrategist: TextView
    private lateinit var statKDA: TextView
    private lateinit var statKD: TextView
    private lateinit var statMVP: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_players_profile, container, false)


        //Encontrar las views dentro del layout
        playerImageView = view.findViewById(R.id.PlayerImage)
        playerName = view.findViewById(R.id.NamePlayer)
        playerUID = view.findViewById(R.id.UidPlayer)
        playerRank = view.findViewById(R.id.RankText)
        playerScore = view.findViewById(R.id.ScoreText)
        playerTimePlayed = view.findViewById(R.id.TimeText)
        playerMatches = view.findViewById(R.id.MatchesText)
        roleVanguard = view.findViewById(R.id.VanguardText)
        roleDuelist = view.findViewById(R.id.DuelistText)
        roleStrategist = view.findViewById(R.id.StrategistText)
        statKDA = view.findViewById(R.id.KdaText)
        statKD = view.findViewById(R.id.KdText)
        statMVP = view.findViewById(R.id.MvpText)

        val query = arguments?.getString("player_id")

        LoadPlayer(query.toString())

        return (view)
    }

    private fun LoadPlayer(playerUid: String) {
        MarvelAPIInstance.apiService.getPlayerById(playerUid).
        enqueue(object : Callback<Player> {
            override fun onResponse(
                call: Call<Player>,
                response: Response<Player>
            ) {
                if (!response.isSuccessful) {
                    //mensaje error
                    Log.e("API", "Error: ${response.code()}")
                    //cargar la leaderboard en su lugar
                    return
                }
                //obtener la respuesta
                val player = response.body() ?: return

                playerName.text = player.name
                playerUID.text = player.uid.toString()




            }

            override fun onFailure(call: Call<Player>, t: Throwable) {
                Log.e("API", "Error: ${t.message}", t)            }

        })
    }


}