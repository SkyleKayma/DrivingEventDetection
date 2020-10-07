package fr.openium.testdrivingdistraction.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream

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
}