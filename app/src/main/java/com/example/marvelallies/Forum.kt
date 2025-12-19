package com.example.marvelallies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide


class Forum : Fragment() {

    private lateinit var back: ImageButton
    private lateinit var imageNew: ImageView
    private lateinit var newText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forum, container, false)

        //Tomar elementos pasados
        val newImageUrl = arguments?.getString("new_image")
        val newDescription = arguments?.getString("new_description")

        newText = view.findViewById(R.id.NewsText)
        imageNew = view.findViewById(R.id.NewsImage)


        newText.text = newDescription

        Glide.with(requireContext())
            //Si carga toma la imagen de este URL
            .load("https://marvelrivalsapi.com/rivals"+newImageUrl)
            //Si no pone una de placeholder
            .placeholder(R.drawable.frame_1)
            //Acomoda la imagen en el centro del item
            .fitCenter()
            //En la imagen del item
            .into(imageNew)

        // Aquí usas la vista inflada para encontrar el botón
        back = view.findViewById(R.id.Back)
        back.setOnClickListener {
            replaceFragment()
        }

        return view
    }


    private fun replaceFragment(){

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, News())
        fragmentTransaction.commit()
    }

}