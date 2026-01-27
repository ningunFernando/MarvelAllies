package MarvelAPI

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//Para crear la instancia y pueda ser llamada donde sea
object MarvelAPIInstance
{
    private const val _BASE_URL = "https://marvelrivalsapi.com/" //link de la API

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .callTimeout(120, TimeUnit.SECONDS)
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
