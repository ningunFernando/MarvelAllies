package com.example.marvelallies

import MarvelAPI.MarvelAPIInstance
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import models.SkinAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import Hero
import android.content.res.Configuration
import android.view.MenuItem
import models.AbilitiesAdapter
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity


class CharacterDetails : Fragment() {

    //Declarar elementos del fragment
    private lateinit var nameLayout: TextView
    private lateinit var bioLayout: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAbilities: RecyclerView
    private lateinit var progressBar: ProgressBar



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //inflate fragment
        val view = inflater.inflate(R.layout.fragment_character_details, container, false)

        // encontrar elementos de los fragments
        nameLayout = view.findViewById(R.id.Name)
        bioLayout = view.findViewById(R.id.Bio)
        recyclerView = view.findViewById(R.id.RecyclerSkin)
        recyclerAbilities = view.findViewById(R.id.RecyclerAbilities)

        progressBar = view.findViewById(R.id.progressBar)

        //scrolleo horizontal
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        //scroll vertical
        recyclerAbilities.layoutManager = LinearLayoutManager(requireContext())

        //detectar presionar boton back

        // Recibir argumentos
        val query = arguments?.getString("character_id")
        val characterName = arguments?.getString("character_name")

        //Modifiicar el nombre de la API en mayusculas
        nameLayout.text = characterName?.uppercase()

        //cargar informacion de acuerdo a la ID del fragment anterior
            loadCharacterDetails(query.toString())


        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_24)
            title = getString(R.string.Characters)
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
        requireActivity().title = getString(R.string.Characters )
    }

    //Cuando cambia de orientacion vuelve a cargar la informacion del personaje
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val query = arguments?.getString("character_id")
        loadCharacterDetails(query.toString())
    }
    private fun loadCharacterDetails(characterId: String) {
        progressBar.visibility = View.VISIBLE

        //obtiene al personaje por la ID
        MarvelAPIInstance.apiService.getHeroById(characterId)
            .enqueue(object : Callback<Hero> {
                override fun onResponse(call: Call<Hero>, response: Response<Hero>) {
                    progressBar.visibility = View.GONE

                    //cargar la bio y las skins
                        response.body()?.let { hero ->
                            bioLayout.text = hero.bio
                            //cargar las skins del personaje
                            recyclerView.adapter = SkinAdapter(hero.costumes)
                            //cargar las habilidades del personaje
                            recyclerAbilities.adapter = AbilitiesAdapter(hero.abilities)


                    }
                }
                //error de conexion
                override fun onFailure(call: Call<Hero>, t: Throwable) {
                    Log.e("API", "Error de conexión: ${t.message}")
                    progressBar.visibility = View.GONE

                }
            })
    }
}


