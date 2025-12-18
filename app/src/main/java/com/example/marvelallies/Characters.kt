package com.example.marvelallies
import MarvelAPI.MarvelAPIInstance
import Hero
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import models.CharactersBanner
import models.CharactersItem
import models.CharactersPageAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Characters : Fragment() {

    //variable del view Pager (carrusel)
    private lateinit var viewPager: ViewPager2
    private lateinit var searchButton: ImageButton
    private lateinit var searchInputText: EditText
    private lateinit var tabLayout: TabLayout


    private lateinit var characterLayout: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //carga el fragmento characters y encuentra el viewpager
        val view = inflater.inflate(R.layout.fragment_characters, container, false)
        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.into_tab_layout)


        //Encontrar el boton de search y el input Field
        searchButton = view.findViewById(R.id.Search)
        searchInputText = view.findViewById(R.id.SearchLayout)

        searchButton.setOnClickListener{
            ShowLayout()
        }

        //Para el layout listener
        characterLayout = view.findViewById(R.id.CharactersLayout)
        characterLayout.setOnClickListener{
            HideLayout()
        }

        //cargar los personajes de la api
        loadCharactersFromAPI()
        return view
    }

    //Cuando cambia de orientation vuelve a cargar los datos de la API para mostrar los items adecuados
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //para que no crashee con ayuda de chatgtp
        if (isAdded) {
            loadCharactersFromAPI()
        }
    }

    private fun loadCharactersFromAPI() {
        //llamar a la API
        MarvelAPIInstance.apiService.getAllHeroes().enqueue(object : Callback<List<Hero>> {

                override fun onResponse(call: Call<List<Hero>>, response: Response<List<Hero>>) {
                    if (!response.isSuccessful) {
                        //mensaje error
                        Log.e("API", "Error: ${response.code()}")
                        return
                    }

                    //obtener la liosta de personajes
                    val apiCharacters = response.body() ?: emptyList()
                    //Log.d("hola", "${apiCharacters}")


                    //los divide en paginas
                    val pages = groupIntoPages(apiCharacters)

                    // Pasar un callback al adapter
                    viewPager.adapter = CharactersPageAdapter(pages) { character ->
                        // Esta función se ejecuta cuando se hace clic en un personaje
                        onCharacterClicked(character)
                    }

                    //unir la Tab layout con el adapter
                    TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    }.attach()

                }

                override fun onFailure(call: Call<List<Hero>>, t: Throwable) {
                    Log.e("API", "Error: ${t.message}")
                }
            })
    }
    private fun onCharacterClicked(character: CharactersItem) {
        // Aquí puedes navegar al fragmento de detalles
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
        fragmentTransaction.commit()
    }

    //Con ayuda de ChatGTP agarre los 9 que necesitaba
    private fun groupIntoPages(apiCharacters: List<Hero>): List<CharactersBanner> {
        //para que no crashee
        if (!isAdded || context == null) {
            return emptyList()
        }
        //Dependiendo de la orientacion del dispositivo acomodar el grid en 3X3 0 2x3
        var numberItems: Int = if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //Divide a los personajes en lista de 9
            9
        }else{
            //Divide a los personajes en lista de 6
            6
        }
        val chunked = apiCharacters.chunked(numberItems)


        //cada grupo de 9 personajes se convierte en una pagina
        return chunked.map { chunk ->
            CharactersBanner(
                characters = chunk.map {
                    //Cada item de la page obtiene el nombre, id y descripcion
                    CharactersItem(
                        query = it.id,
                        name = it.name,
                        imageUrl = it.imageUrl,

                        )
                }
            )
        }
    }

    private fun groupOnePage(apiCharacter: Hero): List<CharactersBanner>{
        //para que no crashee
        if (!isAdded || context == null) {
            return emptyList()
        }
        //Declarar el character Item
        val characterItem: CharactersItem = CharactersItem(
            query = apiCharacter.id,
            name = apiCharacter.name,
            imageUrl = apiCharacter.imageUrl,
        )

        //Pasar al banner con la lista de personajes (solo uno)
        val charactersBanner: CharactersBanner = CharactersBanner(
            characters = listOf(characterItem)
        )

        //Devolver la pagina con un solo heroe
        return listOf(charactersBanner)
    }

    private fun loadCharacterDetails(characterId: String) {
        //obtiene al personaje por el nombre
        MarvelAPIInstance.apiService.getHeroById(characterId)
            .enqueue(object : Callback<Hero> {
                override fun onResponse(call: Call<Hero>, response: Response<Hero>) {
                    if (!response.isSuccessful) {
                        //mensaje error
                        Log.e("API", "Error: ${response.code()}")
                        loadCharactersFromAPI()
                        return
                    }

                    //el character sera igual ala respuesta de la API
                    val character = response.body()

                    if (character != null) {
                        // Usar la versión singular
                        val pages = groupOnePage(character)

                        viewPager.adapter = CharactersPageAdapter(pages) { characterItem ->
                            onCharacterClicked(characterItem)
                        }
                    }
                }

                //error de conexion
                override fun onFailure(call: Call<Hero>, t: Throwable) {
                    Log.e("API", "Error de conexión: ${t.message}")
                }
            })
    }


    private fun HideLayout(){
        searchInputText.visibility = View.INVISIBLE


    }
    private fun ShowLayout(){
        if(searchInputText.visibility == View.INVISIBLE) {
            searchInputText.visibility = View.VISIBLE
        }else{
            //println(searchInputText.text)
            loadCharacterDetails(searchInputText.text.toString())
        }

    }



}



