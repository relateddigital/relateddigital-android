package com.relateddigital.relateddigital_android.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.relateddigital.relateddigital_android.BuildConfig
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.locationPermission.LocationPermission
import com.relateddigital.relateddigital_android.model.UtilResultModel
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.relateddigital.relateddigital_android.inapp.FontFamily
import com.relateddigital.relateddigital_android.util.AppUtils.isResourceAvailable


object AppUtils {
    private var sId: String = ""
    private const val installationDir = "INSTALLATION"

    private fun id(context: Context): String {
        if (sId.isEmpty()) {
            val pm = context.packageManager
            if (pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, context.packageName)
                    == PackageManager.PERMISSION_GRANTED &&
                    pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, context.packageName)
                    == PackageManager.PERMISSION_GRANTED) {
                try {
                    sId = getIdFromExternalStorage(context)
                } catch (e: Exception) {
                    sId = ""
                    e.printStackTrace()
                }
                if (sId.isEmpty()) {
                    val installation = File(context.filesDir, installationDir)
                    sId = try {
                        if (!installation.exists()) {
                            writeInstallationFile(installation)
                        }
                        readInstallationFile(installation)
                    } catch (e: Exception) {
                        throw RuntimeException(e)
                    }
                }
            } else {
                val installation = File(context.filesDir, installationDir)
                sId = try {
                    if (!installation.exists()) {
                        writeInstallationFile(installation)
                    }
                    readInstallationFile(installation)
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        }
        return sId
    }

    @Throws(IOException::class)
    private fun readInstallationFile(installation: File): String {
        var f: RandomAccessFile? = null
        return try {
            f = RandomAccessFile(installation, "r")
            val bytes = ByteArray(f.length().toInt())
            f.readFully(bytes)
            String(bytes)
        } finally {
            if (f != null) {
                try {
                    f.close()
                } catch (e: IOException) {
                    Log.e("Error", e.toString())
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun writeInstallationFile(installation: File) {
        val out = FileOutputStream(installation)
        val id = UUID.randomUUID().toString()
        out.write(id.toByteArray())
        out.close()
    }

    fun getAppVersion(context: Context): String {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return pInfo.versionName
        } catch (e: Exception) {
            Log.d("AppVersion", "Version Name Error : $e")
        }
        return ""
    }

    fun getIdentifierForVendor(context: Context): String {
        return id(context)
    }

    fun getOsVersion(): String {
        return Build.VERSION.RELEASE
    }

    fun getSdkVersion(): String {
        return BuildConfig.VERSION_NAME
    }

    fun getOsType(): String {
        return "Android"
    }

    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            model
        } else {
            "$manufacturer $model"
        }
    }

    fun getCarrier(context: Context): String {
        val manager = context
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return manager.networkOperator
    }

    fun getLocal(context: Context): String {
        return context.resources.configuration.locale.language
    }

    fun getUserAgent(): String {
        return System.getProperty("http.agent")!!
    }

    fun getDeviceType(): String {
        return Build.MANUFACTURER + " : " + Build.MODEL
    }

    fun getCookieId(context: Context): String {
        var cookieId: String = SharedPref.readString(context, Constants.COOKIE_ID_KEY, "")
        if (cookieId.isEmpty()) {
            cookieId = createNewCookieId()
            SharedPref.writeString(context, Constants.COOKIE_ID_KEY, cookieId)
        }
        return cookieId
    }

    private fun createNewCookieId(): String {
        return UUID.randomUUID().toString()
    }

    fun clearCookieId(context: Context) {
        SharedPref.clearKey(context, Constants.COOKIE_ID_KEY)
    }

    fun getCurrentDateString(): String? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormat.format(Date())
    }

