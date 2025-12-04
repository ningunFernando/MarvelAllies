package MarvelAPI

import MarvelCharacter
import retrofit2.Call
import retrofit2.http.GET

interface MarvelAPIService
{
    /*
    De momento esto unicamente cuenta con los personajes, segun la documentacion de la pagina podriamos
    ir creando mas funciones para obtener los demas datos, como stats de un personaje, buscar un personaje,
    hero ladeaboard, player stats, etc.
     */

    @GET("v1/heroes")
    fun getCharacters(): Call<List<MarvelCharacter>>
}
