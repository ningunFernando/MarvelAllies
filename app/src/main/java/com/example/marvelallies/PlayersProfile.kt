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
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.content.res.Configuration

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
    private lateinit var progressBar: ProgressBar


    private lateinit var back: ImageButton
    private lateinit var dataLayout: LinearLayout


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
        back = view.findViewById(R.id.Back)
        dataLayout = view.findViewById(R.id.DataLayout)

        progressBar = view.findViewById(R.id.progressBar)




        back.setOnClickListener {
            replaceFragment()
        }

        val query = arguments?.getString("player_id")

        LoadPlayer(query.toString())

        return (view)
    }

    private fun LoadPlayer(playerUid: String) {
        progressBar.visibility = View.VISIBLE

        MarvelAPIInstance.apiService.getPlayerById(playerUid).
        enqueue(object : Callback<Player> {
            override fun onResponse(
                call: Call<Player>,
                response: Response<Player>
            ) {
                progressBar.visibility = View.GONE

                if (!response.isSuccessful) {
                    //mensaje error
                    Log.e("API", "Error: ${response.code()}")
                    if(response.code() == 403)
                    {
                        playerName.text = "This profile is private"
                        hideEverything()
                        return
                    }
                    if(response.code() == 429)
                    {
                        playerName.text = "Too many requests please try later"
                        hideEverything()

                        return
                    }

                    //cargar la leaderboard en su lugar
                    return
                }

                //obtener la respuesta
                val player = response.body() ?: return

                //variables con el tiempo de cada role y verifica checar si envia un valor nulo, en ese caso es igual a 0
                var timeVanguard  = player.overall_stats.roles_played.vanguard?.total_time_played?.time_played ?: 0f
                var timeDuelist  = player.overall_stats.roles_played.duelist?.total_time_played?.time_played ?: 0f
                var timeStrategist = player.overall_stats.roles_played.strategist?.total_time_played?.time_played ?: 0f

                //una sumatoria de todos los tiempos
                val rolesTotalTime = timeVanguard +timeDuelist+timeStrategist

                //convertirlo en porcentaje
                timeVanguard = intToPercent(rolesTotalTime, timeVanguard)
                timeDuelist = intToPercent(rolesTotalTime, timeDuelist)
                timeStrategist = intToPercent(rolesTotalTime, timeStrategist)

                //Change values
                playerName.text = player.name
                playerUID.text = player.uid.toString()
                playerRank.text = player.player.rank.rank
                playerScore.text = player.player.rank.score
                playerTimePlayed.text = player.overall_stats.total_play_time.playtime
                playerMatches.text = player.overall_stats.total_matches.toString()

                //agregar el porcentaje de uso y dejar el float en dos decimales
                var vanguardText: String = "Vanguard: ${String.format("%.2f", timeVanguard)}%"
                var duelistText: String = "Duelist: ${String.format("%.2f", timeDuelist)}%"
                var strategistText: String = "Strategist: ${String.format("%.2f", timeStrategist)}%"

                roleVanguard.text = vanguardText
                roleDuelist.text = duelistText
                roleStrategist.text = strategistText

                //cambiar las overal stats y dejar el float en dos decimales

                var kdaTextText = "KDA :  ${String.format("%.2f", player.overall_stats.overall_kda.kda)}"

                var kdTextText = "KD :  ${String.format("%.2f", player.overall_stats.overall_kd)}"

                var mvpText: String = player.overall_stats.total_mvps.mvps.toString()
                var mvpTextText = "MVP :  $mvpText"

                statKDA.text = kdaTextText
                statKD.text = kdTextText
                statMVP.text = mvpTextText

                Glide.with(requireContext())
                    //Si carga toma la imagen de este URL
                    .load("https://marvelrivalsapi.com/rivals"+player.player.icon.player_icon)
                    //Si no pone una de placeholder
                    .placeholder(R.drawable.frame_1)
                    //Acomoda la imagen en el centro del item
                    .fitCenter()
                    //En la imagen del item
                    .into(playerImageView)



            }

            override fun onFailure(call: Call<Player>, t: Throwable) {
                Log.e("API", "Error: ${t.message}", t)            }

        })
    }

    //convertirlo en porcentaje con una regla de 3
    private fun intToPercent(total: Float, role: Float): Float {
        var percentage: Float = (role*100)/total
        return percentage
    }

    //moverse al fragment characters
    private fun replaceFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, Players())
            .commit()
    }

    //Ocultar todo lo que no se va mostrar y hacer grande el texto
    private fun hideEverything()
    {
        playerName.setTextSize(TypedValue.COMPLEX_UNIT_SP,40f);
        dataLayout.visibility = View.INVISIBLE
        playerUID.visibility = View.INVISIBLE

    }

}