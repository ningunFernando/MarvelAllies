package com.example.marvelallies

import MarvelAPI.MarvelAPIInstance
import MarvelCharacter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import models.CharactersBanner
import models.CharactersItem
import models.CharactersPageAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Characters : Fragment() {

    //variable del view Pager (carrusel)
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //carga el fragmento characters y encuentra el viewpager
        val view = inflater.inflate(R.layout.fragment_characters, container, false)
        viewPager = view.findViewById(R.id.viewPager)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //cargar los personajes de la api
        loadCharactersFromAPI()
    }

    private fun loadCharactersFromAPI() {
        //llamar a la API
        MarvelAPIInstance.apiService.getCharacters().enqueue(object : Callback<List<MarvelCharacter>> {

                override fun onResponse(
                    call: Call<List<MarvelCharacter>>,
                    response: Response<List<MarvelCharacter>>
                ) {
                    if (!response.isSuccessful) {
                        //mensaje error
                        Log.e("API", "Error: ${response.code()}")
                        return
                    }

                    //obtener la liosta de personajes
                    val apiCharacters = response.body() ?: emptyList()

                    //los divide en paginas
                    val pages = groupIntoPages(apiCharacters)

                    // Pasar un callback al adapter
                    viewPager.adapter = CharactersPageAdapter(pages) { character ->
                        // Esta función se ejecuta cuando se hace clic en un personaje
                        onCharacterClicked(character)
                    }

                }

                override fun onFailure(call: Call<List<MarvelCharacter>>, t: Throwable) {
                    Log.e("API", "Error: ${t.message}")
                }
            })
    }
    private fun onCharacterClicked(character: CharactersItem) {
        // Aquí puedes navegar al fragmento de detalles
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        val detailsFragment = CharacterDetails().apply {
            arguments = Bundle().apply {
                putString("character_name", character.name)
                putString("character_image", character.imageUrl)
            }
        }
        fragmentTransaction.replace(R.id.frameLayout, detailsFragment)
        fragmentTransaction.addToBackStack(null) // Para volver atrás
        fragmentTransaction.commit()
    }

    private fun groupIntoPages(apiCharacters: List<MarvelCharacter>): List<CharactersBanner> {
        //Divide a los personajes en lista de 9
        val chunked = apiCharacters.chunked(9)

        //cada grupo de 9 personajes se convierte en una pagina
        return chunked.map { chunk ->
            CharactersBanner(
                characters = chunk.map {
                    //Cada item de la page obtiene el nombre y descripcion
                    CharactersItem(
                        name = it.name,
                        imageUrl = it.imageUrl


                    )
                }
            )
        }
    }

    fun ReplaceFragment(character: CharactersItem) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        val detailsFragment = CharacterDetails().apply {
            arguments = Bundle().apply {
                putString("character_name", character.name)
                putString("character_image", character.imageUrl)
            }
        }
        fragmentTransaction.replace(R.id.frameLayout, detailsFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


}

