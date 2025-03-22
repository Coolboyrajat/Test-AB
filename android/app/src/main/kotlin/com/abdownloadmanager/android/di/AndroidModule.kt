
package com.abdownloadmanager.android.di

import android.content.Context
import com.abdownloadmanager.shared.utils.DownloadSystem
import com.abdownloadmanager.shared.utils.FilePathProvider
import ir.amirab.downloader.connection.DownloaderClient
import ir.amirab.downloader.connection.OkHttpDownloaderClient
import ir.amirab.downloader.connection.proxy.ProxyStrategyProvider
import ir.amirab.downloader.utils.IDiskStat
import ir.amirab.util.config.datastore.createMapConfigDatastore
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    // Android context
    single { androidContext() }
    
    // Android-specific implementations
    single<FilePathProvider> { AndroidFilePathProvider(get()) }
    single<IDiskStat> { AndroidDiskStat(get()) }
    single<DownloadSystem> { AndroidDownloadSystem(get()) }
    
    // HTTP client
    single {
        OkHttpClient.Builder()
            .build()
    }
    
    single<DownloaderClient> {
        OkHttpDownloaderClient(get(), get<ProxyStrategyProvider>())
    }
    
    // Datastore
    single {
        createMapConfigDatastore(
            path = androidContext().filesDir.resolve("config").absolutePath,
            coroutineContext = Dispatchers.IO
        )
    }
}
