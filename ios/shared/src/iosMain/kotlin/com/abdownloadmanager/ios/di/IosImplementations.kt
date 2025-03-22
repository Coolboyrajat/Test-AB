
package com.abdownloadmanager.ios.di

import com.abdownloadmanager.shared.utils.DownloadFoldersRegistry
import com.abdownloadmanager.shared.utils.DownloadListDb
import com.abdownloadmanager.shared.utils.DownloadSystem
import com.abdownloadmanager.shared.utils.IDownloadListDb
import ir.amirab.downloader.DownloadManager
import ir.amirab.downloader.manager.CategoryManager
import ir.amirab.downloader.manager.QueueManager
import ir.amirab.downloader.monitor.DownloadMonitor
import ir.amirab.downloader.monitor.IDownloadMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import java.io.File

class IosDownloadSystem {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // Get iOS document directory path
    private val documentsPath: String by lazy {
        val paths = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        )
        val documentsDirectory = paths.firstOrNull() as? String ?: ""
        documentsDirectory
    }
    
    // Create a directory for downloads if it doesn't exist
    private val downloadsDirectory: File by lazy {
        val downloadsDir = File(documentsPath, "Downloads")
        if (!downloadsDir.exists()) {
            NSFileManager.defaultManager.createDirectoryAtPath(
                downloadsDir.absolutePath,
                true,
                null,
                null
            )
        }
        downloadsDir
    }
    
    // Initialize database
    private val downloadListDB: IDownloadListDb by lazy {
        DownloadListDb(scope)
    }
    
    // Initialize download folders registry
    private val foldersRegistry: DownloadFoldersRegistry by lazy {
        DownloadFoldersRegistry(
            scope = scope,
            db = downloadListDB,
            defaultDownloadFolder = downloadsDirectory
        )
    }
    
    // Initialize download manager
    private val downloadManager: DownloadManager by lazy {
        DownloadManager(scope)
    }
    
    // Initialize queue manager
    private val queueManager: QueueManager by lazy {
        QueueManager(downloadManager, scope)
    }
    
    // Initialize category manager
    private val categoryManager: CategoryManager by lazy {
        CategoryManager(scope)
    }
    
    // Initialize download monitor
    private val downloadMonitor: IDownloadMonitor by lazy {
        DownloadMonitor(downloadManager)
    }
    
    // Create the download system
    val downloadSystem: DownloadSystem by lazy {
        DownloadSystem(
            downloadManager = downloadManager,
            queueManager = queueManager,
            categoryManager = categoryManager,
            downloadMonitor = downloadMonitor,
            scope = scope,
            downloadListDB = downloadListDB,
            foldersRegistry = foldersRegistry
        ).apply {
            scope.launchWhenCreated {
                boot()
            }
        }
    }
}

fun CoroutineScope.launchWhenCreated(block: suspend () -> Unit) {
    kotlinx.coroutines.launch {
        block()
    }
}
