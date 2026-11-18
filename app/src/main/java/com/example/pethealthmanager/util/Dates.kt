package com.example.pethealthmanager.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

object Dates {

    private const val DAY_MS = 86_400_000L

    private fun calendar(millis: Long) = Calendar.getInstance().apply { timeInMillis = millis }

    fun startOfDay(millis: Long): Long = calendar(millis).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    fun today(): Long = startOfDay(System.currentTimeMillis())

    fun dateAt(year: Int, monthZeroBased: Int, day: Int): Long =
        Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, monthZeroBased)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    fun dateParts(millis: Long): Triple<Int, Int, Int> {
        val c = calendar(millis)
        return Triple(
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun timeParts(millis: Long): Pair<Int, Int> {
        val c = calendar(millis)
        return c.get(Calendar.HOUR_OF_DAY) to c.get(Calendar.MINUTE)
    }

    fun combine(dateMillis: Long, hour: Int, minute: Int): Long =
        calendar(startOfDay(dateMillis)).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    fun formatDate(millis: Long): String =
        SimpleDateFormat("d MMM yyyy", Locale.US).format(Date(millis))

    fun formatShortDate(millis: Long): String =
        SimpleDateFormat("MMM d", Locale.US).format(Date(millis))

    fun formatDateTime(millis: Long): String =
        SimpleDateFormat("d MMM yyyy · HH:mm", Locale.US).format(Date(millis))

    fun formatTime(millis: Long): String =
        SimpleDateFormat("HH:mm", Locale.US).format(Date(millis))

    fun daysUntil(target: Long): Int {
        val diff = startOfDay(target) - today()
        return (diff.toDouble() / DAY_MS).roundToInt()
    }

    fun relativeDay(millis: Long): String {
        val days = daysUntil(millis)
        return when {
            days == 0 -> "Today"
            days == 1 -> "Tomorrow"
            days == -1 -> "Yesterday"
            days > 1 -> "In $days days"
            else -> "${-days} days ago"
        }
    }

    fun dueText(nextDueAt: Long): String {
        val days = daysUntil(nextDueAt)
        return when {
            days < 0 -> "Overdue by ${-days} ${if (-days == 1) "day" else "days"}"
            days == 0 -> "Due today"
            days == 1 -> "Due tomorrow"
            else -> "Due in $days days"
        }
    }

    /** Next occurrence of a birthday or homecoming anniversary, today included. */
    fun nextAnniversary(annualMillis: Long): Long {
        val source = calendar(annualMillis)
        val now = Calendar.getInstance()
        val next = calendar(today()).apply {
            set(Calendar.YEAR, now.get(Calendar.YEAR))
            set(Calendar.MONTH, source.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, source.get(Calendar.DAY_OF_MONTH))
        }
        if (next.timeInMillis < today()) next.add(Calendar.YEAR, 1)
        return next.timeInMillis
    }

    fun ageText(birthMillis: Long): String {
        if (birthMillis > System.currentTimeMillis()) return "Not born yet"
        val now = Calendar.getInstance()
        val birth = calendar(birthMillis)
        var years = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
        var months = now.get(Calendar.MONTH) - birth.get(Calendar.MONTH)
        if (now.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH)) months--
        if (months < 0) {
            years--
            months += 12
        }
        return when {
            years <= 0 && months <= 0 -> "Newborn"
            years <= 0 -> "$months mo old"
            months == 0 -> "$years yr old"
            else -> "$years yr $months mo old"
        }
    }

    fun weightText(kg: Double): String {
        val rounded = (kg * 10).roundToInt() / 10.0
        return if (rounded % 1.0 == 0.0) {
            "${rounded.toInt()} kg"
        } else {
            String.format(Locale.US, "%.1f kg", rounded)
        }
    }

    fun amountText(value: Double?): String {
        if (value == null) return ""
        val rounded = if (value % 1.0 == 0.0) value.toInt().toString() else String.format(Locale.US, "%.1f", value)
        return rounded
    }
}
