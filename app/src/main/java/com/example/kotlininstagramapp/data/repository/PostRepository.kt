package com.example.kotlininstagramapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.kotlininstagramapp.data.api.BaseResponse
import com.example.kotlininstagramapp.data.api.PostApi
import com.example.kotlininstagramapp.data.model.HomePagePostItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import javax.inject.Inject


class PostRepository @Inject constructor(private val postService: PostApi)  {
    val PAGE_SIZE = 5
    var currentPage = 0
    var isLoading = false

    fun getHomePagePosts(page: Int): MutableLiveData<List<HomePagePostItem>> {
        val userId = UserSingleton.userModel!!.userId
        val liveData = MutableLiveData<List<HomePagePostItem>>()

        postService.getPagedPostsFromFollowedUsers(userId,page,PAGE_SIZE).enqueue(object : Callback<BaseResponse>{
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if(response.isSuccessful){
                    val body = response.body()?.data as? List<Map<String,Any>>
                    val list  =  body?.map { HomePagePostItem.fromMap(it) }
                    liveData.value = list?.let { ArrayList(it) }
                }else{
                    liveData.value = emptyList()
                }
                isLoading = false
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
        return liveData

    }

}