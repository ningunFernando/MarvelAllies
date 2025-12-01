package MarvelAPI

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MarvelAPIService {
    @GET("/api/v1/heroes")
    fun getCharacters(
        @Query("apikey") apikey: String,
        @Query("ts") timestamp: String,
        @Query("hash") hash: String,
        @Query("limit") limit: Int
    ): Call <MarvelResponse>
}