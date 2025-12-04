package MarvelAPI

import okhttp3.Interceptor
import okhttp3.Response

//interceptar, modificar o inspeccionar todas las peticiones y respuestas HTTP antes de que se envíen o reciban
//Este unicamente es un interceptor que ayuda a poder la key
//unicamente exite la private key para esta api, no la publica


//Este fue hecho con sugerencia y ayuda de Chat GPT
class ApiKeyInterceptor : Interceptor
{
    //https://marvelrivalsapi.com/dashboard/settings
    private val _privateKey = "16179dd8cec63563b9b26996bd9b2218b90341914ecfda3dc2c64ae7505bb30b"

    override fun intercept(chain: Interceptor.Chain): Response
    {
        val request = chain.request().newBuilder()
            .addHeader("x-api-key", _privateKey) //Asi lo pide la API
            .build()

        return chain.proceed(request)
    }
}
