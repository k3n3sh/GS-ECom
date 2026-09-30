package com.k3n3sh.gsecom.di

import android.content.Context
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.k3n3sh.gsecom.BuildConfig
import com.k3n3sh.gsecom.data.network.AndroidConnectivityChecker
import com.k3n3sh.gsecom.data.network.ConnectivityChecker
import com.k3n3sh.gsecom.data.remote.ApiJson
import com.k3n3sh.gsecom.data.remote.ProductApi
import com.k3n3sh.gsecom.data.repository.ProductRepositoryImpl
import com.k3n3sh.gsecom.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // One client for the app
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    // BASE_URL comes from the flavor
    @Provides
    @Singleton
    fun provideProductApi(okHttpClient: OkHttpClient): ProductApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ApiJson.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ProductApi::class.java)
    }

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .components { add(OkHttpNetworkFetcherFactory(callFactory = { okHttpClient })) }
            .crossfade(true)
            .build()
    }
}

// Interface to implementation
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    abstract fun bindConnectivityChecker(impl: AndroidConnectivityChecker): ConnectivityChecker
}
