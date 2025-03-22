package com.abdownloadmanager.shared.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.vector.ImageVector


@Composable
fun DownloadList(
    downloadItems: List<DownloadItem> = emptyList(),
    topBarActions: List<TopBarAction> = emptyList(),
    stats: DownloadStats = DownloadStats()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Customizable Top Bar
        TopAppBar(
            title = { Text("AB DM") },
            actions = {
                topBarActions.forEach { action ->
                    IconButton(onClick = action.onClick) {
                        Icon(action.icon, contentDescription = action.description)
                    }
                }
            }
        )

        // Tab Row with dynamic stats
        TabRow(selectedTabIndex = 0) {
            Tab(selected = true, onClick = {}) { Text("ALL ${stats.total}") }
            Tab(selected = false, onClick = {}) { Text("DOWNLOADING ${stats.downloading}") }
            Tab(selected = false, onClick = {}) { Text("FINISHED ${stats.finished}") }
            Tab(selected = false, onClick = {}) { Text("ERROR ${stats.error}") }
        }

        // Dynamic Download Items List
        LazyColumn {
            items(downloadItems) { item ->
                DownloadItem(
                    fileName = item.fileName,
                    progress = item.progress,
                    fileSize = "${item.downloadedSize} / ${item.totalSize}",
                    speed = item.speed,
                    time = item.remainingTime,
                    status = item.status
                )
            }
        }
    }
}

// Data classes to handle the dynamic nature of the UI
data class TopBarAction(
    val icon: ImageVector,
    val description: String,
    val onClick: () -> Unit
)

data class DownloadStats(
    val total: Int = 0,
    val downloading: Int = 0,
    val finished: Int = 0,
    val error: Int = 0
)

data class DownloadItem(
    val fileName: String,
    val progress: Float,
    val downloadedSize: String,
    val totalSize: String,
    val speed: String,
    val remainingTime: String,
    val status: String = ""
)

@Composable
private fun DownloadItem(
    fileName: String,
    progress: Float,
    fileSize: String,
    speed: String? = null,
    time: String,
    status: String? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(fileName, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreVert, "More options")
                }
            }

            if (progress < 1f) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(fileSize, style = MaterialTheme.typography.bodySmall)
                if (speed != null) {
                    Text(speed, style = MaterialTheme.typography.bodySmall)
                }
                if (status != null) {
                    Text(status, style = MaterialTheme.typography.bodySmall)
                }
                Text(time, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}