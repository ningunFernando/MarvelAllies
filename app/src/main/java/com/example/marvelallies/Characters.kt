package com.example.marvelallies

import MarvelAPI.MarvelAPIInstance
import MarvelCharacter
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
class Characters : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        MarvelAPIInstance.apiService.getCharacters()
            .enqueue(object : Callback<List<MarvelCharacter>> { //Obtenemos la estructura del Marvel Characters
                override fun onResponse( call: Call<List<MarvelCharacter>>, response: Response<List<MarvelCharacter>> ) {
                    if (response.isSuccessful) {
                        val characters = response.body()
                        characters?.forEach { character -> //Vamos 1 por 1, para poder obtener su informaicon
                            Log.d("Character", "Name: ${character.name}, Role: ${character.role}") //De momento solo de debugea y sacamos el nombre y rol
                        }
                    } else {
                        Log.e("ApiError", "Response: ${response.code()} - ${response.message()}") // Debug en caso que falle algo
                    }
                }

                override fun onFailure(call: Call<List<MarvelCharacter>>, t: Throwable) {
                    Log.e("ApiError", t.message ?: "Unknown error")
                }
            })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_characters, container, false)
    }


}