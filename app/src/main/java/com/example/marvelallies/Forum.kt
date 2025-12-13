package com.example.marvelallies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton


class Forum : Fragment() {

    private lateinit var back: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forum, container, false)

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