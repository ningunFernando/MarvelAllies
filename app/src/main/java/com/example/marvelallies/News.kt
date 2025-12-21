package com.example.marvelallies

import Balance
import MarvelAPI.MarvelAPIInstance
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import models.NewsAdapter
import models.NewsItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager



class News : Fragment() {

    private lateinit var recyclerNews: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //para que no crashee con ayuda de chatgtp
        getNews()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_news, container, false)

        recyclerNews = view.findViewById(R.id.RecyclerNews)

        recyclerNews.layoutManager = LinearLayoutManager(requireContext())
        progressBar = view.findViewById(R.id.progressBar)


        getNews()

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.apply {
            title = getString(R.string.News)
            setDisplayHomeAsUpEnabled(false)
            setHomeAsUpIndicator(null)
        }
    }



    private fun getNews() {
        progressBar.visibility = View.VISIBLE

        MarvelAPIInstance.apiService.getNewsBalances(1, 20)
            .enqueue(object : Callback<Balance> {
                override fun onResponse(call: Call<Balance>, response: Response<Balance>) {
                    progressBar.visibility = View.GONE

                    if (!response.isSuccessful) {
                        Log.e("NewsAPI", "Error HTTP: ${response.code()}")
                        return
                    }

                    val news = response.body()
                    if (news == null) {
                        Log.e("NewsAPI", "Response body is null")
                        return
                    }



                    val newsItems = news.balances!!.map { player ->
                        NewsItem(
                            title = player.title,
                            description = player.fullContent,
                            imageUrl = player.imagePath
                        )
                    }

                    val adapter = NewsAdapter(newsItems) { new ->
                        onCharacterClicked(new)
                    }

                    //Cambiar el recycler view
                    recyclerNews.adapter = adapter


                }

                override fun onFailure(call: Call<Balance>, t: Throwable) {
                    Log.e("NewsAPI", "Request failed", t)
                    progressBar.visibility = View.GONE

                }
            })
    }
    private fun onCharacterClicked(new: NewsItem) {
        // Aquí puedes navegar al fragmento de detalles
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        //Pasar argumentos al segundo fragment
        val detailsFragment = Forum().apply {
            arguments = Bundle().apply {
                //mandar id al otro fragment
                putString("new_image", new.imageUrl)
                putString("new_description", new.description)
            }

        }
        //cambiar de fragment
        fragmentTransaction.replace(R.id.frameLayout, detailsFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


}
