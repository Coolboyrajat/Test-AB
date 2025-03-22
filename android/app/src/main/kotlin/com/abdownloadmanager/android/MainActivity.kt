package com.abdownloadmanager.android

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.abdownloadmanager.android.ui.DownloadList
import com.abdownloadmanager.shared.domain.DownloadSystem
import com.abdownloadmanager.shared.domain.IDownloadMonitor
import com.abdownloadmanager.shared.ui.theme.MyApplicationTheme
import ir.amirab.downloader.downloaditem.DownloadStatus
import ir.amirab.downloader.monitor.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File


class MainActivity : ComponentActivity(), KoinComponent {

    private lateinit var downloadSystem: DownloadSystem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize download system -  This section needs significant expansion for production-ready code.  Error handling and resource management are minimal here.
        val appContext = applicationContext
        val downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: File(appContext.filesDir, "downloads").apply { mkdirs() }

        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        val downloadListDB = DownloadListDb(scope) // Requires implementation
        val foldersRegistry = DownloadFoldersRegistry(
            scope = scope,
            db = downloadListDB,
            defaultDownloadFolder = downloadsDir
        )
        val downloadManager = DownloadManager(scope) // Requires implementation, likely using a system download manager
        val queueManager = QueueManager(downloadManager, scope) // Requires implementation
        val categoryManager = CategoryManager(scope) // Requires implementation
        val downloadMonitor = DownloadMonitor(downloadManager) // Requires implementation

        downloadSystem = DownloadSystem(
            downloadManager = downloadManager,
            queueManager = queueManager,
            categoryManager = categoryManager,
            downloadMonitor = downloadMonitor,
            scope = scope,
            downloadListDB = downloadListDB,
            foldersRegistry = foldersRegistry
        )

        scope.launch {
            downloadSystem.boot() // Requires implementation in DownloadSystem
        }

        setContent {
            val scope = rememberCoroutineScope()
            var showAddDownloadDialog by remember { mutableStateOf(false) }

            MyApplicationTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("AB DM") },
                            actions = {
                                IconButton(onClick = { /* Search action */ }) {
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                }
                                IconButton(onClick = { /* Browser action - Placeholder */ }) {
                                    Icon(Icons.Default.Public, contentDescription = "Browser")
                                }
                                IconButton(onClick = { /* More action - Placeholder */ }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = { /* Menu action - Placeholder */ }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
                            }
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { showAddDownloadDialog = true },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = "Add Download")
                        }
                    }
                ) { padding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        color = Color(0xFF121212)
                    ) {
                        DownloadList(downloadSystem.downloadMonitor) // Pass the download monitor
                    }
                }

                if (showAddDownloadDialog) {
                    AddDownloadDialog(downloadSystem, scope, showAddDownloadDialog)
                }
            }
        }
    }
}

@Composable
fun AddDownloadDialog(downloadSystem: DownloadSystem, scope:CoroutineScope, showDialog: MutableState<Boolean>){
    var url by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text("Add Download") },
        text = {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmedUrl = url.trim()
                    if (trimmedUrl.isNotBlank()) {
                        scope.launch {
                            try {
                                downloadSystem.downloadManager.createDownloadJob(trimmedUrl)
                                showDialog.value = false
                            } catch (e: Exception) {
                                // Handle error appropriately
                            }
                        }
                    }
                }
            ) {
                Text("Download")
            }
        },
        dismissButton = {
            TextButton(onClick = { showDialog.value = false }) {
                Text("Cancel")
            }
        }
    )
}


class MainViewModel : androidx.lifecycle.ViewModel(), KoinComponent {
    private val downloadMonitor: IDownloadMonitor by inject()

    val activeDownloadCount = downloadMonitor.activeDownloadCount

    val allDownloadsFlow = downloadMonitor.getAllDownloadsFlow()
}

// Placeholder for DownloadItem and DownloadList composables.  These need to be adapted to use the data structures from ir.amirab.downloader
@Composable
fun DownloadList(downloadMonitor: DownloadMonitor){
    //Implement DownloadList using downloadMonitor
    Text("Download List Placeholder")
}

@Composable
fun DownloadItem(downloadItem: DownloadItem){
    // Implement DownloadItem using downloadItem
    Text("Download Item Placeholder")
}