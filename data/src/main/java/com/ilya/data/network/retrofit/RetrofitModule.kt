package com.ilya.data.network.retrofit

import android.util.Log
import com.ilya.data.network.retrofit.api.FriendsManageVkApi
import com.ilya.data.network.retrofit.api.PostsDataExecuteVkApi
import com.ilya.data.network.retrofit.api.UserDataExecuteVkApi
import com.ilya.data.network.retrofit.api.UserDataVkApi
import com.ilya.data.network.retrofit.api.UsersVkApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create


@Module
@InstallIn(ViewModelComponent::class)
internal object RetrofitModule {

    @Provides
    fun provideRetrofit(): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor { Log.d("okhttptag", it) }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                )
            )
            .build()

        return retrofit
    }

    @Provides
    fun provideUsersApi(retrofit: Retrofit): UsersVkApi = retrofit.create()


    @Provides
    fun provideUserDataApi(retrofit: Retrofit): UserDataVkApi = retrofit.create()


    @Provides
    fun provideFriendsManageApi(retrofit: Retrofit): FriendsManageVkApi = retrofit.create()

    @Provides
    fun provideUserDataExecutorApi(retrofit: Retrofit): UserDataExecuteVkApi = retrofit.create()

    @Provides
    fun providePostAdditionalDataExecutorApi(retrofit: Retrofit): PostsDataExecuteVkApi =
        retrofit.create()

}
