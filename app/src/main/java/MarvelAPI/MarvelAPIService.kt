package MarvelAPI

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MarvelAPIService {
    @Get("v1/public/characters")
    fun getCharacters
}