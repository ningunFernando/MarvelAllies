package MarvelAPI

import Hero
import Player
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarvelAPIService
{
    @GET("v1/heroes")
    fun getAllHeroes(): Call<List<Hero>>

    //LLamar a un heroe en especifico
    @GET("heroes/hero/{query}")
    fun getHeroById(@Path("query") query: String): Call<Hero>

    @GET("v2/player")
    fun gelAllPlayers(): Call<List<Player>>



   // @GET("/api/v2/players/leaderboard")
   // fun getLeaderboard( @Query("page") page: Int = 1, @Query("limit") limit: Int = 25,): Call<Leaderboard>

}
