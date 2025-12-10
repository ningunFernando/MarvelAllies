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
import models.CharactersPagerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Characters : Fragment() {

    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_characters, container, false)
        viewPager = view.findViewById(R.id.viewPager)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCharactersFromAPI()
    }

    private fun loadCharactersFromAPI() {
        MarvelAPIInstance.apiService.getCharacters()
            .enqueue(object : Callback<List<MarvelCharacter>> {

                override fun onResponse(
                    call: Call<List<MarvelCharacter>>,
                    response: Response<List<MarvelCharacter>>
                ) {
                    if (!response.isSuccessful) {
                        Log.e("API", "Error: ${response.code()}")
                        return
                    }

                    val apiCharacters = response.body() ?: emptyList()

                    val pages = groupIntoPages(apiCharacters)

                    viewPager.adapter = CharactersPagerAdapter(pages)
                }

                override fun onFailure(call: Call<List<MarvelCharacter>>, t: Throwable) {
                    Log.e("API", "Error: ${t.message}")
                }
            })
    }

    private fun groupIntoPages(apiCharacters: List<MarvelCharacter>): List<CharactersBanner> {
        val chunked = apiCharacters.chunked(9)

        return chunked.map { chunk ->
            CharactersBanner(
                characters = chunk.map {
                    CharactersItem(
                        name = it.name,
                        imageUrl = it.imageUrl
                    )
                }
            )
        }
    }
}

