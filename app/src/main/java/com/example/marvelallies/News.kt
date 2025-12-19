package com.example.marvelallies

import Balance
import MarvelAPI.MarvelAPIInstance
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class News : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_news, container, false)

        getNews()

        return view
    }

    private fun getNews() {
        MarvelAPIInstance.apiService.getNewsBalances(1, 20)
            .enqueue(object : Callback<Balance> {
                override fun onResponse(call: Call<Balance>, response: Response<Balance>) {
                    if (!response.isSuccessful) {
                        Log.e("NewsAPI", "Error HTTP: ${response.code()}")
                        return
                    }

                    val news = response.body()
                    if (news == null) {
                        Log.e("NewsAPI", "Response body is null")
                        return
                    }

                    val patches = news.balances.orEmpty()
                    if (patches.isEmpty()) {
                        Log.e("NewsAPI", "No balances available")
                        return
                    }

                    Log.d("News", "Total patches: ${news.total_balances}")
                    patches.forEachIndexed { index, patch ->
                        Log.d("NewsPatch", "======= PATCH #$index =======")
                        Log.d("NewsPatch", "Title: ${patch.title}")
                        Log.d("NewsPatch", "Date: ${patch.date}")
                        Log.d("NewsPatch", "Preview (first 200 chars): ${patch.fullContent.take(200)}...")
                        Log.d("NewsPatch", "Image Path: ${patch.imagePath}")
                    }
                }

                override fun onFailure(call: Call<Balance>, t: Throwable) {
                    Log.e("NewsAPI", "Request failed", t)
                }
            })
    }


}
