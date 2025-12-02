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
import java.math.BigInteger
import java.security.MessageDigest

class Characters : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        MarvelAPIInstance.apiService.getCharacters()
            .enqueue(object : Callback<List<MarvelCharacter>> {
                override fun onResponse(
                    call: Call<List<MarvelCharacter>>,
                    response: Response<List<MarvelCharacter>>
                ) {
                    if (response.isSuccessful) {
                        val characters = response.body()
                        characters?.forEach { character ->
                            Log.d("Character", "Name: ${character.name}, Role: ${character.role}")
                        }
                    } else {
                        Log.e("ApiError", "Response: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<MarvelCharacter>>, t: Throwable) {
                    Log.e("ApiError", t.message ?: "Unknown error")
                }
            })

    }

    private fun md5(input : String): String{
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_characters, container, false)
    }


}