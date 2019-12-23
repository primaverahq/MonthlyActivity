package com.primaverahq.monthlyactivity.utils

import java.util.*

internal fun Calendar.atStartOfMonthAndDay() = apply {
    set(Calendar.DAY_OF_MONTH, 1)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

internal fun Calendar.daysOfWeek() = getActualMaximum(Calendar.DAY_OF_WEEK)

internal fun Calendar.daysOfMonth() = getActualMaximum(Calendar.DAY_OF_MONTH)

internal fun Calendar.daysOfWeekBeforeFirstOfMonth(): Int {
    val startDayOfMonth = get(Calendar.DAY_OF_WEEK)
    return (startDayOfMonth - firstDayOfWeek + daysOfWeek()) % daysOfWeek()
}