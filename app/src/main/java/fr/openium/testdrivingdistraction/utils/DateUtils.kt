package fr.openium.testdrivingdistraction.utils

import android.content.Context
import android.util.Log
import fr.openium.testdrivingdistraction.R
import java.text.SimpleDateFormat
import java.util.*

class DateUtils(private val context: Context) {

    companion object {
        private const val TAG = "DateUtils"
    }

    private var dateFullFormat: SimpleDateFormat =
        SimpleDateFormat(context.getString(R.string.generic_date_full_format), Locale.getDefault())

    private var dateFullTextFormat: SimpleDateFormat =
        SimpleDateFormat(context.getString(R.string.generic_date_full_text_format), Locale.getDefault())

    private var dateShortTextFormat: SimpleDateFormat =
        SimpleDateFormat(context.getString(R.string.generic_date_short_text_format), Locale.getDefault())

    fun format(time: Long, format: Format): String =
        if (time > 0L) {
            when (format) {
                Format.DATE_FULL ->
                    dateFullFormat
                Format.DATE_FULL_TEXT ->
                    dateFullTextFormat
                Format.DATE_SHORT_TEXT ->
                    dateShortTextFormat
            }.format(time)
        } else "-"

    fun parse(date: String, format: Format) =
        try {
            when (format) {
                Format.DATE_FULL ->
                    dateFullFormat
                Format.DATE_FULL_TEXT ->
                    dateFullTextFormat
                Format.DATE_SHORT_TEXT ->
                    dateShortTextFormat
            }.parse(date)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing date")
            null
        }

    enum class Format {
        DATE_FULL,
        DATE_FULL_TEXT,
        DATE_SHORT_TEXT
    }
}