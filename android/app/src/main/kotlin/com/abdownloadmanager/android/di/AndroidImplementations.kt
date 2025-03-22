
package com.abdownloadmanager.android.di

import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.abdownloadmanager.shared.utils.DownloadSystem
import com.abdownloadmanager.shared.utils.FilePathProvider
import ir.amirab.downloader.utils.IDiskStat
import java.io.File

class AndroidFilePathProvider(private val context: Context) : FilePathProvider {
    override fun getDefaultDownloadDirectory(): String {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
    }
    
    override fun getAppDataDirectory(): String {
        return context.filesDir.absolutePath
    }
    
    override fun getAppCacheDirectory(): String {
        return context.cacheDir.absolutePath
    }
    
    override fun getTempDirectory(): String {
        return context.cacheDir.resolve("temp").apply { mkdirs() }.absolutePath
    }
}

class AndroidDiskStat(private val context: Context) : IDiskStat {
    override fun getFreeSpace(path: String): Long {
        val statFs = StatFs(path)
        return statFs.availableBlocksLong * statFs.blockSizeLong
    }
    
    override fun getTotalSpace(path: String): Long {
        val statFs = StatFs(path)
        return statFs.totalBlocksLong * statFs.blockSizeLong
    }
}

class AndroidDownloadSystem(private val context: Context) : DownloadSystem {
    override fun openFile(path: String) {
        // Implementation will use Android's Intent system to open files
        // This would use FileProvider and Intent.ACTION_VIEW
    }
    
    override fun revealInFileExplorer(path: String) {
        // This would open the file's parent directory
        // Similar to openFile but targeting the directory
    }
    
    override fun openUrl(url: String) {
        // This would use an Intent with ACTION_VIEW to open URLs
    }
}
