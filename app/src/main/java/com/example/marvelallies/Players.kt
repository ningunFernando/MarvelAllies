package com.example.marvelallies

import MarvelAPI.MarvelAPIInstance
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import models.PlayersAdapter
import models.PlayersItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import Leaderboard

class Players : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchButton: ImageButton
    private lateinit var searchInputText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_players, container, false)

        searchButton = view.findViewById(R.id.Search)
        searchInputText = view.findViewById(R.id.SearchLayout)
        recyclerView = view.findViewById(R.id.RecyclerPlayers)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        LoadLeaderboard()

        return view
    }

    private fun LoadLeaderboard() { MarvelAPIInstance.apiService.getLeaderboard( page = 1, limit = 50).enqueue(object : Callback<Leaderboard> {

                override fun onResponse(
                    call: Call<Leaderboard>,
                    response: Response<Leaderboard>
                ) {
                    if (!response.isSuccessful || response.body() == null) {
                        Log.e("API", "Error: ${response.code()}")
                        return
                    }

                    val leaderboard = response.body()!!

                    val playersItems = leaderboard.players.map { player ->
                        PlayersItem(
                            uid = player.uid,
                            name = player.name,
                            player_icon = player.icon.player_icon,
                            rank = player.rank.rank.rank,
                            score = player.score
                        )
                    }


                    val adapter = PlayersAdapter(playersItems) { player ->
                        onCharacterClicked(player)
                    }

                    recyclerView.adapter = adapter
                }

                override fun onFailure(
                    call: Call<Leaderboard>,
                    t: Throwable
                ) {
                    Log.e("API", "Error: ${t.message}", t)
                }
            })
    }

    private fun onCharacterClicked(player: PlayersItem) {

    }
}
