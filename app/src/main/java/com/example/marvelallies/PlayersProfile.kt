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


class PlayersProfile : Fragment() {

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
    private lateinit var dataLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_players_profile, container, false)

        // Views
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
        progressBar = view.findViewById(R.id.progressBar)
        dataLayout = view.findViewById(R.id.DataLayout)

        // Obtener ID del player
        val query = arguments?.getString("player_id")
        query?.let { LoadPlayer(it) }

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_24)
            title = getString(R.string.Players)
        }

        setHasOptionsMenu(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                parentFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onResume() {
        super.onResume()
        requireActivity().title = getString(R.string.Players)
    }

    private fun LoadPlayer(playerUid: String) {
        progressBar.visibility = View.VISIBLE

        MarvelAPIInstance.apiService.getPlayerById(playerUid)
            .enqueue(object : Callback<Player> {
                override fun onResponse(call: Call<Player>, response: Response<Player>) {
                    progressBar.visibility = View.GONE

                    if (!response.isSuccessful) {
                        Log.e("API", "Error: ${response.code()}")
                        handleApiError(response.code())
                        return
                    }

                    val player = response.body() ?: return
                    bindPlayerData(player)
                }

                override fun onFailure(call: Call<Player>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.e("API", "Error: ${t.message}", t)
                }
            })
    }

    private fun handleApiError(code: Int) {
        when(code) {
            403 -> {
                playerName.text = "This profile is private"
                hideEverything()
            }
            429 -> {
                playerName.text = "Too many requests, try later"
                hideEverything()
            }
        }
    }

    private fun bindPlayerData(player: Player) {
        var timeVanguard = player.overall_stats.roles_played.vanguard?.total_time_played?.time_played ?: 0f
        var timeDuelist = player.overall_stats.roles_played.duelist?.total_time_played?.time_played ?: 0f
        var timeStrategist = player.overall_stats.roles_played.strategist?.total_time_played?.time_played ?: 0f

        val totalTime = timeVanguard + timeDuelist + timeStrategist
        timeVanguard = intToPercent(totalTime, timeVanguard)
        timeDuelist = intToPercent(totalTime, timeDuelist)
        timeStrategist = intToPercent(totalTime, timeStrategist)

        playerName.text = player.name
        playerUID.text = player.uid.toString()
        playerRank.text = player.player.rank.rank
        playerScore.text = player.player.rank.score
        playerTimePlayed.text = player.overall_stats.total_play_time.playtime
        playerMatches.text = player.overall_stats.total_matches.toString()

        roleVanguard.text = "Vanguard: ${String.format("%.2f", timeVanguard)}%"
        roleDuelist.text = "Duelist: ${String.format("%.2f", timeDuelist)}%"
        roleStrategist.text = "Strategist: ${String.format("%.2f", timeStrategist)}%"

        statKDA.text = "KDA: ${String.format("%.2f", player.overall_stats.overall_kda.kda)}"
        statKD.text = "KD: ${String.format("%.2f", player.overall_stats.overall_kd)}"
        statMVP.text = "MVP: ${player.overall_stats.total_mvps.mvps}"

        Glide.with(requireContext())
            .load("https://marvelrivalsapi.com/rivals${player.player.icon.player_icon}")
            .placeholder(R.drawable.frame_1)
            .fitCenter()
            .into(playerImageView)
    }

    private fun intToPercent(total: Float, value: Float): Float {
        return if(total > 0) (value * 100) / total else 0f
    }

    private fun hideEverything() {
        playerName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)
        dataLayout.visibility = View.INVISIBLE
        playerUID.visibility = View.INVISIBLE
    }
}
