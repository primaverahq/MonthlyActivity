package com.primaverahq.monthlyactivity.evaluators

import android.graphics.Color
import com.primaverahq.monthlyactivity.ColorEvaluator

/**
 * [ColorEvaluator] that has an array of threshold values with associated colors.
 * It returns color of the threshold, if a provided value is higher, or transparent,
 * if there is no threshold that is less than the value. Thresholds must be sorted
 * by descending and the last one should be less of equal to the minimum value
 * in the displayed data.
 */
class ThresholdEvaluator(private val thresholds: Array<Pair<Int, Int>>) : ColorEvaluator() {

    override fun prepare(data: Map<Int, Int>) {}

    override fun evaluateColor(value: Int): Int {
        return thresholds.firstOrNull { value >= it.first }?.second ?: Color.TRANSPARENT
    }
}