
package com.abdownloadmanager.shared.utils

import org.koin.dsl.module

/**
 * Shared Koin module that provides common dependencies across platforms
 */
object SharedAppModule {
    val module = module {
        // Common dependencies that work across platforms will be defined here
        // These will complement platform-specific implementations
    }
}
