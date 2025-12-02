package MarvelAPI

import MarvelCharacter
import retrofit2.Call
import retrofit2.http.GET

interface MarvelAPIService {

    @GET("v1/heroes")
    fun getCharacters(): Call<List<MarvelCharacter>>
}
