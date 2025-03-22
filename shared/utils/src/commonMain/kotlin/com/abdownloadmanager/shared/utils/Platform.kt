
package com.abdownloadmanager.shared.utils

/**
 * Platform-specific file path provider
 */
interface FilePathProvider {
    /**
     * Returns the default download directory for the platform
     */
    fun getDefaultDownloadDirectory(): String
    
    /**
     * Returns the app data directory for the platform
     */
    fun getAppDataDirectory(): String
    
    /**
     * Returns the app cache directory for the platform
     */
    fun getAppCacheDirectory(): String
    
    /**
     * Returns a temporary directory for the platform
     */
    fun getTempDirectory(): String
}

/**
 * Platform-specific download system operations
 */
interface DownloadSystem {
    /**
     * Opens a file with the default application
     */
    fun openFile(path: String)
    
    /**
     * Shows a file in the file explorer, highlighting it
     */
    fun revealInFileExplorer(path: String)
    
    /**
     * Opens a URL in the default browser
     */
    fun openUrl(url: String)
}
