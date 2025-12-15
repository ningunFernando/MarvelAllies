package MarvelAPI

import Hero
import Player
import retrofit2.Call
import retrofit2.http.GET

interface MarvelAPIService
{
    @GET("v1/heroes")
    fun getAllHeroes(): Call<List<Hero>>

    @GET("v2/player")
    fun gelAllPlayers(): Call<List<Player>>
}
