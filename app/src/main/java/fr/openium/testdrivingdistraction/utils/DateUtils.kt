package fr.openium.testdrivingdistraction.utils

import android.content.Context
import android.util.Log
import fr.openium.testdrivingdistraction.R
import java.text.SimpleDateFormat
import java.util.*

class DateUtils(context: Context) {

    companion object {
        private const val TAG = "DateUtils"
    }

    private var dateFullFormat: SimpleDateFormat =
        SimpleDateFormat(context.getString(R.string.generic_date_full_format), Locale.getDefault())

    private var dateFullTextFormat: SimpleDateFormat =
        SimpleDateFormat(context.getString(R.string.generic_date_full_text_format), Locale.getDefault())

    private var dateShortTextFormat: SimpleDateFormat =
        SimpleDateFormat(context.getString(R.string.generic_date_short_text_format), Locale.getDefault())

    private var dateOnlyDateTextFormat: SimpleDateFormat =
        SimpleDateFormat(context.getString(R.string.generic_date_only_date_text_format), Locale.getDefault())

    private var dateOnlyTimeToTimeTextFormat: SimpleDateFormat =
        SimpleDateFormat(context.getString(R.string.generic_date_only_time_text_format), Locale.getDefault())

    fun format(time: Long, format: Format): String =
        if (time > 0L) {
            when (format) {
                Format.DATE_FULL ->
                    dateFullFormat
                Format.DATE_FULL_TEXT ->
                    dateFullTextFormat
                Format.DATE_SHORT_TEXT ->
                    dateShortTextFormat
                Format.DATE_ONLY_DATE_TEXT ->
                    dateOnlyDateTextFormat
                Format.DATE_ONLY_TIME_TEXT ->
                    dateOnlyTimeToTimeTextFormat
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
                Format.DATE_ONLY_DATE_TEXT ->
                    dateOnlyDateTextFormat
                Format.DATE_ONLY_TIME_TEXT ->
                    dateOnlyTimeToTimeTextFormat
            }.parse(date)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing date $e")
            null
        }

    fun formatThenParse(time: Long, formatFormat: Format, parseFormat: Format) =
        parse(format(time, formatFormat), parseFormat)

    fun parseThenFormat(date: String, parseFormat: Format, formatFormat: Format) =
        format(parse(date, parseFormat)?.time ?: 0L, formatFormat)

    enum class Format {
        DATE_FULL,
        DATE_FULL_TEXT,
        DATE_SHORT_TEXT,
        DATE_ONLY_DATE_TEXT,
        DATE_ONLY_TIME_TEXT
    }
}