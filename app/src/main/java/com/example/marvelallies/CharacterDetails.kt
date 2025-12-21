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

    /*
     * Aquí declaro los elementos visuales que voy a utilizar para mostrar
     * la información detallada del personaje seleccionado
     * Se inicializan más adelante cuando la vista ya está inflada
     */
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
        val view = inflater.inflate(R.layout.fragment_character_details, container, false)


         // Inicializo los componentes principales que mostrarán los datos del personaje

        nameLayout = view.findViewById(R.id.Name)
        bioLayout = view.findViewById(R.id.Bio)
        recyclerView = view.findViewById(R.id.RecyclerSkin)
        recyclerAbilities = view.findViewById(R.id.RecyclerAbilities)
        progressBar = view.findViewById(R.id.progressBar)

        /*
         * Configuro los RecyclerView según el tipo de información:
         * - Las skins se muestran en forma horizontal
         * - Las habilidades se muestran en forma vertical
         * Esto mejora la experiencia visual y la jerarquía de información
         */
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        recyclerAbilities.layoutManager =
            LinearLayoutManager(requireContext())

        /*
         * Obtengo los datos enviados desde el fragment anterior
         * Estos argumentos determinan qué personaje se va a consultar en la API
         */
        val query = arguments?.getString("character_id")
        val characterName = arguments?.getString("character_name")
        nameLayout.text = characterName?.uppercase()

        /*
         * Cargo la información completa del personaje utilizando su ID
         * Esta llamada inicia el consumo del servicio y la actualización de la UI
         */
        loadCharacterDetails(query.toString())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
         * Configuro la barra superior para mostrar el botón de regreso
         * Esto permite volver al fragment anterior sin cerrar la actividad
         */
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_24)
            title = getString(R.string.Characters)
        }

        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*
         * Detecto la acción del botón de regreso en la barra superior
         * y retiro este fragment del back stack para volver al anterior
         */
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
        /*
         * Aseguro que el título de la actividad sea consistente
         * cuando el fragment vuelve a primer plano
         */
        requireActivity().title = getString(R.string.Characters)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        /*
         * Cuando ocurre un cambio de orientación, vuelvo a solicitar
         * la información del personaje para evitar inconsistencias visuales
         * y asegurar que los datos se muestren correctamente
         */
        val query = arguments?.getString("character_id")
        loadCharacterDetails(query.toString())
    }

    private fun loadCharacterDetails(characterId: String) {

        /*
         * Muestro la barra de progreso mientras se realiza la petición
         * para indicar que los datos están siendo cargados
         */
        progressBar.visibility = View.VISIBLE

        /*
         * Realizo la llamada asíncrona a la API utilizando Retrofit
         * El resultado se maneja a través de callbacks para no bloquear la UI
         */
        MarvelAPIInstance.apiService.getHeroById(characterId)
            .enqueue(object : Callback<Hero> {

                override fun onResponse(call: Call<Hero>, response: Response<Hero>) {
                    progressBar.visibility = View.GONE

                    /*
                     * Si la respuesta es válida, actualizo la información visual
                     * del fragment con los datos del personaje recibido
                     */
                    response.body()?.let { hero ->
                        bioLayout.text = hero.bio

                        /*
                         * Inicializo los adapters con la información obtenida
                         * Cada RecyclerView se encarga de representar un tipo
                         * distinto de información del personaje
                         */
                        recyclerView.adapter = SkinAdapter(hero.costumes)
                        recyclerAbilities.adapter = AbilitiesAdapter(hero.abilities)
                    }
                }

                override fun onFailure(call: Call<Hero>, t: Throwable) {
                    /*
                     * Manejo el error de conexión registrándolo en el log
                     * y oculto la barra de progreso para evitar bloqueos visuales
                     */
                    Log.e("API", "Error de conexión: ${t.message}")
                    progressBar.visibility = View.GONE
                }
            })
    }
}
