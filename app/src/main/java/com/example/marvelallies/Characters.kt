package com.example.marvelallies

import Hero
import MarvelAPI.MarvelAPIInstance
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//De momento la API esta aqui, lo correcto seria crear la instancia en el main, para cuando carguen
//y hacer el fetch cada que se necesite y donde se necesite
class Characters : Fragment()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        MarvelAPIInstance.apiService.getAllHeroes()
            .enqueue(object : Callback<List<Hero>> {
                override fun onResponse(call: Call<List<Hero>>, response: Response<List<Hero>>) {
                    if (response.isSuccessful) {
                        val heroes = response.body() ?: emptyList()

                        heroes.forEach { hero ->
                            Log.d("Hero", "ID: ${hero.id}")
                            Log.d("Hero", "Name: ${hero.name}")
                            Log.d("Hero", "Real Name: ${hero.real_name}")
                            Log.d("Hero", "Image URL: ${hero.imageUrl}")
                            Log.d("Hero", "Role: ${hero.role}")
                            Log.d("Hero", "Attack Type: ${hero.attack_type}")
                            Log.d("Hero", "Team: ${hero.team?.joinToString(", ") ?: "No team"}")
                            Log.d("Hero", "Difficulty: ${hero.difficulty}")
                            Log.d("Hero", "Bio: ${hero.bio}")

                            hero.abilities.forEach { ability ->
                                Log.d("Ability", "Name: ${ability.ability_name}")
                                Log.d("Ability", "Description: ${ability.description}")
                            }
                        }

                    } else {
                        Log.e("ApiError", "Response: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<Hero>>, t: Throwable) {
                    Log.e("ApiError", t.message ?: "Unknown error")
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_characters, container, false)
    }


}