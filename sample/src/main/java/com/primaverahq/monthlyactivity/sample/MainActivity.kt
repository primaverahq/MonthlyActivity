package com.primaverahq.monthlyactivity.sample

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.primaverahq.monthlyactivity.ColorEvaluator
import com.primaverahq.monthlyactivity.MonthlyActivityView
import com.primaverahq.monthlyactivity.evaluators.LinearAlphaEvaluator
import com.primaverahq.monthlyactivity.evaluators.ThresholdEvaluator
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val random = Random(Date().time)

    private val year = 2019
    private val month = 11

    private val data = (1..31).map { it to random.nextInt(100) }.toMap()

    private val listener = { x: Int, y: Int, day: Int ->
        Toast.makeText(
            this@MainActivity,
            "Column: $x, Row: $y, Day: $day, Data: ${data[day]}",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView1(view1)
        initView2(view2)
        initView3(view3)
    }

    private fun initView1(view: MonthlyActivityView) {
        val thresholds = arrayOf(
            95 to Color.parseColor("#196127"),
            85 to Color.parseColor("#239a3b"),
            70 to Color.parseColor("#7bc96f"),
            50 to Color.parseColor("#c6e48b"),
            0 to Color.parseColor("#ebedf0")
        )
        view.colorEvaluator = ThresholdEvaluator(thresholds)
        view.onTileClickListener = listener
        view.setData(year, month, data)
    }

    private fun initView2(view: MonthlyActivityView) {
        val baseColor = Color.parseColor("#ee5454")
        view.colorEvaluator = LinearAlphaEvaluator(baseColor)
        view.onTileClickListener = listener
        view.setData(year, month, data)
    }

    private fun initView3(view: MonthlyActivityView) {
        view.colorEvaluator = object : ColorEvaluator() {

            fun randomColorPart(value: Int) = value * random.nextInt(255) / 100

            override fun evaluateColor(value: Int): Int {
                return Color.argb(
                    value * 255 / 100,
                    randomColorPart(value),
                    randomColorPart(value),
                    randomColorPart(value)
                )
            }
        }
        view.onTileClickListener = listener
        view.setData(year, month, data)
    }
}
