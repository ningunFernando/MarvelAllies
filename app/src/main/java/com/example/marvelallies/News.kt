package com.example.marvelallies

import Balance
import MarvelAPI.MarvelAPIInstance
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import models.NewsAdapter
import models.NewsItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

class News : Fragment()
{
     // Este RecyclerView muestra la lista de noticias obtenidas desde la API
    private lateinit var _recyclerNews: RecyclerView

    /*
     * La barra de progreso indica el estado de carga mientras
     * se realiza la petición de red
     */
    private lateinit var _progressBar: ProgressBar

    override fun onConfigurationChanged(newConfig: Configuration)
    {
        super.onConfigurationChanged(newConfig)
        /*
         * Al cambiar la orientación, vuelvo a solicitar las noticias
         * para asegurar que el RecyclerView se renderice correctamente
         */
        //para que no crashee con ayuda de chatgtp
        getNews()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        /*
         * Inflo el layout del fragment y preparo los componentes necesarios
         * para mostrar la lista de noticias
         */
        val view = inflater.inflate(R.layout.fragment_news, container, false)

        _recyclerNews = view.findViewById(R.id.RecyclerNews)

        /*
         * Utilizo un LinearLayoutManager vertical para mostrar
         * las noticias en forma de lista
         */
        _recyclerNews.layoutManager = LinearLayoutManager(requireContext())

        _progressBar = view.findViewById(R.id.progressBar)

         //Inicio la carga de noticias apenas la vista está disponible
        getNews()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        /*
         * Configuro la Toolbar para este fragment
         * No muestro botón de regreso porque funciona como sección principal
         */
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.apply {
            title = getString(R.string.News)
            setDisplayHomeAsUpEnabled(false)
            setHomeAsUpIndicator(null)
        }
    }

    private fun getNews()
    {
        /*
         * Muestro la barra de progreso antes de iniciar la llamada
         * para informar al usuario que los datos se están cargando
         */
        _progressBar.visibility = View.VISIBLE

        /*
         * Realizo la petición a la API para obtener noticias
         * Se especifica la página y el límite de resultados
         */
        MarvelAPIInstance.apiService.getNewsBalances(1, 20)
            .enqueue(object : Callback<Balance>
            {
                override fun onResponse(call: Call<Balance>, response: Response<Balance>)
                {
                    _progressBar.visibility = View.GONE

                    if (!response.isSuccessful)
                    {
                        Log.e("NewsAPI", "Error HTTP: ${response.code()}")
                        return
                    }

                    val news = response.body()
                    if (news == null)
                    {
                        Log.e("NewsAPI", "Response body is null")
                        return
                    }

                    /*
                     * Transformo el modelo recibido desde la API
                     * en una lista de elementos que el adapter puede consumir
                     */
                    val newsItems = news.balances!!.map { player ->
                        NewsItem(
                            title = player.title,
                            description = player.fullContent,
                            imageUrl = player.imagePath
                        )
                    }

                    /*
                     * Inicializo el adapter pasando un callback
                     * que se ejecuta cuando el usuario selecciona una noticia
                     */
                    val adapter = NewsAdapter(newsItems)
                    { new ->
                        onCharacterClicked(new)
                    }

                    //Cambiar el recycler view
                    _recyclerNews.adapter = adapter
                }

                override fun onFailure(call: Call<Balance>, t: Throwable)
                {
                    Log.e("NewsAPI", "Request failed", t)
                    _progressBar.visibility = View.GONE
                }
            })
    }

    private fun onCharacterClicked(new: NewsItem)
    {
        /*
         * Manejo la navegación al fragment de detalle de la noticia,
         * enviando la información necesaria mediante argumentos
         */
        val fragmentTransaction = parentFragmentManager.beginTransaction()

        //Pasar argumentos al segundo fragment
        val detailsFragment = Forum().apply {
            arguments = Bundle().apply {
                //mandar id al otro fragment
                putString("new_image", new.imageUrl)
                putString("new_description", new.description)
                putString("post_id", safeKey(new.title))
            }
        }

        //cambiar de fragment
        fragmentTransaction.replace(R.id.frameLayout, detailsFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun safeKey(input: String): String
    {
        return input.replace(Regex("[.#$\\[\\]/]"), "_")
    }
}
