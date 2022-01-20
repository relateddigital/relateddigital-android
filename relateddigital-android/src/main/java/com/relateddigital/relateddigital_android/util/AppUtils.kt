package com.relateddigital.relateddigital_android.util

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import android.media.RingtoneManager
import android.net.Uri
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.TypedValue
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.inapp.FontFamily
import com.relateddigital.relateddigital_android.model.Message
import com.relateddigital.relateddigital_android.model.SpinToWin
import com.relateddigital.relateddigital_android.model.SpinToWinExtendedProps
import java.net.URI
import java.net.URL
import java.nio.charset.StandardCharsets


object AppUtils {
    private var sId: String = ""
    private const val installationDir = "INSTALLATION"

    private fun id(context: Context): String {
        if (sId.isEmpty()) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
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
            } else {
                val pm = context.packageManager
                if (pm.checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        context.packageName
                    )
                    == PackageManager.PERMISSION_GRANTED &&
                    pm.checkPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        context.packageName
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
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

    fun isFontResourceAvailable(context: Context, name: String?): Boolean {
        val res = context.resources.getIdentifier(name, "font", context.packageName)
        return res != 0
    }

    fun isIconResourceAvailable(context: Context?, resId: Int): Boolean {
        if (context != null) {
            try {
                return context.resources.getResourceName(resId) != null
            } catch (ignore: NotFoundException) {
                val element = Throwable().stackTrace[0]
                LogUtils.formGraylogModel(
                    context,
                    "e",
                    "Checking if a resource is available : " + ignore.message,
                    element.className + "/" + element.methodName + "/" + element.lineNumber
                )
            }
        }
        return false
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
            if (isFontResourceAvailable(context, fontName)) {
                val id = context.resources.getIdentifier(fontName, "font", context.packageName)
                return ResourcesCompat.getFont(context, id)!!
            }
        }
        return Typeface.DEFAULT
    }

    fun getBitMapFromUri(context: Context, photoUrl: String): Bitmap? {
        val url: URL
        var image: Bitmap? = null
        try {
            setThreadPool()
            url = URL(photoUrl)
            val connection = url.openConnection()
            connection.readTimeout = 30000 // 30 sec
            image = BitmapFactory.decodeStream(connection.getInputStream())
        } catch (e: IOException) {
            val element = Throwable().stackTrace[0]
            LogUtils.formGraylogModel(
                context,
                "e",
                "Getting bitmap from uri : " + e.message,
                element.className + "/" + element.methodName + "/" + element.lineNumber
            )
            e.printStackTrace()
        }
        return image
    }

