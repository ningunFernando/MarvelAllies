package com.example.marvelallies

import MarvelAPI.MarvelAPIInstance
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import models.CommentaryAdapter
import models.CommentaryItem


class Forum : Fragment() {
    /*
     * Estos elementos se utilizan para mostrar el contenido
     * de una noticia seleccionada previamente
     */
    private lateinit var imageNew: ImageView
    private lateinit var newText: TextView

    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
         * No realizo lógica adicional en este método,
         * ya que el fragment depende principalmente de los argumentos recibidos
         */
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /*
         * Inflo el layout correspondiente al detalle de la noticia
         * y trabajo directamente sobre esta vista
         */
        val view = inflater.inflate(R.layout.fragment_forum, container, false)

        //Tomar elementos pasados
        val newImageUrl = arguments?.getString("new_image")
        val newDescription = arguments?.getString("new_description")

        /*
         * Inicializo los componentes visuales que mostrarán
         * la información textual y gráfica de la noticia
         */
        newText = view.findViewById(R.id.NewsText)
        imageNew = view.findViewById(R.id.NewsImage)

        /*
         * Asigno directamente el texto recibido
         * Se asume que el contenido ya viene procesado desde el fragment anterior
         */
        newText.text = newDescription

        /*
         * Utilizo Glide para manejar la carga de imágenes remotas
         * Esto permite una carga eficiente, manejo de caché
         * y control de errores visuales
         */
        Glide.with(requireContext())
            //Si carga toma la imagen de este URL
            .load("https://marvelrivalsapi.com/rivals" + newImageUrl)
            //Si no pone una de placeholder
            .placeholder(R.drawable.frame_1)
            //Acomoda la imagen en el centro del item
            .fitCenter()
            //En la imagen del item
            .into(imageNew)

        //Comentarios
        recyclerView = view.findViewById(R.id.RecyclerCommentary)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        LoadCommentaries()

        recyclerView.adapter = CommentaryAdapter()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
         * Configuro la Toolbar para mostrar el botón de regreso,
         * ya que este fragment se comporta como una pantalla secundaria
         */
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_24)
            title = getString(R.string.Forum)
        }

        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*
         * Intercepto el evento del botón de regreso en la Toolbar
         * para volver al fragment anterior usando el back stack
         */
        return when (item.itemId) {
            android.R.id.home -> {
                parentFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun LoadCommentaries(){
        MarvelAPIInstance.apiService.getPlayerById("224829686")
            .enqueue(object : Callback<Player> {

                override fun onResponse(call: Call<Player>, response: Response<Player>) {

                    if (!response.isSuccessful) {
                        Log.e("API", "Error: ${response.code()}")
                        return
                    }

                    /*
                     * Si la respuesta es válida, enlazo los datos
                     * del jugador con los componentes visuales
                     */
                    val player = response.body() ?: return

                    val commentaryItem =
                        CommentaryItem(
                            name = player.name,
                            //commentary = player.fullContent,
                            imageURL = player.player.icon.player_icon,
                        )
                    }

                override fun onFailure(call: Call<Player>, t: Throwable) {
                    Log.e("API", "Error: ${t.message}", t)
                }
            })
    }
}
