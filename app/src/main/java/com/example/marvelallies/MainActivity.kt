package com.example.marvelallies

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavBarViww : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavBarViww = findViewById(R.id.bottomNavigationView)
        bottomNavBarViww.setOnItemSelectedListener{ item->
            handleNavigationItemSelected(item.itemId)
        }

        LoadFragment(News())




    }
    private fun LoadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
    }

   private fun handleNavigationItemSelected(itemId: Int): Boolean{
       return  when (itemId){
           R.id.news ->{
               LoadFragment(News())
               true
           }
           R.id.characters -> {
               LoadFragment(Characters())
               true
           }
           R.id.players ->{
               LoadFragment(Players())
               true
           }
           R.id.profile ->{
               LoadFragment(Profile())
               true
           }
           else -> false
       }
   }
}