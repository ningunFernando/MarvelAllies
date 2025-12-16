package MarvelAPI

import Hero
import Player
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MarvelAPIService
{
    @GET("v1/heroes")
    fun getAllHeroes(): Call<List<Hero>>

    @GET("v2/player")
    fun gelAllPlayers(): Call<List<Player>>

    //LLamar a un heroe en especifico
    @GET("heroes/hero/{query}")
    fun getHeroById(@Path("query") query: String): Call<Hero>

}
