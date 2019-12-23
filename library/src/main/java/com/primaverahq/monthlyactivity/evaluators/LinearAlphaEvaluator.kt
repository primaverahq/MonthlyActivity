package com.primaverahq.monthlyactivity.evaluators

import android.graphics.Color
import com.primaverahq.monthlyactivity.ColorEvaluator

/**
 * [ColorEvaluator] that returns [baseColor] with alpha of value
 * divided by the maximum value of the displayed data.
 */
class LinearAlphaEvaluator(private val baseColor: Int) : ColorEvaluator() {

    private var maximumValue: Int = 0

    override fun prepare(data: Map<Int, Int>) {
        maximumValue = data.maxBy { it.value }?.value ?: 0
    }

    override fun evaluateColor(value: Int): Int {
        return Color.argb(
            value * 255 / maximumValue,
            Color.red(baseColor),
            Color.green(baseColor),
            Color.blue(baseColor)
        )
    }
}