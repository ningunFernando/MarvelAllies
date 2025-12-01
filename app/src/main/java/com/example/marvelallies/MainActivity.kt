package com.example.marvelallies

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.marvelallies.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ReplaceFragment(News())

        binding.bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.news -> ReplaceFragment(News())
                R.id.characters -> ReplaceFragment(Characters())
                R.id.players -> ReplaceFragment(Players())
                R.id.profile -> ReplaceFragment(Profile())

                else -> {

                }
            }
            true
        }

    }

    private fun ReplaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}