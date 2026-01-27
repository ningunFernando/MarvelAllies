package com.example.marvelallies

import MarvelAPI.MarvelAPIInstance
import Player
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.gms.auth.api.signin.GoogleSignInClient


class Profile : Fragment()
{
    //FIREBASE VARIABLES
    private lateinit var _auth: FirebaseAuth
    private lateinit var _dataBase: FirebaseFirestore
    private lateinit var _googleSingInClient: GoogleSignInClient

    //UID API SETUP
    private lateinit var _layoutApiIdSetup: LinearLayout
    private lateinit var _layoutProfileContent: LinearLayout
    private lateinit var _etApiId: EditText
    private lateinit var _btnSaveApiId: Button

    //PROFILE UI
    private lateinit var _imageProfile: ImageView
    private lateinit var _nameProfile: TextView
    private lateinit var _uidProfile: TextView
    private lateinit var _rankTextProfile: TextView
    private lateinit var _scoreTextProfile: TextView
    private lateinit var _timeTextProfile: TextView
    private lateinit var _matchesTextProfile: TextView
    private lateinit var _vanguardTextProfile: TextView
    private lateinit var _duelistTextProfile: TextView
    private lateinit var _strategistTextProfile: TextView
    private lateinit var _overallProfile: TextView
    private lateinit var _kdaTextProfile: TextView
    private lateinit var _kdTextProfile: TextView
    private lateinit var _mvpTextProfile: TextView
    private lateinit var _btnEditProfile: Button
    private lateinit var _btnSignOut: Button
    private lateinit var _btnExitApp: Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onResume()
    {
        super.onResume()
        requireActivity().title = getString(R.string.Profile)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        //FIREBASE VARIABLES
        _auth = FirebaseAuth.getInstance()
        _dataBase = FirebaseFirestore.getInstance()

        //UID API SETUP
        _layoutApiIdSetup = view.findViewById(R.id.layoutApiIdSetup)
        _layoutProfileContent = view.findViewById(R.id.layoutProfileContent)
        _etApiId = view.findViewById(R.id.etApiId)
        _btnSaveApiId = view.findViewById(R.id.btnSaveApiId)

        _btnSaveApiId.setOnClickListener { saveApiId()}

        //PROFILE UI
        _imageProfile = view.findViewById(R.id.imageProfile)
        _nameProfile = view.findViewById(R.id.NameProfile)
        _uidProfile = view.findViewById(R.id.UidProfile)
        _rankTextProfile = view.findViewById(R.id.RankTextProfile)
        _scoreTextProfile = view.findViewById(R.id.ScoreTextProfile)
        _timeTextProfile = view.findViewById(R.id.TimeTextProfile)
        _matchesTextProfile = view.findViewById(R.id.MatchesTextProfile)
        _vanguardTextProfile = view.findViewById(R.id.VanguardTextProfile)
        _duelistTextProfile = view.findViewById(R.id.DuelistTextProfile)
        _strategistTextProfile = view.findViewById(R.id.StrategistTextProfile)
        _overallProfile = view.findViewById(R.id.OverallProfile)
        _kdaTextProfile = view.findViewById(R.id.KdaTextProfile)
        _kdTextProfile = view.findViewById(R.id.KdTextProfile)
        _mvpTextProfile = view.findViewById(R.id.MvpTextProfile)
        _btnEditProfile = view.findViewById(R.id.btnEditProfile)
        _btnSignOut = view.findViewById(R.id.btnSignOut)
        _btnExitApp = view.findViewById(R.id.btnExitApp)

        //BOTONES PARA SING OUT Y EDITAR PERFIL
        _btnSignOut.setOnClickListener { SingOut() }
        _btnEditProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, EditProfile())
                .addToBackStack(null)
                .commit()
        }
        _btnExitApp.setOnClickListener{
            requireActivity().finishAffinity()
        }

        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.apply {
            title = getString(R.string.Profile)
            setDisplayHomeAsUpEnabled(false)
            setHomeAsUpIndicator(null)
        }
        loadUserAndToggleUi()
    }

    private fun loadUserAndToggleUi()
    {
        /*
         * Obtengo el usuario actual desde FirebaseAuth
         * Si es null, significa que no hay sesion activa
         */
        val user = _auth.currentUser
        if(user == null)
        {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()
            return
        }

        //el uid es mi identificador de firebase auth y mi id en la firestore
        val uid = user?.uid ?: ""
        val email = user?.email ?: ""

        //referencia a mi doc en firestore
        val docRef = _dataBase.collection("users").document(uid)

        docRef.get()
            .addOnSuccessListener { doc ->
                //si el documento no existe se crea
                //se guarda email y apiid en el documento para poder llamarlos luego desde firestore
                if(!doc.exists())
                {
                    val newUserData = hashMapOf(
                        "email" to email,
                        "apiId" to null
                    )
                    docRef.set(newUserData)
                        .addOnSuccessListener {
                            showApiSetup()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error saving user data", Toast.LENGTH_SHORT).show()
                            showApiSetup()
                        }
                    return@addOnSuccessListener
                }
                //si ya existe el registro de apiId se muestra el perfil
                //si no existe se muestra la pantalla de setup de apiId
                val apiId = doc.getLong("apiId")
                if(apiId == null)
                {
                    showApiSetup()
                }
                else
                {
                    showFullProfile()
                    loadPlayer(apiId.toString())
                }
        }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error loading user data", Toast.LENGTH_SHORT).show()
                showApiSetup()
            }
    }


    //Firebase setup for the profile uid upload and creation of a firestore colection
    private fun saveApiId()
    {
        val user = _auth.currentUser ?: run {
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val apidIdText = _etApiId.text.toString()
        if(apidIdText.isBlank())
        {
            _etApiId.error = "Your Rivals UID is required"
            return
        }

        val apiIdNumber = apidIdText.toLongOrNull()
        if(apiIdNumber == null)
        {
            _etApiId.error = "Your Rivals UID must be a number"
            return
        }
        
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val docRef = FirebaseFirestore.getInstance().collection("users").document(uid)

        docRef.update("apiId", apiIdNumber)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "UID saved", Toast.LENGTH_SHORT).show()
                showFullProfile()
            }
            .addOnFailureListener { e ->
               Log.e("Profile", "Error saving UID", e)
                Toast.makeText(requireContext(), "Error saving UID: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showApiSetup()
    {
        _layoutApiIdSetup.visibility = View.VISIBLE
        _layoutProfileContent.visibility = View.GONE
    }

    private fun showFullProfile()
    {
        _layoutApiIdSetup.visibility = View.GONE
        _layoutProfileContent.visibility = View.VISIBLE
    }

    //Fetching api
    private fun loadPlayer(playerUid: String)
    {
        /*
         * Muestro la barra de progreso mientras se consulta
         * la información detallada del jugador
         */
        MarvelAPIInstance.apiService.getPlayerById(playerUid)
            .enqueue(object : Callback<Player>
            {
                override fun onResponse(
                    call: Call<Player>,
                    response: Response<Player>
                )
                {
                    if (!response.isSuccessful)
                    {
                        Log.e("API", "Error: ${response.code()}")
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
                    Toast.makeText(requireContext(), "API error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun intToPercent(total: Float, value: Float): Float
    {
        // Convierto un valor absoluto a porcentaje,
        return if (total > 0) (value * 100) / total else 0f
    }

    private fun bindPlayerData(player: Player)
    {
        var timeVanguard =
            player.overall_stats.roles_played.vanguard?.total_time_played?.time_played ?: 0f
        var timeDuelist =
            player.overall_stats.roles_played.duelist?.total_time_played?.time_played ?: 0f
        var timeStrategist =
            player.overall_stats.roles_played.strategist?.total_time_played?.time_played ?: 0f

        val totalTime = timeVanguard + timeDuelist + timeStrategist
        timeVanguard = intToPercent(totalTime, timeVanguard)
        timeDuelist = intToPercent(totalTime, timeDuelist)
        timeStrategist = intToPercent(totalTime, timeStrategist)

        _nameProfile.text = player.name
        _uidProfile.text = "UID: ${player.uid}"
        _rankTextProfile.text = player.player.rank.rank
        _scoreTextProfile.text = player.player.rank.score
        _timeTextProfile.text = player.overall_stats.total_play_time.playtime
        _matchesTextProfile.text = player.overall_stats.total_matches.toString()

        _vanguardTextProfile.text = "${getString(R.string.Vanguard)}: ${"%.2f".format(timeVanguard)}%"
        _duelistTextProfile.text = "${getString(R.string.Duelist)}: ${"%.2f".format(timeDuelist)}%"
        _strategistTextProfile.text = "${getString(R.string.Strategist)}: ${"%.2f".format(timeStrategist)}%"

        _kdaTextProfile.text = "${getString(R.string.KDA)}: ${"%.2f".format(player.overall_stats.overall_kda.kda)}"
        _kdTextProfile.text = "${getString(R.string.KD)}: ${"%.2f".format(player.overall_stats.overall_kd)}"
        _mvpTextProfile.text = "${getString(R.string.MVP)}: ${player.overall_stats.total_mvps.mvps}"

        Glide.with(requireContext())
            .load("https://marvelrivalsapi.com/rivals${player.player.icon.player_icon}")
            .placeholder(R.drawable.frame_1)
            .fitCenter()
            .into(_imageProfile)
    }


    //Sign out
    private fun SingOut()
    {
        _auth.signOut()
        _googleSingInClient.signOut()

        startActivity(Intent(requireContext(), LoginActivity::class.java))
    }
}