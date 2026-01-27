package com.example.marvelallies

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
import RealtimeComment


class Forum : Fragment()
{
    /*
     * Estos elementos se utilizan para mostrar el contenido
     * de una noticia seleccionada previamente y enviar el mensaje de los comentarios
     */
    private lateinit var _imageNew: ImageView
    private lateinit var _newText: TextView
    private lateinit var _commentaryAdapter: CommentaryAdapter
    private lateinit var _etComment: EditText
    private lateinit var _btnSend: Button

    private lateinit var _recyclerView: RecyclerView

    private val _auth = FirebaseAuth.getInstance()
    private val _rtdb = FirebaseDatabase
        .getInstance("https://marvelallies-default-rtdb.europe-west1.firebasedatabase.app")
        .reference

    //listener activo para los comentarios en real time
    private var _commentsListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
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
    ): View?
    {
        /*
         * Inflo el layout correspondiente al detalle de la noticia
         * y trabajo directamente sobre esta vista
         */
        val view = inflater.inflate(R.layout.fragment_forum, container, false)

        //Tomar elementos pasados
        val newImageUrl = arguments?.getString("new_image")
        val newDescription = arguments?.getString("new_description")
        Log.d("FORUM", "Arguments = ${arguments?.keySet()}")
        //se toma el id del post para el comentario, si hay uno
        val postId = arguments?.getString("post_id") ?: return view
        Log.d("FORUM", "postId = $postId")
        /*
         * Inicializo los componentes visuales que mostrarán
         * la información textual y gráfica de la noticia
         */
        _newText = view.findViewById(R.id.NewsText)
        _imageNew = view.findViewById(R.id.NewsImage)

        /*
         * Asigno directamente el texto recibido
         * Se asume que el contenido ya viene procesado desde el fragment anterior
         */
        _newText.text = newDescription ?: ""


        //Comentarios
        _recyclerView = view.findViewById(R.id.RecyclerCommentary)
        _etComment = view.findViewById(R.id.etComment)
        _btnSend = view.findViewById(R.id.btnSend)


        _recyclerView.layoutManager = LinearLayoutManager(requireContext())
        _commentaryAdapter = CommentaryAdapter()
        _recyclerView.adapter = _commentaryAdapter

        listenComments(postId)

        _btnSend.setOnClickListener {
            val text = _etComment.text.toString().trim()
            if (text.isNotEmpty())
            {
                sendComment(postId, text)
                _etComment.setText("")
            }
        }

        /*
        * Utilizo Glide para manejar la carga de imágenes remotas
        * Esto permite una carga eficiente, manejo de caché
        * y control de errores visuales
        */
        if (!newImageUrl.isNullOrBlank())
        {
            Glide.with(requireContext())
                .load("https://marvelrivalsapi.com/rivals" + newImageUrl)
                .placeholder(R.drawable.frame_1)
                .fitCenter()
                .into(_imageNew)
        }
        else
        {
            _imageNew.setImageResource(R.drawable.frame_1)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        /*
         * Intercepto el evento del botón de regreso en la Toolbar
         * para volver al fragment anterior usando el back stack
         */
        return when (item.itemId)
        {
            android.R.id.home -> {
                parentFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*
    *En en esta funcion escucho cambios en los comentarios del post en tiempo real y
    * actualiza el recycle view caundo se detecta un cambio
    */
    private fun listenComments(postId: String)
    {
        /*
        * Se obtiene la referencia a la ruta donde se almacenan
        * los comentarios del post actual dentro del Realtime Database.
        */
        val ref = _rtdb.child("comments").child(postId)

        Log.d("FORUM", "Listening path: comments/$postId")

        val listener = object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                Log.d("FORUM", "onDataChange children=${snapshot.childrenCount}")

                val temp = mutableListOf<Pair<Long, CommentaryItem>>()
                /*
                * Recorremos todos los hijos del snapshot, donde cada hijo
                * representa un comentario almacenado en la base de datos.
                */
                for (child in snapshot.children)
                {
                    val c = child.getValue(RealtimeComment::class.java) ?: continue
                    temp.add(
                        c.timestamp to CommentaryItem(
                            name = formatUid(c.authorUid),
                            commentary = c.text,
                            imageURL = ""
                        )
                    )
                }

                val list = temp.sortedBy { it.first }.map { it.second }
                _commentaryAdapter.submitList(list)
                _recyclerView.scrollToPosition(maxOf(list.size - 1, 0))
            }

            override fun onCancelled(error: DatabaseError)
            {
                Log.e("RTDB", "listenComments cancelled: ${error.message}")
            }
        }
        _commentsListener = listener
        ref.addValueEventListener(listener)
    }

    /*
     * Envía un nuevo comentario al Realtime Database asociado al post.
     */
    private fun sendComment(postId: String, text: String)
    {
        Log.d("FORUM", "Writing path: comments/$postId")

        val user = _auth.currentUser
        if (user == null)
        {
            Log.e("FORUM", "No user logged in")
            return
        }

        val comment = RealtimeComment(
            text = text,
            authorUid = user.uid,
            timestamp = System.currentTimeMillis()
        )

        _rtdb.child("comments").child(postId)
            .push()
            .setValue(comment)
            .addOnSuccessListener {
                Log.d("RTDB", "Comment saved Id=$postId uid=${user.uid}")
            }
            .addOnFailureListener { e ->
                Log.e("RTDB", "Failed to send comment postId=$postId", e)
            }

    }

    /*
    * Formatea el UID para que sea más legible en pantalla.
    */
    private fun formatUid(uid: String): String
    {
        return if (uid.length > 8) "UID ${uid.take(6)}..." else "UID $uid"
    }

}
