package com.example.block_sites_flutter

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.accessibility/channel"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(
                flutterEngine.dartExecutor.binaryMessenger,
                CHANNEL
        ).setMethodCallHandler { call, result ->
            when (call.method) {
                "openAccessibilitySettings" -> {
                    try {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        result.success(null)
                    } catch (e: Exception) {
                        result.error("ERROR", "Failed to open settings: ${e.message}", null)
                    }
                }

                "isAccessibilityEnabled" -> {
                    try {
                        val accessibilityEnabled = Settings.Secure.getInt(
                                contentResolver,
                                Settings.Secure.ACCESSIBILITY_ENABLED
                        )

                        if (accessibilityEnabled == 1) {
                            val enabledServices = Settings.Secure.getString(
                                    contentResolver,
                                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                            )

                            if (!TextUtils.isEmpty(enabledServices) &&
                                    enabledServices.contains("com.example.block_sites_flutter/.MyAccessibilityService")
                            ) {
                                result.success(true)
                                return@setMethodCallHandler
                            }
                        }

                        result.success(false)
                    } catch (e: Settings.SettingNotFoundException) {
                        result.error("ERROR", "Setting not found: ${e.message}", null)
                    }
                }

                else -> result.notImplemented()
            }
        }
    }
}
