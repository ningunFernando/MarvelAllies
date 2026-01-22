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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class Profile : Fragment() {

    //FIREBASE VARIABLES
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleSingInClient: GoogleSignInClient

    //UID API SETUP
    private lateinit var layoutApiIdSetup: LinearLayout
    private lateinit var layoutProfileContent: LinearLayout
    private lateinit var etApiId: EditText
    private lateinit var btnSaveApiId: Button

    //PROFILE UI
    private lateinit var imageProfile: ImageView
    private lateinit var nameProfile: TextView
    private lateinit var uidProfile: TextView
    private lateinit var rankTextProfile: TextView
    private lateinit var scoreTextProfile: TextView
    private lateinit var timeTextProfile: TextView
    private lateinit var matchesTextProfile: TextView
    private lateinit var vanguardTextProfile: TextView
    private lateinit var duelistTextProfile: TextView
    private lateinit var strategistTextProfile: TextView
    private lateinit var overallProfile: TextView
    private lateinit var kdaTextProfile: TextView
    private lateinit var kdTextProfile: TextView
    private lateinit var mvpTextProfile: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var btnSignOut: Button
    private lateinit var btnExitApp: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onResume() {
        super.onResume()
        requireActivity().title = getString(R.string.Profile)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //FIREBASE VARIABLES
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //UID API SETUP
        layoutApiIdSetup = view.findViewById(R.id.layoutApiIdSetup)
        layoutProfileContent = view.findViewById(R.id.layoutProfileContent)
        etApiId = view.findViewById(R.id.etApiId)
        btnSaveApiId = view.findViewById(R.id.btnSaveApiId)

        btnSaveApiId.setOnClickListener { saveApiId()}

        //PROFILE UI
        imageProfile = view.findViewById(R.id.imageProfile)
        nameProfile = view.findViewById(R.id.NameProfile)
        uidProfile = view.findViewById(R.id.UidProfile)
        rankTextProfile = view.findViewById(R.id.RankTextProfile)
        scoreTextProfile = view.findViewById(R.id.ScoreTextProfile)
        timeTextProfile = view.findViewById(R.id.TimeTextProfile)
        matchesTextProfile = view.findViewById(R.id.MatchesTextProfile)
        vanguardTextProfile = view.findViewById(R.id.VanguardTextProfile)
        duelistTextProfile = view.findViewById(R.id.DuelistTextProfile)
        strategistTextProfile = view.findViewById(R.id.StrategistTextProfile)
        overallProfile = view.findViewById(R.id.OverallProfile)
        kdaTextProfile = view.findViewById(R.id.KdaTextProfile)
        kdTextProfile = view.findViewById(R.id.KdTextProfile)
        mvpTextProfile = view.findViewById(R.id.MvpTextProfile)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnSignOut = view.findViewById(R.id.btnSignOut)
        btnExitApp = view.findViewById(R.id.btnExitApp)

        //BOTONES PARA SING OUT Y EDITAR PERFIL
        btnSignOut.setOnClickListener { SingOut() }
        btnEditProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, EditProfile())
                .addToBackStack(null)
                .commit()
        }
        btnExitApp.setOnClickListener{
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

    private fun loadUserAndToggleUi(){
        /*
         * Obtengo el usuario actual desde FirebaseAuth
         * Si es null, significa que no hay sesion activa
         */
        val user = auth.currentUser
        if(user == null){
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()
            return
        }

        //el uid es mi identificador de firebase auth y mi id en la firestore
        val uid = user?.uid ?: ""
        val email = user?.email ?: ""


        //referencia a mi doc en firestore
        val docRef = db.collection("users").document(uid)

        docRef.get()
            .addOnSuccessListener { doc ->
                //si el documento no existe se crea
                //se guarda email y apiid en el documento para poder llamarlos luego desde firestore
                if(!doc.exists()){
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
                if(apiId == null){
                    showApiSetup()
                }else{
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
    private fun saveApiId(){
        val user = auth.currentUser ?: run{
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val apidIdText = etApiId.text.toString()
        if(apidIdText.isBlank()){
            etApiId.error = "Your Rivals UID is required"
            return
        }

        val apiIdNumber = apidIdText.toLongOrNull()
        if(apiIdNumber == null){
            etApiId.error = "Your Rivals UID must be a number"
            return
        }

        val uid = user.uid
        val docRef = db.collection("users").document(uid)

        docRef.update("apiId", apiIdNumber)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "UID saved", Toast.LENGTH_SHORT).show()
                showFullProfile()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error saving UID", Toast.LENGTH_SHORT).show()
            }
    }


    private fun showApiSetup() {
        layoutApiIdSetup.visibility = View.VISIBLE
        layoutProfileContent.visibility = View.GONE
    }

    private fun showFullProfile() {
        layoutApiIdSetup.visibility = View.GONE
        layoutProfileContent.visibility = View.VISIBLE
    }


    //Fetching api

    private fun loadPlayer(playerUid: String) {
        /*
         * Muestro la barra de progreso mientras se consulta
         * la información detallada del jugador
         */
        MarvelAPIInstance.apiService.getPlayerById(playerUid)
            .enqueue(object : Callback<Player> {

                override fun onResponse(
                    call: Call<Player>,
                    response: Response<Player>)
                {

                    if (!response.isSuccessful) {
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

                override fun onFailure(call: Call<Player>, t: Throwable) {
                    Toast.makeText(requireContext(), "API error", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun intToPercent(total: Float, value: Float): Float {
        // Convierto un valor absoluto a porcentaje,
        return if (total > 0) (value * 100) / total else 0f
    }

    private fun bindPlayerData(player: Player) {
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

        nameProfile.text = player.name
        uidProfile.text = "UID: ${player.uid}"
        rankTextProfile.text = player.player.rank.rank
        scoreTextProfile.text = player.player.rank.score
        timeTextProfile.text = player.overall_stats.total_play_time.playtime
        matchesTextProfile.text = player.overall_stats.total_matches.toString()

        vanguardTextProfile.text = "Vanguard: ${"%.2f".format(timeVanguard)}%"
        duelistTextProfile.text = "Duelist: ${"%.2f".format(timeDuelist)}%"
        strategistTextProfile.text = "Strategist: ${"%.2f".format(timeStrategist)}%"

        kdaTextProfile.text = "KDA: ${"%.2f".format(player.overall_stats.overall_kda.kda)}"
        kdTextProfile.text = "KD: ${"%.2f".format(player.overall_stats.overall_kd)}"
        mvpTextProfile.text = "MVP: ${player.overall_stats.total_mvps.mvps}"

        Glide.with(requireContext())
            .load("https://marvelrivalsapi.com/rivals${player.player.icon.player_icon}")
            .placeholder(R.drawable.frame_1)
            .fitCenter()
            .into(imageProfile)
    }


    //Sign out
    private fun SingOut(){
        auth.signOut()
        googleSingInClient.signOut()

        startActivity(Intent(requireContext(), LoginActivity::class.java))
    }

}