package MarvelAPI

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request().newBuilder()
            .addHeader("x-api-key", "16179dd8cec63563b9b26996bd9b2218b90341914ecfda3dc2c64ae7505bb30b")
            .build()

        return chain.proceed(request)
    }
}
