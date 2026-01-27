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

class Players : Fragment()
{
    /*
     * RecyclerView encargado de mostrar el leaderboard
     * o el resultado de una búsqueda específica
     */
    private lateinit var _recyclerView: RecyclerView

    /*
     * Barra de progreso que indica el estado de carga
     * durante las peticiones a la API
     */
    private lateinit var _progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        /*
         * Indico que este fragment define su propio menú,
         * necesario para habilitar el buscador en la Toolbar
         */
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        /*
         * Inflo el layout del fragment y preparo los componentes
         * que mostrarán la información de los jugadores
         */
        val view = inflater.inflate(R.layout.fragment_players, container, false)

        _recyclerView = view.findViewById(R.id.RecyclerPlayers)
        _progressBar = view.findViewById(R.id.progressBar)

        /*
         * Utilizo un LinearLayoutManager para presentar
         * los jugadores en una lista vertical
         */
        _recyclerView.layoutManager = LinearLayoutManager(requireContext())

         // Cargo el leaderboard completo al entrar al fragment
        loadLeaderboard()
        return view
    }

    override fun onResume()
    {
        super.onResume()
        /*
         * Configuro la Toolbar para esta sección,
         * ocultando el botón de regreso al tratarse de una pantalla principal
         */
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setHomeAsUpIndicator(null)
            title = getString(R.string.Players)
        }
    }

    // Menu con buscador
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        inflater.inflate(R.menu.top_bar_search_players, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = getString(R.string.Name)

        /*
         * Configuro el buscador para ejecutar la búsqueda
         * únicamente cuando el usuario confirma la acción
         */
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String?): Boolean
            {
                /*
                 * Este método se ejecuta cuando el usuario confirma la búsqueda
                 * desde el teclado (por ejemplo, presionando Enter o el botón de búsqueda)
                 * Decido procesar la búsqueda únicamente en este punto para evitar
                 * realizar múltiples llamadas a la API mientras el usuario escribe
                 */
                if (!query.isNullOrBlank())
                {
                    loadPlayer(query)
                }

                 // Limpio el foco del SearchView para cerrar el teclado
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean
            {
                /*
                 * decido no utilizarlo para evitar búsquedas en tiempo real,
                 * Aunque no se use lo pide la interfaz
                 */
                return false
            }
        })

    }

    // Cargar leaderboard completo
    private fun loadLeaderboard()
    {
        /*
         * Muestro la barra de progreso antes de iniciar la llamada
         * para informar al usuario del estado de carga
         */
        _progressBar.visibility = View.VISIBLE

        /*
         * Solicito el leaderboard paginado desde la API
         * El límite se establece para reducir la cantidad de peticiones
         */
        MarvelAPIInstance.apiService.getLeaderboard(page = 1, limit = 50)
            .enqueue(object : Callback<Leaderboard>
            {
                override fun onResponse(call: Call<Leaderboard>, response: Response<Leaderboard>)
                {
                    _progressBar.visibility = View.GONE
                    if (!response.isSuccessful || response.body() == null)
                    {
                        Log.e("API", "Error: ${response.code()}")
                        return
                    }

                    /*
                     * Transformo el modelo de la API en un modelo
                     * adaptado a la capa de presentación
                     */
                    val playersItems = response.body()!!.players.map { player ->
                        PlayersItem(
                            query = player.uid,
                            name = player.name,
                            player_icon = player.icon.player_icon,
                            rank = player.rank.rank.rank,
                            score = player.score
                        )
                    }

                    _recyclerView.adapter = PlayersAdapter(playersItems)
                    { player ->
                        openPlayerProfile(player)
                    }
                }

                override fun onFailure(call: Call<Leaderboard>, t: Throwable)
                {
                    _progressBar.visibility = View.GONE
                    Log.e("API", "Error: ${t.message}", t)
                }
            })
    }

    // Cargar jugador por UID
    private fun loadPlayer(playerUid: String)
    {
        /*
         * Se realiza una búsqueda directa por UID
         * En caso de error, se vuelve a mostrar el leaderboard completo
         */
        _progressBar.visibility = View.VISIBLE

        MarvelAPIInstance.apiService.getPlayerById(playerUid)
            .enqueue(object : Callback<Player>
            {
                override fun onResponse(call: Call<Player>, response: Response<Player>)
                {
                    _progressBar.visibility = View.GONE

                    if (!response.isSuccessful || response.body() == null)
                    {
                        Log.e("API", "Error: ${response.code()}")
                        loadLeaderboard() // fallback
                        return
                    }

                    val player = response.body()!!

                    /*
                     * Normalizo el score eliminando separadores
                     * para poder manejarlo como valor numérico
                     */
                    val scoreData = player.player.rank.score
                        .replace(",", "")
                        .toInt()

                    val playerItem = PlayersItem(
                        query = player.uid.toString(),
                        name = player.name,
                        player_icon = player.player.icon.player_icon,
                        rank = player.player.rank.rank,
                        score = scoreData
                    )

                    _recyclerView.adapter = PlayersAdapter(listOf(playerItem))
                    { p ->
                        openPlayerProfile(p)
                    }
                }

                override fun onFailure(call: Call<Player>, t: Throwable)
                {
                    _progressBar.visibility = View.GONE
                    Log.e("API", "Error: ${t.message}", t)
                }
            })
    }

    private fun openPlayerProfile(player: PlayersItem)
    {
        /*
         * Manejo la navegación hacia el fragment de perfil
         * enviando el UID del jugador seleccionado
         */
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
