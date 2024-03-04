package com.ilya.data.di

import android.util.Log
import com.ilya.data.VkApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


@Module
@InstallIn(ViewModelComponent::class)
internal object RetrofitModule {
    @Provides
    fun provideApi(): VkApi {
        val loggingInterceptor = HttpLoggingInterceptor { Log.d("okhttptag", it) }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        
        val okHttpsClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(VkApi.BASE_URL)
            .client(okHttpsClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        
        return retrofit.create(VkApi::class.java)
    }
}
