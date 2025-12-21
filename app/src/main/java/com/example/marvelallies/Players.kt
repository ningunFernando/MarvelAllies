package com.example.marvelallies

import MarvelAPI.MarvelAPIInstance
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import models.PlayersAdapter
import models.PlayersItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ProgressBar
import Leaderboard
import Player

class Players : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_players, container, false)

        recyclerView = view.findViewById(R.id.RecyclerPlayers)
        progressBar = view.findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadLeaderboard()

        return view
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setHomeAsUpIndicator(null)
            title = getString(R.string.Players)
        }
    }

    // Menu con buscador
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_bar_search_players, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView

        searchView.queryHint = getString(R.string.Name)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    loadPlayer(query)
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    // Cargar leaderboard completo
    private fun loadLeaderboard() {
        progressBar.visibility = View.VISIBLE

        MarvelAPIInstance.apiService.getLeaderboard(page = 1, limit = 50)
            .enqueue(object : Callback<Leaderboard> {
                override fun onResponse(call: Call<Leaderboard>, response: Response<Leaderboard>) {
                    progressBar.visibility = View.GONE
                    if (!response.isSuccessful || response.body() == null) {
                        Log.e("API", "Error: ${response.code()}")
                        return
                    }

                    val playersItems = response.body()!!.players.map { player ->
                        PlayersItem(
                            query = player.uid,
                            name = player.name,
                            player_icon = player.icon.player_icon,
                            rank = player.rank.rank.rank,
                            score = player.score
                        )
                    }

                    recyclerView.adapter = PlayersAdapter(playersItems) { player ->
                        openPlayerProfile(player)
                    }
                }

                override fun onFailure(call: Call<Leaderboard>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.e("API", "Error: ${t.message}", t)
                }
            })
    }

    // Cargar jugador por UID
    private fun loadPlayer(playerUid: String) {
        progressBar.visibility = View.VISIBLE

        MarvelAPIInstance.apiService.getPlayerById(playerUid)
            .enqueue(object : Callback<Player> {
                override fun onResponse(call: Call<Player>, response: Response<Player>) {
                    progressBar.visibility = View.GONE
                    if (!response.isSuccessful || response.body() == null) {
                        Log.e("API", "Error: ${response.code()}")
                        loadLeaderboard() // fallback
                        return
                    }

                    val player = response.body()!!
                    val scoreData = player.player.rank.score.replace(",", "").toInt()

                    val playerItem = PlayersItem(
                        query = player.uid.toString(),
                        name = player.name,
                        player_icon = player.player.icon.player_icon,
                        rank = player.player.rank.rank,
                        score = scoreData
                    )

                    recyclerView.adapter = PlayersAdapter(listOf(playerItem)) { p ->
                        openPlayerProfile(p)
                    }
                }

                override fun onFailure(call: Call<Player>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.e("API", "Error: ${t.message}", t)
                }
            })
    }

    // Navegar al perfil del jugador
    private fun openPlayerProfile(player: PlayersItem) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        val detailsFragment = PlayersProfile().apply {
            arguments = Bundle().apply {
                putString("player_id", player.query)
            }
        }
        fragmentTransaction.replace(R.id.frameLayout, detailsFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
