
package com.abdownloadmanager.shared.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Shared downloads page that can be used by any platform
 */
@Composable
fun DownloadsPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Downloads")
        // Download list and controls will be implemented here
        // This is a simplified version for demonstration
    }
}
