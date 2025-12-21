package MarvelAPI

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Para crear la instancia y pueda ser llamada donde sea
object MarvelAPIInstance
{
    private const val _BASE_URL = "https://marvelrivalsapi.com/" //link de la API

    private val client = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor())
        .build()


    val apiService: MarvelAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MarvelAPIService::class.java)
    }
}
