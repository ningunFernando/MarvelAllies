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
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import models.CommentaryAdapter
import models.CommentaryItem
import Player
import RealtimeComment


class Forum : Fragment() {
    /*
     * Estos elementos se utilizan para mostrar el contenido
     * de una noticia seleccionada previamente y enviar el mensaje de los comentarios
     */
    private lateinit var imageNew: ImageView
    private lateinit var newText: TextView
    private lateinit var commentaryAdapter: CommentaryAdapter
    private lateinit var etComment: EditText
    private lateinit var btnSend: Button

    private lateinit var recyclerView: RecyclerView

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val rtdb = FirebaseDatabase.getInstance().reference

    private var commentsListener: ValueEventListener? = null

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

        //se toma el id del post para el comentario si hay uno
        val postId = arguments?.getString("post_id") ?: return view

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
        newText.text = newDescription ?: ""

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
        etComment = view.findViewById(R.id.etComment)
        btnSend = view.findViewById(R.id.btnSend)


        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        commentaryAdapter = CommentaryAdapter()
        recyclerView.adapter = commentaryAdapter

        listenComments(postId)

        btnSend.setOnClickListener {
            val text = etComment.text.toString().trim()
            if (text.isNotEmpty()) {
                sendComment(postId, text)
                etComment.setText("")
            }
        }
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

    private fun listenComments(postId: String) {
        val ref = rtdb.child("comments").child(postId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CommentaryItem>()

                for (child in snapshot.children) {
                    val c = child.getValue(RealtimeComment::class.java) ?: continue

                    list.add(
                        CommentaryItem(
                            name = if (c.authorName.isNotBlank()) c.authorName else "UID ${c.authorApiId}",
                            commentary = c.text,
                            imageURL = c.authorIcon
                        )
                    )
                }

             //nos da el comentario mas reciente hasta abajo de la lista
                commentaryAdapter.submitList(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RTDB", "listenComments cancelled: ${error.message}")
            }
        }

        commentsListener = listener
        ref.orderByChild("timestamp").addValueEventListener(listener)
    }

    private fun sendComment(postId: String, text: String) {
        val user = auth.currentUser
        if (user == null) {
            Log.e("FORUM", "No user logged in")
            return
        }

        firestore.collection("users").document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                val apiId = doc.getLong("apiId")
                if (apiId == null || apiId == 0L) {
                    Log.e("FORUM", "apiId missing. User must set it in Profile.")
                    return@addOnSuccessListener
                }

                fetchPlayerPreview(apiId) { authorName, authorIcon ->
                    val comment = RealtimeComment(
                        text = text,
                        authorUid = user.uid,
                        authorApiId = apiId,
                        authorName = authorName,
                        authorIcon = authorIcon,
                        timestamp = System.currentTimeMillis()
                    )

                    rtdb.child("comments").child(postId)
                        .push()
                        .setValue(comment)
                        .addOnFailureListener { e ->
                            Log.e("RTDB", "Failed to send comment", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FORUM", "Failed to read user apiId", e)
            }
    }


    private fun fetchPlayerPreview(
        apiId: Long,
        onDone: (name: String, iconPath: String) -> Unit
    ) {
        MarvelAPIInstance.apiService.getPlayerById(apiId.toString())
            .enqueue(object : Callback<Player> {
                override fun onResponse(call: Call<Player>, response: Response<Player>) {
                    val player = response.body()
                    val name = player?.name ?: "Unknown"
                    val icon = player?.player?.icon?.player_icon ?: ""
                    onDone(name, icon)
                }

                override fun onFailure(call: Call<Player>, t: Throwable) {
                    Log.e("API", "fetchPlayerPreview failed: ${t.message}", t)
                    onDone("Unknown", "")
                }
            })
    }

}
