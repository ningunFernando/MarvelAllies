package com.example.marvelallies

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.analytics.FirebaseAnalytics



class MainActivity : AppCompatActivity()
{
    /*
     * La barra de navegación inferior se encarga de cambiar
     * entre los fragmentos principales de la aplicación
     */
    private lateinit var _bottomNavBarView: BottomNavigationView
    /*
     * La Toolbar funciona como barra superior global y se reutiliza
     * por los fragments para mostrar títulos y acciones
     */
    private lateinit var _topBarview: Toolbar

    /*
    * inicializacion de la variable para llamar a las analiticas de firebase
    * para registrar eventos personalizados y análisis de datos y comportamiento
    * del usuario
    */
    private lateinit var analytics: FirebaseAnalytics

    /*
    *guarda el ultimo item seleccionado en la barra de navegacion
    * para evitar registrar multiples eventos si el usuario toca mas de una seccion
    */
    private var lastNavItemId: Int? = null



    override fun onStart()
    {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser == null)
        {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //inicializacion de una instancia de firebase para nuestras analiticas
        analytics = FirebaseAnalytics.getInstance(this)

        /*
         * Inicializo la barra de navegación inferior y delego la lógica
         * de selección a un método separado para mejorar la legibilidad
         */


        _bottomNavBarView = findViewById(R.id.bottomNavigationView)
        _bottomNavBarView.setOnItemSelectedListener { item ->
            handleNavigationItemSelected(item.itemId)
        }
        /*
         * Configuro la Toolbar como ActionBar para que pueda
         * integrarse con los fragments y los menús
         */
        _topBarview = findViewById(R.id.TopbarView)
        setSupportActionBar(_topBarview)
        /*
         * Cargo el fragment inicial al iniciar la aplicación
         * Este fragment actúa como pantalla principal por defecto
         */

        //En caso de que sea primera vez cargando la activity, el fragment base sera las news
        if (savedInstanceState == null)
        {
            _bottomNavBarView.selectedItemId = R.id.news
        }

    }

    private fun LoadFragment(fragment: Fragment)
    {
        /*
         * Reemplazo el fragment actual sin agregarlo al back stack,
         * ya que la navegación principal se controla desde el BottomNavigationView
         */
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }

    private fun handleNavigationItemSelected(itemId: Int): Boolean
    {
        /*
        * Se registra el evento de navegacion solo si el usuario cambio
        * de seccion. Esto evita registrar eventos innecesarios registrados en
        * las analiticas
        */
        if (lastNavItemId != itemId)
        {
            logNavSelectionEvent(itemId)
            lastNavItemId = itemId
        }
        /*
         * Centralizo la lógica de navegación para evitar duplicación
         * y facilitar el mantenimiento cuando se agreguen más secciones
         */
        return when (itemId)
        {
            R.id.news -> {
                LoadFragment(News())
                true
            }
            R.id.characters -> {
                LoadFragment(Characters())
                true
            }
            R.id.players -> {
                LoadFragment(Players())
                true
            }
            R.id.profile -> {
                LoadFragment(Profile())
                true
            }
            else -> false
        }
    }

    //Crear las opciones para el toolbar (top bar)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        /*
         * Inflo el menú global de la Toolbar.
         * Estas opciones permanecen disponibles sin importar el fragment activo
         */
        menuInflater.inflate(R.menu.top_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        /*
         * Manejo el cambio de tema de la aplicación
         * AppCompatDelegate se encarga de aplicar el modo
         * de forma global y persistente
         */
        return when (item.itemId)
        {
            R.id.lightMode -> {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
                true
            }
            R.id.darkMode -> {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logNavSelectionEvent(itemId: Int){
        val screen = when(itemId){
            R.id.news -> "News"
            R.id.characters -> "Characters"
            R.id.players -> "Players"
            R.id.profile -> "Profile"
            else -> "Unknown"
        }

        val bundle = Bundle().apply {
            putString("screen", screen)
            putString("source", "bottom_nav")
        }

        analytics.logEvent("nav_selection", bundle)
    }
}
