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
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import Player
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem

class PlayersProfile : Fragment()
{
    /*
     * Estos elementos representan toda la información visual
     * asociada al perfil detallado de un jugador
     */
    private lateinit var _playerImageView: ImageView
    private lateinit var _playerName: TextView
    private lateinit var _playerUID: TextView
    private lateinit var _playerRank: TextView
    private lateinit var _playerScore: TextView
    private lateinit var _playerTimePlayed: TextView
    private lateinit var _playerMatches: TextView
    private lateinit var _roleVanguard: TextView
    private lateinit var _roleDuelist: TextView
    private lateinit var _roleStrategist: TextView
    private lateinit var _statKDA: TextView
    private lateinit var _statKD: TextView
    private lateinit var _statMVP: TextView
    private lateinit var _progressBar: ProgressBar
    private lateinit var _dataLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        /*
         * Inflo el layout del perfil del jugador y preparo
         * todos los componentes visuales que se actualizarán dinámicamente
         */
        val view = inflater.inflate(R.layout.fragment_players_profile, container, false)

        // Views
        _playerImageView = view.findViewById(R.id.PlayerImage)
        _playerName = view.findViewById(R.id.NamePlayer)
        _playerUID = view.findViewById(R.id.UidPlayer)
        _playerRank = view.findViewById(R.id.RankText)
        _playerScore = view.findViewById(R.id.ScoreText)
        _playerTimePlayed = view.findViewById(R.id.TimeText)
        _playerMatches = view.findViewById(R.id.MatchesText)
        _roleVanguard = view.findViewById(R.id.VanguardText)
        _roleDuelist = view.findViewById(R.id.DuelistText)
        _roleStrategist = view.findViewById(R.id.StrategistText)
        _statKDA = view.findViewById(R.id.KdaText)
        _statKD = view.findViewById(R.id.KdText)
        _statMVP = view.findViewById(R.id.MvpText)
        _progressBar = view.findViewById(R.id.progressBar)
        _dataLayout = view.findViewById(R.id.DataLayout)

        /*
         * Obtengo el UID del jugador enviado desde el fragment anterior
         * Si el valor existe, inicio la carga del perfil
         */
        val query = arguments?.getString("player_id")
        query?.let { LoadPlayer(it) }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        /*
         * Configuro la Toolbar para permitir regresar
         * a la lista de jugadores
         */
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_24)
            title = getString(R.string.Players)
        }
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        /*
         * Manejo manualmente el botón de regreso
         * utilizando el back stack del FragmentManager
         */
        return when (item.itemId)
        {
            android.R.id.home -> {
                parentFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume()
    {
        super.onResume()
        /*
         * Aseguro que el título de la actividad
         * se mantenga consistente al volver al fragment
         */
        requireActivity().title = getString(R.string.Players)
    }

    private fun LoadPlayer(playerUid: String)
    {
        /*
         * Muestro la barra de progreso mientras se consulta
         * la información detallada del jugador
         */
        _progressBar.visibility = View.VISIBLE

        MarvelAPIInstance.apiService.getPlayerById(playerUid)
            .enqueue(object : Callback<Player>
            {
                override fun onResponse(call: Call<Player>, response: Response<Player>)
                {
                    _progressBar.visibility = View.GONE

                    if (!response.isSuccessful)
                    {
                        Log.e("API", "Error: ${response.code()}")
                        handleApiError(response.code())
                        return
                    }

                    /*
                     * Si la respuesta es válida, enlazo los datos
                     * del jugador con los componentes visuales
                     */
                    val player = response.body() ?: return
                    bindPlayerData(player)
                }

                override fun onFailure(call: Call<Player>, t: Throwable)
                {
                    _progressBar.visibility = View.GONE
                    Log.e("API", "Error: ${t.message}", t)
                    _playerName.text = getString(R.string.APIError)
                    hideEverything()
                }
            })
    }

    private fun handleApiError(code: Int)
    {
        /*
         * Manejo explícitamente errores conocidos de la API
         * para mostrar un mensaje claro al usuario
         */
        when (code)
        {
            403 -> {
                _playerName.text = getString(R.string.Private)
                hideEverything()
            }
            429 -> {
                _playerName.text = getString(R.string.Requests)
                hideEverything()
            }
        }
    }

    private fun bindPlayerData(player: Player)
    {
        /*
         * Obtengo el tiempo jugado por rol
         * Uso valores por defecto para evitar errores por datos nulos
         */
        var timeVanguard =
            player.overall_stats.roles_played.vanguard?.total_time_played?.time_played ?: 0f
        var timeDuelist =
            player.overall_stats.roles_played.duelist?.total_time_played?.time_played ?: 0f
        var timeStrategist =
            player.overall_stats.roles_played.strategist?.total_time_played?.time_played ?: 0f

        /*
         * Calculo el total y convierto cada valor a porcentaje
         * para representar correctamente la distribución de roles
         */
        val totalTime = timeVanguard + timeDuelist + timeStrategist
        timeVanguard = intToPercent(totalTime, timeVanguard)
        timeDuelist = intToPercent(totalTime, timeDuelist)
        timeStrategist = intToPercent(totalTime, timeStrategist)

         // Asigno los valores básicos del perfil
        _playerName.text = player.name
        _playerUID.text = player.uid.toString()
        _playerRank.text = player.player.rank.rank
        _playerScore.text = player.player.rank.score
        _playerTimePlayed.text = player.overall_stats.total_play_time.playtime
        _playerMatches.text = player.overall_stats.total_matches.toString()

        /*
         * Muestro los porcentajes de uso por rol
         * con formato controlado para mejor legibilidad
         */
        _roleVanguard.text = "${getString(R.string.Vanguard)}: ${String.format("%.2f", timeVanguard)}%"
        _roleDuelist.text = "${getString(R.string.Duelist)}: ${String.format("%.2f", timeDuelist)}%"
        _roleStrategist.text = "${getString(R.string.Strategist)}: ${String.format("%.2f", timeStrategist)}%"

         // Muestro estadísticas generales del jugador.
        _statKDA.text = "${getString(R.string.KDA)}: ${String.format("%.2f", player.overall_stats.overall_kda.kda)}"
        _statKD.text = "${getString(R.string.KD)}: ${String.format("%.2f", player.overall_stats.overall_kd)}"
        _statMVP.text = "${getString(R.string.MVP)}: ${player.overall_stats.total_mvps.mvps}"

        /*
         * Cargo la imagen del jugador utilizando Glide
         * para manejo eficiente de caché y errores
         */
        Glide.with(requireContext())
            .load("https://marvelrivalsapi.com/rivals${player.player.icon.player_icon}")
            .placeholder(R.drawable.frame_1)
            .fitCenter()
            .into(_playerImageView)
    }

    private fun intToPercent(total: Float, value: Float): Float
    {
        // Convierto un valor absoluto a porcentaje,
        return if (total > 0) (value * 100) / total else 0f
    }

    private fun hideEverything()
    {
        /*
         * Oculto toda la información del perfil
         * cuando no es posible mostrar datos válidos
         */
        _playerName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)
        _dataLayout.visibility = View.INVISIBLE
        _playerUID.visibility = View.INVISIBLE
    }
}
