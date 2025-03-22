
package com.abdownloadmanager.android.ui

import androidx.compose.runtime.Composable
import com.abdownloadmanager.shared.app.ui.DownloadsPage
import org.koin.core.component.KoinComponent

@Composable
fun AppUI() {
    // This will use the shared UI components from the shared module
    // with Android-specific adaptations as needed
    DownloadsPage()
}
