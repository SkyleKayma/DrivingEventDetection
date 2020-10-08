package fr.openium.testdrivingdistraction.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import fr.openium.testdrivingdistraction.BuildConfig
import okio.buffer
import okio.sink
import org.zeroturnaround.zip.ZipUtil
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset

/**
 * Created by lgodart on 13/08/2018.
 */
class ShareUtils(private val context: Context) {

    companion object {
        private const val TAG = "ShareUtils"

        private const val SHARE_DIR = "share"
        private const val IMPORT_DIR = "import"
    }

    fun getShareDir(): File =
        File(context.cacheDir, SHARE_DIR).apply {
            mkdir()
        }

    fun getImportDir(): File =
        File(context.cacheDir, IMPORT_DIR).apply {
            mkdir()
        }

    fun copyFile(uri: Uri): File? {
        val file = File(getImportDir(), "import_file.json")

        return try {
            val ins = context.contentResolver.openInputStream(uri)!!
            val out = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (ins.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            out.close()
            ins.close()

            file
        } catch (e: Exception) {
            Log.e(TAG, "Error getting file $e")
            null
        }
    }

    fun getExportSingleIntent(fileName: String, data: String): Intent {
        val tripFile = getJsonFile(fileName, data)

        return Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", tripFile)
            putExtra(Intent.EXTRA_STREAM, uri)
        }
    }

    private fun getJsonFile(fileName: String, data: String): File =
        File(getShareDir(), fileName).apply {
            sink().buffer().use { buffer ->
                buffer.writeString(data, Charset.defaultCharset())
                buffer.flush()
            }
        }

    fun getExportMultiIntent(data: List<Pair<String, String>>): Intent {
        val zipFile = getZipFile(data)

        return Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            zipFile?.let {
                val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", zipFile)
                putExtra(Intent.EXTRA_STREAM, uri)
            }
        }
    }

    private fun getZipFile(data: List<Pair<String, String>>): File? {
        var zipFile: File? = null

        val filesList = mutableListOf<File>()

        data.forEach {
            filesList.add(getJsonFile(it.first, it.second))
        }

        if (filesList.isNotEmpty()) {
            zipFile = File(getShareDir(), "Trips.zip")
            if (zipFile.exists()) {
                zipFile.delete()
            }
            ZipUtil.packEntries(filesList.toTypedArray(), zipFile)
        } else Log.e(TAG, "No trips found")

        return zipFile
    }
}