package MarvelAPI

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object MarvelAPIInstance {
    private const val BASE_URL = "https://marvelrivalsapi.com/api/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor())
        .build()

    val apiService: MarvelAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MarvelAPIService::class.java)
    }
}
