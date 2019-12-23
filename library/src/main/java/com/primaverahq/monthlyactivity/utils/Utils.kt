package com.primaverahq.monthlyactivity.utils

import android.util.DisplayMetrics
import kotlin.math.roundToInt

internal fun dpToPixel(value: Int, metrics: DisplayMetrics): Int = (metrics.density * value).roundToInt()