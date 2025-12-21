package MarvelAPI

import okhttp3.Interceptor
import okhttp3.Response

//interceptar, modificar o inspeccionar todas las peticiones y respuestas HTTP antes de que se envíen o reciban
//Este unicamente es un interceptor que ayuda a poder la key
//unicamente exite la private key para esta api, no la publica


//Este fue hecho con sugerencia y ayuda de Chat GPT
class ApiKeyInterceptor : Interceptor
{
    //https://marvelrivalsapi.com/dashboard/settings - el link de donde sacamos la Key
    private val _privateKey = "c639a6dd1dec673a54538edc33d11f8e24b4d5a546a5829fb2f842a3d47eb609"

    override fun intercept(chain: Interceptor.Chain): Response
    {
        val request = chain.request().newBuilder()
            .addHeader("x-api-key", _privateKey) //Asi lo pide la API
            .build()

        return chain.proceed(request)
    }
}