    fun getCurrentTurkeyDateString(): String? {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val tzTurkey = TimeZone.getTimeZone("Turkey")
            dateFormat.timeZone = tzTurkey
            dateFormat.format(Date())
        } catch (e: Exception) {
            Log.d("TurkeyDate", "Turkey timezone error : $e")
            getCurrentDateString()
        }
    }

    @Throws(Exception::class)
    private fun getIdFromExternalStorage(context: Context): String {
        var ID: String = ""
        val state = Environment.getExternalStorageState()
        if (state == Environment.MEDIA_MOUNTED) {
            val sdcard = Environment.getExternalStorageDirectory()
            if (!sdcard.exists()) {
                sdcard.mkdirs()
            }
            val dir = File(sdcard.absolutePath + "/Download/")
            if (!dir.exists()) {
                dir.mkdir()
            }
            val file = File(dir, "Euromessage")
            if (!file.exists()) {
                val installation = File(context.filesDir, installationDir)
                try {
                    if (installation.exists()) {
                        sId = readInstallationFile(installation)
                    }
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
                file.createNewFile()
                var fos: FileOutputStream? = null
                try {
                    fos = FileOutputStream(file)
                    if (sId.isEmpty()) {
                        fos.write(UUID.randomUUID().toString().toByteArray())
                    } else {
                        fos.write(sId!!.toByteArray())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    ID = ""
                } finally {
                    fos!!.close()
                }
            }
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(file)
                val isr = InputStreamReader(fis)
                val buff = BufferedReader(isr)
                val sb = StringBuilder()
                var line = buff.readLine()
                while (line != null) {
                    sb.append(line)
                    line = buff.readLine()
                }
                ID = try {
                    sb.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    ""
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ID = ""
            } finally {
                fis!!.close()
            }
        } else {
            ID = ""
        }
        return ID
    }

    fun getLocationPermissionStatus(context: Context?): LocationPermission {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationPermission.ALWAYS
            } else {
                LocationPermission.NONE
            }
        } else {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationPermission.ALWAYS
            } else {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    LocationPermission.APP_OPEN
                } else {
                    LocationPermission.NONE
                }
            }
        }
    }

    fun getNumberFromText(textData: String?): UtilResultModel? {
        var text = textData
        var model: UtilResultModel? = null

        if (!text.isNullOrEmpty()) {
            try {
                val numbers: MutableList<String> = ArrayList()
                val pattern = Pattern.compile("<COUNT>(.+?)</COUNT>", Pattern.DOTALL)
                val matcher = pattern.matcher(text)
                while (matcher.find()) {
                    numbers.add(matcher.group(1)!!)
                }
                if (numbers.isNotEmpty()) {
                    model = UtilResultModel()
                    text = text.replace("<COUNT>".toRegex(), "")
                    text = text.replace("</COUNT>".toRegex(), "")
                    model.isTag = true
                    model.message = text
                    var idxToStart = 0
                    for (i in numbers.indices) {
                        val number = numbers[i].toInt()
                        val idx = text.indexOf(numbers[i], idxToStart)
                        if (idx != -1) {
                            model.addStartIdx(idx)
                            model.addEndIdx(idx + numbers[i].length)
                            model.addNumber(number)
                        }
                        idxToStart = text.indexOf(numbers[i]) + 1
                    }
                } else {
                    if (text.contains("<COUNT>")) {
                        Log.e("SocialProof", "Could not parse the number!")
                    } else {
                        Log.e("SocialProof", "Tag COUNT is not used!")
                        model = UtilResultModel()
                        model.message = text
                        model.isTag = false
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.e("SocialProof", "Could not parse the number!")
                model = null
            }
        }

        return model
    }

    fun cleanParameters(map: HashMap<String, String>) {
        for (key in map.keys) {
            if (!StringUtils.isNullOrWhiteSpace(key)) {
                if (!(key != Constants.ORGANIZATION_ID_REQUEST_KEY && key != Constants.SITE_ID_REQUEST_KEY
                                && key != Constants.EXVISITOR_ID_REQUEST_KEY && key != Constants.COOKIE_ID_REQUEST_KEY
                                && key != Constants.ZONE_ID_KEY && key != Constants.BODY_KEY
                                && key != Constants.TOKEN_ID_REQUEST_KEY && key != Constants.APP_ID_REQUEST_KEY
                                && key != Constants.API_VERSION_REQUEST_KEY && key != Constants.FILTER_KEY)) {
                    map.remove(key)
                }
            } else {
                map.remove(key)
            }
        }
    }

    fun isResourceAvailable(context: Context, name: String?): Boolean {
        val res = context.resources.getIdentifier(name, "font", context.packageName)
        return res != 0
    }

    fun getNotificationPermissionStatus(context: Context): String {
        return if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            "granted"
        } else {
            "denied"
        }
    }

    fun isDateDifferenceGreaterThan(date1: String?, date2: String?, thresholdDay: Int): Boolean {
        var result = true
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            val dateRecent = dateFormat.parse(date1!!)
            val dateFar = dateFormat.parse(date2!!)
            if (dateRecent!!.time - dateFar!!.time <= thresholdDay * 24 * 60 * 60 * 1000) {
                result = false
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return true
        }
        return result
    }

    fun getFontFamily(context: Context, fontFamily: String?, fontName: String?): Typeface {
        if (fontFamily.isNullOrEmpty()) {
            return Typeface.DEFAULT
        }
        if (FontFamily.Monospace.toString() == fontFamily.lowercase(Locale.getDefault())) {
            return Typeface.MONOSPACE
        }
        if (FontFamily.SansSerif.toString() == fontFamily.lowercase(Locale.getDefault())) {
            return Typeface.SANS_SERIF
        }
        if (FontFamily.Serif.toString() == fontFamily.lowercase(Locale.getDefault())) {
            return Typeface.SERIF
        }
        if (!fontName.isNullOrEmpty()) {
            if (isResourceAvailable(context, fontName)) {
                val id = context.resources.getIdentifier(fontName, "font", context.packageName)
                return ResourcesCompat.getFont(context, id)!!
            }
        }
        return Typeface.DEFAULT
    }

}