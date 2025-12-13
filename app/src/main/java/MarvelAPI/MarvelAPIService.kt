package MarvelAPI

import Hero
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MarvelAPIService
{
    /*
    De momento esto unicamente cuenta con los personajes, segun la documentacion de la pagina podriamos
    ir creando mas funciones para obtener los demas datos, como stats de un personaje, buscar un personaje,
    hero ladeaboard, player stats, etc.
     */

    @GET("heroes")
    fun getAllHeroes(): Call<List<Hero>>

    //LLamar a un heroe en especifico
    @GET("heroes/hero/{query}")
    fun getHeroById(@Path("query") query: String): Call<Hero>
}
