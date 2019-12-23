package com.primaverahq.monthlyactivity

import android.graphics.Color
import androidx.annotation.ColorInt

/**
 * [ColorEvaluator] is used for day tile color calculation
 * based on the value provided.
 */
abstract class ColorEvaluator {

    internal val colors = mutableMapOf<Int, @ColorInt Int>()

    internal fun evaluate(data: Map<Int, Int>) {
        prepare(data)
        colors.putAll(data.mapValues { evaluateColor(it.value) })
    }

    /**
     * Prepare [ColorEvaluator] for calculations based on the data provided.
     */
    open fun prepare(data: Map<Int, Int>) {}

    /**
     * Calculate a color of a day based on a [value] provided.
     */
    @ColorInt
    abstract fun evaluateColor(value: Int): Int
}