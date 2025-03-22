
package com.abdownloadmanager.android

import android.app.Application
import com.abdownloadmanager.android.di.androidModule
import com.abdownloadmanager.shared.utils.SharedAppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ABDownloadManagerApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@ABDownloadManagerApp)
            modules(
                androidModule,
                SharedAppModule.module
            )
        }
    }
}
