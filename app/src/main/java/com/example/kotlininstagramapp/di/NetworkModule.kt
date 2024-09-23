package com.example.kotlininstagramapp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.enesalgan.nswebrtc.NSWebRTCClient
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.data.api.FollowApi
import com.example.kotlininstagramapp.data.api.LikesApi
import com.example.kotlininstagramapp.data.api.NotificationApi
import com.example.kotlininstagramapp.data.api.PostApi
import com.example.kotlininstagramapp.data.api.StoryApi
import com.example.kotlininstagramapp.data.api.UserApi
import com.example.kotlininstagramapp.utils.DatabaseHelper
import com.example.turkiyefinansappclone.video_call.repository.MainRepository
import com.example.turkiyefinansappclone.video_call.repository.MainService
import com.example.turkiyefinansappclone.video_call.service.FirebaseClient
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApplicationContext(app: Application): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideDatabaseHelper(
        userService: UserApi,
        postService: PostApi,
        followService: FollowApi,
        notificationService: NotificationApi,
        likesService: LikesApi,
        storyService: StoryApi,
        firebaseHelper: FirebaseHelper
    ): DatabaseHelper {
        return DatabaseHelper(
            userService,
            postService,
            followService,
            notificationService,
            likesService,
            storyService,
            firebaseHelper
        )
    }



    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.37:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //http://10.0.2.2:8080/
    //http://192.168.1.37:8080/

    @Provides
    @Singleton
    fun provideFirebaseHelper(): FirebaseHelper {
        return FirebaseHelper()
    }

    // "http://10.0.2.2:8080/"
    //"http://192.168.1.36:8080/"



    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun providePostApi(retrofit: Retrofit): PostApi {
        return retrofit.create(PostApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFollowService(retrofit: Retrofit): FollowApi {
        return retrofit.create(FollowApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationService(retrofit: Retrofit): NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLikesService(retrofit: Retrofit): LikesApi {
        return retrofit.create(LikesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideStoryApi(retrofit: Retrofit): StoryApi {
        return retrofit.create(StoryApi::class.java)
    }



    @Provides
    fun provideGson():Gson = Gson()


    @Provides
    @Singleton
    fun provideFirebaseClient(dbRef: FirebaseFirestore, gson: Gson): FirebaseClient {
        return FirebaseClient(dbRef, gson)
    }



    @Provides
    @Singleton
    fun provideNSWebRTCClient(context: Context): NSWebRTCClient {
        return NSWebRTCClient(context)
    }



    @Provides
    @Singleton
    fun provideMainRepository(
        firebaseClient: FirebaseClient,
        webRTCClient: NSWebRTCClient,
        gson: Gson
    ): MainRepository {
        return MainRepository(firebaseClient, webRTCClient, gson)
    }



    @Provides
    @Singleton
    fun provideMainServiceRepository(mainRepository: MainRepository): MainService {
        return MainService(mainRepository)
    }

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()






}
