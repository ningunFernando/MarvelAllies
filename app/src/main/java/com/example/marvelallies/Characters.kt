package com.example.marvelallies

import MarvelAPI.MarvelAPIInstance
import Hero
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import models.CharactersBanner
import models.CharactersItem
import models.CharactersPageAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Characters : Fragment()
{
    //variable del view Pager (carrusel)
    private lateinit var _viewPager: ViewPager2
    private lateinit var _tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        /*
         * Indico que este fragment tiene su propio menú en la Toolbar
         * Esto es necesario para poder mostrar el buscador personalizado
         */
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        //carga el fragmento characters y encuentra el viewpager
        val view = inflater.inflate(R.layout.fragment_characters, container, false)

        _viewPager = view.findViewById(R.id.viewPager)
        _tabLayout = view.findViewById(R.id.into_tab_layout)
        /*
         * Solicito la lista completa de personajes al inicializar la vista
         * Esta información será transformada en páginas dentro del ViewPager
         */
        loadCharactersFromAPI()
        return view
    }

    //Cuando cambia de orientation vuelve a cargar los datos de la API para mostrar los items adecuados
    override fun onConfigurationChanged(newConfig: Configuration)
    {
        super.onConfigurationChanged(newConfig)
        /*
         * Verifico que el fragment siga asociado a la actividad antes de
         * realizar cualquier operación para evitar errores de ciclo de vida
         */
        if (isAdded)
        {
            loadCharactersFromAPI()
        }
    }

    //Cambiar el título del Toolbar al entrar al fragment
    override fun onResume()
    {
        super.onResume()
        /*
         * Configuro la Toolbar para este fragment eliminando el botón de regreso,
         * ya que este fragment funciona como pantalla principal de navegación
         */
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setHomeAsUpIndicator(null)
            title = getString(R.string.Characters)
        }
    }

    // menu de busqueda del top bar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        inflater.inflate(R.menu.top_bar_search_characters, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = getString(R.string.Name)
        /*
         * Configuro el comportamiento del buscador
         * Solo reacciono cuando el usuario confirma la búsqueda,
         * evitando llamadas innecesarias mientras escribe
         */
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean
            {
                //cuando el usuario presiona buscar
                if (!query.isNullOrBlank())
                {
                    loadCharacterDetails(query)
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean
            {
                return false
            }
        })
    }

    private fun loadCharactersFromAPI()
    {
        /*
         * Realizo una llamada asíncrona para obtener todos los héroes
         * El resultado se procesa en el callback para no bloquear la UI
         */
        MarvelAPIInstance.apiService.getAllHeroes()
            .enqueue(object : Callback<List<Hero>>
            {
                override fun onResponse(
                    call: Call<List<Hero>>,
                    response: Response<List<Hero>>
                )
                {
                    if (!response.isSuccessful)
                    {
                        //mensaje error
                        Log.e("API", "Error: ${response.code()}")
                        return
                    }
                    //obtener la lista de personajes
                    val apiCharacters = response.body() ?: emptyList()
                    /*
                     * Transformo la lista plana de personajes en páginas
                     * Cada página representa un banner dentro del ViewPager
                     */
                    val pages = groupIntoPages(apiCharacters)

                    // Pasar un callback al adapter
                    _viewPager.adapter = CharactersPageAdapter(pages)
                    { character ->
                        // Esta función se ejecuta cuando se hace clic en un personaje
                        onCharacterClicked(character)
                    }
                    /*
                     * Vinculo el TabLayout con el ViewPager sin títulos,
                     * utilizando los tabs únicamente como indicadores de página
                     */
                    TabLayoutMediator(_tabLayout, _viewPager) { _, _ -> }.attach()
                }

                override fun onFailure(call: Call<List<Hero>>, t: Throwable)
                {
                    Log.e("API", "Error: ${t.message}")
                }
            })
    }

    private fun onCharacterClicked(character: CharactersItem)
    {
        /*
         * Manejo la navegación manualmente usando el FragmentManager
         * para poder controlar el back stack y pasar argumentos
         */
        val fragmentTransaction = parentFragmentManager.beginTransaction()

        //Pasar argumentos al segundo fragment
        val detailsFragment = CharacterDetails().apply {
            arguments = Bundle().apply {
                //mandar id y nombre al fragment details
                putString("character_id", character.query)
                putString("character_name", character.name)
            }
        }
        //cambiar de fragment
        fragmentTransaction.replace(R.id.frameLayout, detailsFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    //Con ayuda de ChatGTP agarre los 9 que necesitaba
    private fun groupIntoPages(apiCharacters: List<Hero>): List<CharactersBanner>
    {
        //para que no crashee
        if (!isAdded || context == null)
        {
            return emptyList()
        }
        /*
         * Defino dinámicamente cuántos elementos tendrá cada página
         * según la orientación del dispositivo para optimizar el uso del espacio
         */
        val numberItems: Int =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                //Divide a los personajes en lista de 9
                9
            } else
            {
                //Divide a los personajes en lista de 6
                6
            }

        val chunked = apiCharacters.chunked(numberItems)
        /*
         * Cada grupo de personajes se transforma en un banner,
         * adaptando el modelo de la API a un modelo de UI.
         */
        return chunked.map { chunk ->
            CharactersBanner(
                characters = chunk.map
                {
                    //Cada item de la page obtiene el nombre, id y descripcion
                    CharactersItem(
                        query = it.id,
                        name = it.name,
                        imageUrl = it.imageUrl
                    )
                }
            )
        }
    }

    private fun groupOnePage(apiCharacter: Hero): List<CharactersBanner>
    {
        //para que no crashee
        if (!isAdded || context == null)
        {
            return emptyList()
        }
        /*
         * Este método se utiliza para mostrar un solo personaje,
         * principalmente como resultado de una búsqueda
         */

        //Declarar el character Item
        val characterItem = CharactersItem(
            query = apiCharacter.id,
            name = apiCharacter.name,
            imageUrl = apiCharacter.imageUrl
        )

        //Pasar al banner con la lista de personajes (solo uno)
        val charactersBanner = CharactersBanner(
            characters = listOf(characterItem)
        )

        //Devolver la pagina con un solo heroe
        return listOf(charactersBanner)
    }

    private fun loadCharacterDetails(characterId: String)
    {
        /*
         * Se reutiliza el endpoint de búsqueda por ID para filtrar
         * y mostrar únicamente el personaje solicitado.
         */
        MarvelAPIInstance.apiService.getHeroById(characterId)
            .enqueue(object : Callback<Hero>
            {
                override fun onResponse(call: Call<Hero>, response: Response<Hero>)
                {
                    if (!response.isSuccessful)
                    {
                        //mensaje error
                        Log.e("API", "Error: ${response.code()}")
                        loadCharactersFromAPI()
                        return
                    }
                    //el character sera igual a la respuesta de la API
                    val character = response.body()

                    if (character != null)
                    {
                        // Usar la versión singular
                        val pages = groupOnePage(character)
                        /*
                         * Reemplazo el adapter del ViewPager para mostrar
                         * únicamente el resultado de la búsqueda
                         */
                        _viewPager.adapter = CharactersPageAdapter(pages)
                        { characterItem ->
                            onCharacterClicked(characterItem)
                        }
                    }
                }

                //error de conexion
                override fun onFailure(call: Call<Hero>, t: Throwable)
                {
                    Log.e("API", "Error de conexión: ${t.message}")
                }
            })
    }
}
