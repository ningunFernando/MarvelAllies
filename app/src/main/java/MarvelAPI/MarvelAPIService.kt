package MarvelAPI

import Hero
import Player
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MarvelAPIService
{
    //Heroes
    @GET("v1/heroes")
    fun getAllHeroes(): Call<List<Hero>>

    //LLamar a un heroe en especifico
    @GET("heroes/hero/{query}")
    fun getHeroById(@Path("query") query: String):Call<Hero>

    //Jugadores
    @GET("v2/player")
    fun gelAllPlayers(): Call<List<Player>>

    @GET("v1/find-player/{username}")
    fun getPlayerByName(@Path("username") username: String):Call<Player>
}