    private fun setThreadPool() {
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    fun getNotificationChannelId(context: Context, isNew: Boolean): String {
        val oldChannelId: String =
            SharedPref.readString(context, Constants.NOTIFICATION_CHANNEL_ID_KEY)
        var newChannelId = oldChannelId
        if (isNew) {
            while (newChannelId == oldChannelId) {
                newChannelId = Random().nextInt(100000).toString()
            }
        }
        if (newChannelId.isEmpty()) {
            newChannelId = Random().nextInt(100000).toString()
        }
        SharedPref.writeString(context, Constants.NOTIFICATION_CHANNEL_ID_KEY, newChannelId)
        return newChannelId
    }

    fun getLaunchIntent(context: Context, message: Message?): Intent {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent!!.component
        val notificationIntent = Intent.makeRestartActivityTask(componentName)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        notificationIntent.putExtra("message", message)
        return notificationIntent
    }

    fun isResourceAvailable(context: Context?, resId: Int): Boolean {
        if (context != null) {
            try {
                return context.resources.getResourceName(resId) != null
            } catch (ignore: NotFoundException) {
                val element = Throwable().stackTrace[0]
                LogUtils.formGraylogModel(
                    context,
                    "e",
                    "Checking if a resource is available : " + ignore.message,
                    element.className + "/" + element.methodName + "/" + element.lineNumber
                )
            }
        }
        return false
    }

    fun getAppLabel(pContext: Context, defaultText: String?): String {
        val lPackageManager = pContext.packageManager
        var lApplicationInfo: ApplicationInfo? = null
        try {
            lApplicationInfo =
                lPackageManager.getApplicationInfo(pContext.applicationInfo.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            val element = Throwable().stackTrace[0]
            LogUtils.formGraylogModel(
                pContext,
                "e",
                "Getting application info : " + e.message,
                element.className + "/" + element.methodName + "/" + element.lineNumber
            )
            e.printStackTrace()
        }
        return (if (lApplicationInfo != null) lPackageManager.getApplicationLabel(lApplicationInfo) else defaultText) as String
    }

    fun getSound(context: Context, sound: String?): Uri {
        val id = context.resources.getIdentifier(sound, "raw", context.packageName)
        return if (id != 0) {
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + id)
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
    }

    fun getApplicationName(context: Context): String {
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
            stringId
        )
    }

    fun isAnImage(url: String?): Boolean {
        var result = true
        val splitArr: Array<String>
        val extension: String
        try {
            if (!url.isNullOrEmpty()) {
                splitArr = url.split("\\.").toTypedArray()
                extension = splitArr[splitArr.size - 1]
                if (extension.lowercase(Locale.ROOT) != "jpg" &&
                    extension.lowercase(Locale.ROOT) != "png"
                ) {
                    result = false
                }
            }
        } catch (e: Exception) {
            Log.w("isAnImage", "Could not get the extension from url string!")
        }
        return result
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun createSpinToWinCustomFontFiles(context: Context, jsonStr: String?): ArrayList<String?>? {
        var result: ArrayList<String?>? = null
        var spinToWinModel: SpinToWin?
        var extendedProps: SpinToWinExtendedProps?
        val baseUrlPath = "file://" + context.filesDir.absolutePath + "/"
        var htmlStr = ""
        try {
            spinToWinModel = Gson().fromJson(jsonStr, SpinToWin::class.java)
            extendedProps = Gson().fromJson(
                URI(spinToWinModel.actiondata!!.extendedProps).path,
                SpinToWinExtendedProps::class.java
            )
        } catch (e: java.lang.Exception) {
            Log.e("SpinToWin", "Extended properties could not be parsed properly!")
            return null
        }
        if (spinToWinModel == null || extendedProps == null) {
            return null
        }
        val displayNameFontFamily: String = extendedProps.displayNameFontFamily!!
        val titleFontFamily: String = extendedProps.titleFontFamily!!
        val textFontFamily: String = extendedProps.textFontFamily!!
        val buttonFontFamily: String = extendedProps.buttonFontFamily!!
        val promoCodeTitleFontFamily: String = extendedProps.promocodeTitleFontFamily!!
        val copyButtonFontFamily: String = extendedProps.copyButtonFontFamily!!
        val promoCodesSoldOutMessageFontFamily: String =
            extendedProps.promocodesSoldOutMessageFontFamily!!

        htmlStr = writeHtmlToFile(context)

        if (displayNameFontFamily == "custom") {
            val fontExtension = getFontNameWithExtension(
                context,
                extendedProps.displayNameCustomFontFamilyAndroid!!
            )
            if (fontExtension.isNotEmpty()) {
                writeFontToFile(
                    context,
                    extendedProps.displayNameCustomFontFamilyAndroid!!,
                    fontExtension
                )
                spinToWinModel.fontFiles.add(fontExtension)
            }
        }
        if (titleFontFamily == "custom") {
            val fontExtension =
                getFontNameWithExtension(context, extendedProps.titleCustomFontFamilyAndroid!!)
            if (fontExtension.isNotEmpty()) {
                writeFontToFile(
                    context,
                    extendedProps.titleCustomFontFamilyAndroid!!,
                    fontExtension
                )
                spinToWinModel.fontFiles.add(fontExtension)
            }
        }
        if (textFontFamily == "custom") {
            val fontExtension =
                getFontNameWithExtension(context, extendedProps.textCustomFontFamilyAndroid!!)
            if (fontExtension.isNotEmpty()) {
                writeFontToFile(
                    context,
                    extendedProps.textCustomFontFamilyAndroid!!,
                    fontExtension
                )
                spinToWinModel.fontFiles.add(fontExtension)
            }
        }
        if (buttonFontFamily == "custom") {
            val fontExtension =
                getFontNameWithExtension(context, extendedProps.buttonCustomFontFamilyAndroid!!)
            if (fontExtension.isNotEmpty()) {
                writeFontToFile(
                    context,
                    extendedProps.buttonCustomFontFamilyAndroid!!,
                    fontExtension
                )
                spinToWinModel.fontFiles.add(fontExtension)
            }
        }
        if (promoCodeTitleFontFamily == "custom") {
            val fontExtension = getFontNameWithExtension(
                context,
                extendedProps.promocodeTitleCustomFontFamilyAndroid!!
            )
            if (fontExtension.isNotEmpty()) {
                writeFontToFile(
                    context,
                    extendedProps.promocodeTitleCustomFontFamilyAndroid!!,
                    fontExtension
                )
                spinToWinModel.fontFiles.add(fontExtension)
            }
        }
        if (copyButtonFontFamily == "custom") {
            val fontExtension = getFontNameWithExtension(
                context,
                extendedProps.copyButtonCustomFontFamilyAndroid!!
            )
            if (fontExtension.isNotEmpty()) {
                writeFontToFile(
                    context,
                    extendedProps.copyButtonCustomFontFamilyAndroid!!,
                    fontExtension
                )
                spinToWinModel.fontFiles.add(fontExtension)
            }
        }
        if (promoCodesSoldOutMessageFontFamily == "custom") {
            val fontExtension = getFontNameWithExtension(
                context,
                extendedProps.promocodesSoldOutMessageCustomFontFamilyAndroid!!
            )
            if (fontExtension.isNotEmpty()) {
                writeFontToFile(
                    context,
                    extendedProps.promocodesSoldOutMessageCustomFontFamilyAndroid!!,
                    fontExtension
                )
                spinToWinModel.fontFiles.add(fontExtension)
            }
        }
        if (htmlStr.isNotEmpty()) {
            result = ArrayList()
            result.add(baseUrlPath)
            result.add(htmlStr)
            result.add(Gson().toJson(spinToWinModel, SpinToWin::class.java))
        }
        return result
    }

    private fun getFontNameWithExtension(context: Context, font: String): String {
        val value = TypedValue()
        return if (isFontResourceAvailable(context, font)) {
            val id = context.resources.getIdentifier(font, "font", context.packageName)
            context.resources.getValue(id, value, true)
            val res = value.string.toString().split("/").toTypedArray()
            res[res.size - 1]
        } else {
            ""
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun writeHtmlToFile(context: Context): String {
        val spinToWinFileName = "spintowin"
        var htmlString: String = ""
        val spintowinRelatedDigitalCacheDir = context.filesDir
        var `is`: InputStream? = null
        var fos: FileOutputStream? = null
        try {
            val htmlFile = File("$spintowinRelatedDigitalCacheDir/$spinToWinFileName.html")
            val jsFile = File("$spintowinRelatedDigitalCacheDir/$spinToWinFileName.js")
            htmlFile.createNewFile()
            jsFile.createNewFile()
            `is` = context.assets.open("$spinToWinFileName.html")
            var bytes = getBytesFromInputStream(`is`)
            `is`.close()
            htmlString = String(bytes!!, StandardCharsets.UTF_8)
            fos = FileOutputStream(htmlFile, false)
            fos.write(bytes)
            fos.close()
            `is` = context.assets.open("$spinToWinFileName.js")
            bytes = getBytesFromInputStream(`is`)
            `is`.close()
            fos = FileOutputStream(jsFile)
            fos.write(bytes)
            fos.close()
        } catch (e: java.lang.Exception) {
            Log.e("SpinToWin", "Could not create spintowin cache files properly!")
            e.printStackTrace()
            return ""
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: java.lang.Exception) {
                    Log.e("SpinToWin", "Could not close spintowin is stream properly!")
                    e.printStackTrace()
                }
            }
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: java.lang.Exception) {
                    Log.e("SpinToWin", "Could not close spintowin fos stream properly!")
                    e.printStackTrace()
                }
            }
        }
        return htmlString
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun writeFontToFile(
        context: Context,
        fontName: String,
        fontNameWithExtension: String
    ){
        val spintowinRelatedDigitalCacheDir = context.filesDir
        var `is`: InputStream? = null
        var fos: FileOutputStream? = null
        try {
            val fontFile = File("$spintowinRelatedDigitalCacheDir/$fontNameWithExtension")
            fontFile.createNewFile()
            val fontId = context.resources.getIdentifier(fontName, "font", context.packageName)
            `is` = context.resources.openRawResource(fontId)
            val bytes = getBytesFromInputStream(`is`)
            `is`.close()
            fos = FileOutputStream(fontFile)
            fos.write(bytes)
            fos.close()
        } catch (e: java.lang.Exception) {
            Log.e("SpinToWin", "Could not create spintowin cache files properly!")
            e.printStackTrace()
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: java.lang.Exception) {
                    Log.e("SpinToWin", "Could not close spintowin is stream properly!")
                    e.printStackTrace()
                }
            }
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: java.lang.Exception) {
                    Log.e("SpinToWin", "Could not close spintowin fos stream properly!")
                    e.printStackTrace()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun getBytesFromInputStream(`is`: InputStream): ByteArray? {
        val os = ByteArrayOutputStream()
        val buffer = ByteArray(0xFFFF)
        var len = `is`.read(buffer)
        while (len != -1) {
            os.write(buffer, 0, len)
            len = `is`.read(buffer)
        }
        return os.toByteArray()
    }
}