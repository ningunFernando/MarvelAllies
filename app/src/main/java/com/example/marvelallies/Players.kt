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
import Player
 import android.widget.LinearLayout

class Players : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchButton: ImageButton
    private lateinit var playersLayout: LinearLayout
    private lateinit var searchInputText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_players, container, false)

        //Encontrar los elementos en la toolbar
        searchButton = view.findViewById(R.id.Search)
        searchInputText = view.findViewById(R.id.SearchLayout)
        playersLayout = view.findViewById(R.id.PlayersLayout)

        //cuando se clickea el boton de lupa
        searchButton.setOnClickListener{
            ShowLayout()
        }

        //cuando se clickea afuera de la lupa dentro del toolbar
        playersLayout.setOnClickListener{
            HideLayout()
        }

        //Encontrar el recycler view
        recyclerView = view.findViewById(R.id.RecyclerPlayers)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //Cargar el Leaderboard
        LoadLeaderboard()

        return view
    }

    //Carga el leaderboard con un limite de 50 jugadores
    private fun LoadLeaderboard() {
        MarvelAPIInstance.apiService.getLeaderboard( page = 1, limit = 50).enqueue(object : Callback<Leaderboard> {

                override fun onResponse(
                    call: Call<Leaderboard>,
                    response: Response<Leaderboard>
                ) {
                    if (!response.isSuccessful || response.body() == null) {
                        Log.e("API", "Error: ${response.code()}")
                        return
                    }

                    //tomar la variable de la rrespuesta
                    val leaderboard = response.body()!!

                    //mapear las respuestas de la api para que concuerden con el players item
                    val playersItems = leaderboard.players.map { player ->
                        PlayersItem(
                            uid = player.uid,
                            name = player.name,
                            player_icon = player.icon.player_icon,
                            rank = player.rank.rank.rank,
                            score = player.score
                        )
                    }

                    //el adaptador sera igual a la respuesta de la api y detectar los clicks
                    val adapter = PlayersAdapter(playersItems) { player ->
                        onCharacterClicked(player)
                    }

                    //Cambiar el recycler view
                    recyclerView.adapter = adapter
                }

        //En caso de que haya error
                override fun onFailure(call: Call<Leaderboard>, t: Throwable) {
                    Log.e("API", "Error: ${t.message}", t)
                }
            })
    }

    private fun LoadPlayer(playerUid: String) {
        MarvelAPIInstance.apiService.getPlayerById(playerUid).
        enqueue(object : Callback<Player> {
            override fun onResponse(
                call: Call<Player>,
                response: Response<Player>
            ) {
                val player = response.body() ?: return

                val playerItem = PlayersItem(
                    uid = player.uid.toString(),
                    name = player.name,
                    player_icon = player.player.icon.player_icon,
                    rank = player.player.rank.rank,
                    score = player.player.rank.score.toInt()
                )

                val adapter = PlayersAdapter(listOf(playerItem)) { player->
                    onCharacterClicked(player)
                }

                recyclerView.adapter = adapter
                println("He cambiado el adapter")

            }

            override fun onFailure(call: Call<Player>, t: Throwable) {
                Log.e("API", "Error: ${t.message}", t)            }

        })
    }

    private fun onCharacterClicked(player: PlayersItem) {
        // Aquí puedes navegar al fragmento de detalles
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        //Pasar argumentos al segundo fragment
        val detailsFragment = PlayersProfile().apply {
            arguments = Bundle().apply {
                //mandar id y nombre al fragment details
                putString("character_id", player.uid)
            }

        }
        //cambiar de fragment
        fragmentTransaction.replace(R.id.frameLayout, detailsFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    //Mostrar el editor de texto
    private fun ShowLayout(){
        if(searchInputText.visibility == View.INVISIBLE) {
            searchInputText.visibility = View.VISIBLE
        }else{
            //println(searchInputText.text)
            LoadPlayer(searchInputText.text.toString())
        }
    }

    //Esconder el editor de texto
    private fun HideLayout(){
        searchInputText.visibility = View.INVISIBLE
    }
}
