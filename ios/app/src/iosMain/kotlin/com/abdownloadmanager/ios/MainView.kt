
package com.abdownloadmanager.ios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.abdownloadmanager.ios.di.IosDownloadSystem
import com.abdownloadmanager.shared.ui.components.DownloadItemRow
import com.abdownloadmanager.shared.ui.components.DownloadListTabRow
import com.abdownloadmanager.shared.ui.components.ProgressBar
import com.abdownloadmanager.shared.utils.bytesToReadableStr
import com.abdownloadmanager.shared.utils.formatETA
import ir.amirab.downloader.downloaditem.DownloadStatus
import ir.amirab.downloader.monitor.CompletedDownloadItemState
import ir.amirab.downloader.monitor.IDownloadItemState
import ir.amirab.downloader.monitor.ProcessingDownloadItemState
import ir.amirab.downloader.monitor.statusOrFinished
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class TopBarAction(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val description: String,
    val onClick: () -> Unit
)

data class DownloadStats(
    val total: Int,
    val downloading: Int,
    val finished: Int,
    val error: Int
)

@Composable
fun MainView() {
    // Initialize dependency injection
    val downloadSystem = remember { IosDownloadSystem().downloadSystem }
    val downloadMonitor = downloadSystem.downloadMonitor
    val scope = rememberCoroutineScope()
    
    // Track current tab
    var selectedTab by remember { mutableStateOf(0) }
    
    // Collect download stats
    val downloadStats by produceState(
        initialValue = DownloadStats(0, 0, 0, 0),
        downloadMonitor.downloadListFlow,
        downloadMonitor.activeDownloadListFlow,
        downloadMonitor.completedDownloadListFlow
    ) {
        val downloads = downloadMonitor.downloadListFlow.value
        val active = downloadMonitor.activeDownloadListFlow.value
        val completed = downloadMonitor.completedDownloadListFlow.value
        
        val error = downloads.count { 
            when (it) {
                is ProcessingDownloadItemState -> it.statusOrFinished() is DownloadStatus.Error
                is CompletedDownloadItemState -> it.status is DownloadStatus.Error
                else -> false
            }
        }
        
        value = DownloadStats(
            total = downloads.size,
            downloading = active.size,
            finished = completed.size,
            error = error
        )
    }
    
    // Filter downloads based on selected tab
    val filteredDownloads by produceState(
        initialValue = emptyList<IDownloadItemState>(),
        downloadMonitor.downloadListFlow,
        selectedTab
    ) {
        value = when (selectedTab) {
            0 -> downloadMonitor.downloadListFlow.value // All
            1 -> downloadMonitor.activeDownloadListFlow.value // Downloading
            2 -> downloadMonitor.completedDownloadListFlow.value.filter { it.status !is DownloadStatus.Error } // Finished
            3 -> downloadMonitor.downloadListFlow.value.filter { 
                when (it) {
                    is ProcessingDownloadItemState -> it.statusOrFinished() is DownloadStatus.Error
                    is CompletedDownloadItemState -> it.status is DownloadStatus.Error
                    else -> false
                }
            } // Error
            else -> downloadMonitor.downloadListFlow.value
        }
    }
    
    // Define top bar actions
    val topBarActions = listOf(
        TopBarAction(
            icon = Icons.Default.Search,
            description = "Search",
            onClick = { /* Implement search functionality */ }
        ),
        TopBarAction(
            icon = Icons.Default.Public,
            description = "Browser",
            onClick = { /* Implement browser functionality */ }
        ),
        TopBarAction(
            icon = Icons.Default.Settings,
            description = "Settings",
            onClick = { /* Implement settings */ }
        )
    )
    
    // Show Add Download Dialog
    var showAddDownloadDialog by remember { mutableStateOf(false) }
    
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("AB DM") },
                    actions = {
                        topBarActions.forEach { action ->
                            IconButton(onClick = action.onClick) {
                                Icon(action.icon, contentDescription = action.description)
                            }
                        }
                        // More menu
                        IconButton(onClick = { /* Implement more menu */ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { /* Implement menu */ }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF121212)
                    )
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFF121212))
            ) {
                // Tab row
                DownloadListTabRow(
                    selectedTabIndex = selectedTab,
                    onTabSelected = { selectedTab = it },
                    tabs = listOf(
                        "ALL" to downloadStats.total.toString(),
                        "DOWNLOADING" to downloadStats.downloading.toString(),
                        "FINISHED" to downloadStats.finished.toString(),
                        "ERROR" to downloadStats.error.toString()
                    )
                )
                
                // Download list
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredDownloads) { downloadItem ->
                        DownloadItemRow(downloadItem = downloadItem)
                    }
                }
            }
        }
        
        // Add Download Dialog
        if (showAddDownloadDialog) {
            AddDownloadDialog(
                onDismiss = { showAddDownloadDialog = false },
                onAddDownload = { url ->
                    scope.launch {
                        try {
                            downloadSystem.downloadManager.createDownloadJob(url)
                            showAddDownloadDialog = false
                        } catch (e: Exception) {
                            // Handle error
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun AddDownloadDialog(
    onDismiss: () -> Unit,
    onAddDownload: (String) -> Unit
) {
    var url by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Download") },
        text = {
            Column {
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAddDownload(url) },
                enabled = url.isNotBlank()
            ) {
                Text("Download")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
