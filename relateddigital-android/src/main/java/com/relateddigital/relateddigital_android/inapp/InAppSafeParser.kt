package com.relateddigital.relateddigital_android.inapp

import com.google.gson.Gson
import java.net.URI
import java.util.Locale

object SafeColor {
    private val namedColors = mapOf(
        "black" to 0xFF000000.toInt(),
        "darkgray" to 0xFF444444.toInt(),
        "gray" to 0xFF888888.toInt(),
        "lightgray" to 0xFFCCCCCC.toInt(),
        "white" to 0xFFFFFFFF.toInt(),
        "red" to 0xFFFF0000.toInt(),
        "green" to 0xFF00FF00.toInt(),
        "blue" to 0xFF0000FF.toInt(),
        "yellow" to 0xFFFFFF00.toInt(),
        "cyan" to 0xFF00FFFF.toInt(),
        "magenta" to 0xFFFF00FF.toInt(),
        "aqua" to 0xFF00FFFF.toInt(),
        "fuchsia" to 0xFFFF00FF.toInt(),
        "darkgrey" to 0xFF444444.toInt(),
        "grey" to 0xFF888888.toInt(),
        "lightgrey" to 0xFFCCCCCC.toInt(),
        "lime" to 0xFF00FF00.toInt(),
        "maroon" to 0xFF800000.toInt(),
        "navy" to 0xFF000080.toInt(),
        "olive" to 0xFF808000.toInt(),
        "purple" to 0xFF800080.toInt(),
        "silver" to 0xFFC0C0C0.toInt(),
        "teal" to 0xFF008080.toInt(),
        "transparent" to 0x00000000
    )

    @JvmStatic
    fun parseOrNull(value: String?): Int? {
        val color = value?.trim()
        if (color.isNullOrEmpty()) return null
        if (color.startsWith("rgb", ignoreCase = true)) return parseRgb(color)
        val hex = when {
            color.startsWith("#") -> color.substring(1)
            color.length == 6 || color.length == 8 -> color
            else -> return namedColors[color.lowercase(Locale.ROOT)]
        }
        if (hex.length != 6 && hex.length != 8) return null
        val parsed = hex.toLongOrNull(16) ?: return null
        return if (hex.length == 6) (parsed or 0xFF000000L).toInt() else parsed.toInt()
    }

    @JvmStatic
    fun parse(value: String?, fallback: Int): Int = parseOrNull(value) ?: fallback

    @JvmStatic
    fun isValid(value: String?): Boolean = parseOrNull(value) != null

    private fun parseRgb(value: String): Int? {
        val open = value.indexOf('(')
        val close = value.lastIndexOf(')')
        if (open < 0 || close <= open) return null
        val parts = value.substring(open + 1, close).split(",").map { it.trim() }
        if (parts.size != 3 && parts.size != 4) return null
        val rgb = parts.take(3).map { it.toFloatOrNull()?.toInt() ?: return null }
        if (rgb.any { it !in 0..255 }) return null
        val alpha = if (parts.size == 4) {
            val a = parts[3].toFloatOrNull() ?: return null
            if (a < 0f || a > 1f) return null
            Math.round(a * 255)
        } else {
            255
        }
        return (alpha shl 24) or (rgb[0] shl 16) or (rgb[1] shl 8) or rgb[2]
    }
}

fun String?.toFloatOr(default: Float): Float {
    return this?.trim()?.removeSuffix("px")?.toFloatOrNull() ?: default
}

fun String?.toIntOr(default: Int): Int {
    val value = this?.trim()?.removeSuffix("px") ?: return default
    return value.toIntOrNull() ?: value.toFloatOrNull()?.toInt() ?: default
}

fun String?.toLongOr(default: Long): Long {
    val value = this?.trim() ?: return default
    return value.toLongOrNull() ?: value.toDoubleOrNull()?.toLong() ?: default
}

fun <T> parseEncodedExtendedProps(encoded: String?, clazz: Class<T>): T? {
    if (encoded.isNullOrEmpty()) return null
    return try {
        Gson().fromJson(URI(encoded).path, clazz)
    } catch (e: Exception) {
        null
    }
}
