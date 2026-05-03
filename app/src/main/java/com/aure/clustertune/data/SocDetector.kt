package com.aure.clustertune.data

import android.os.Build
import java.io.BufferedReader
import java.io.InputStreamReader

open class SocDetector {

    private var cachedSocModel: String? = null

    open fun detectSocModel(): String? {
        cachedSocModel?.let { return it }
        val candidates = listOf(
            readProperty("ro.soc.model"),
            readProperty("ro.vendor.qti.soc_model"),
            readProperty("ro.fota.platform"),
            readProperty("ro.product.model"),
            readProperty("ro.product.device"),
            Build.SOC_MODEL,
            Build.HARDWARE,
            Build.BOARD,
            Build.MODEL,
            Build.PRODUCT,
            Build.DEVICE,
        )
        return candidates.firstOrNull { !it.isNullOrBlank() }
            ?.trim()
            ?.also { cachedSocModel = it }
    }

    private fun readProperty(name: String): String? {
        return runCatching {
            val process = ProcessBuilder("getprop", name)
                .redirectErrorStream(true)
                .start()
            val output = BufferedReader(InputStreamReader(process.inputStream)).use { it.readText() }
            process.waitFor()
            output.trim().takeIf { it.isNotBlank() }
        }.getOrNull()
    }
}
