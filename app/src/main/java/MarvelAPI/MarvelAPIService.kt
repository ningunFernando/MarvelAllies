package MarvelAPI

import Balance
import Hero
import Player
import Leaderboard
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarvelAPIService
{
    //Heroes
    @GET("api/v1/heroes")
    fun getAllHeroes(): Call<List<Hero>>

    //LLamar a un heroe en especifico
    @GET("v1/heroes/hero/{query}")
    fun getHeroById(
        @Path("query") query: String
    ): Call<Hero>


    //Players
    //Encontrar un player en especifico
    @GET("/api/v2/player/{query}")
    fun getPlayerById(
        @Path("query") query: String
    ): Call<Player>

    //Obtener la leaderboard decidiendo que pagina y el limite de players
    @GET("/api/v2/players/leaderboard")
    fun getLeaderboard(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<Leaderboard>

    //News
    @GET("api/v1/balances")
    fun getNewsBalances(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<Balance>
}
