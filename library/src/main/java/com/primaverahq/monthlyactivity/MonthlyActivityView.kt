package com.primaverahq.monthlyactivity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.primaverahq.monthlyactivity.utils.*
import java.text.DateFormatSymbols
import java.util.*
import kotlin.math.ceil
import kotlin.math.roundToInt

typealias OnTileClickListener = (x: Int, y: Int, day: Int) -> Unit

class MonthlyActivityView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * Main [Calendar] used in all calculations. It is possible
     * to change the year and the month of this calendar
     * through [setData] methods but other fields are supposed to never be changed.
     */
    private val calendar = Calendar.getInstance().atStartOfMonthAndDay()

    /**
     * View data as a [Map] of day of month to a value.
     */
    private var data: Map<Int, Int> = emptyMap()

    /**
     * Days of week titles.
     */
    private val daysOfWeekTitles: Array<String> = weekDaysTitles()

    /**
     * [PointF]s of origin of days of week titles.
     * They will be drawn in the center of their titles.
     */
    private var daysOfWeekTitleTextOrigins = Array(7) { PointF() }

    /**
     * Tile [Rect] for drawing.
     */
    private val tileRect: Rect = Rect()

    /**
     * [Paint] to draw day tiles. Initially does not have assigned color
     * that is later assigned by [ColorEvaluator].
     */
    private val tilePaint = Paint().apply {
        isAntiAlias = false
        style = Paint.Style.FILL
    }

    /**
     * [TextPaint] to draw days of week title tiles above day tiles.
     */
    private val textPaint = TextPaint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL_AND_STROKE
    }


    /**
     * Pixel size of a day tile. Although invisible,
     * days of week titles tiles have the same size.
     */
    var tileSize: Int = dpToPixel(DEFAULT_SIZE, context.resources.displayMetrics)
        set(value) {
            field = value
            requestLayout()
        }

    /**
     * Pixel size of space between day tiles horizontally
     * and vertically. Although invisible, days of week
     * titles tiles have the same horizontal space between them,
     * and also the same vertical space between them and day tiles.
     */
    var tileSpacing: Int = dpToPixel(DEFAULT_SPACING, context.resources.displayMetrics)
        set(value) {
            field = value
            requestLayout()
        }

    /**
     * [ColorEvaluator] to set day tiles color. Does not affect
     * days of week titles tiles. If not set no tiles will be drawn.
     */
    var colorEvaluator: ColorEvaluator? = null

    /**
     * [OnTileClickListener] to be called when user taps on an individual day tile.
     * Gives access to day of week number and week number of the displayed month,
     * and also the number of day of the month.
     */
    var onTileClickListener: OnTileClickListener? = null


    init {
        setWillNotDraw(false)

        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.MonthlyActivityView,
            0,
            R.style.MonthActivityView
        )
        tileSize =
            a.getDimensionPixelSize(R.styleable.MonthlyActivityView_tile_size, tileSize)
        tileSpacing =
            a.getDimensionPixelSize(R.styleable.MonthlyActivityView_tile_spacing, tileSpacing)
        textPaint.color =
            a.getColor(R.styleable.MonthlyActivityView_title_textColor, textPaint.color)
        textPaint.textSize =
            a.getDimension(R.styleable.MonthlyActivityView_title_textSize, textPaint.textSize)
        if (a.hasValue(R.styleable.MonthlyActivityView_title_fontFamily)) {
            textPaint.typeface =
                ResourcesCompat.getFont(
                    context,
                    a.getResourceId(R.styleable.MonthlyActivityView_title_fontFamily, 0)
                )
        }

        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            measureDimension(suggestedMinimumWidth, widthMeasureSpec, calendar.daysOfWeek()),
            measureDimension(suggestedMinimumHeight, heightMeasureSpec, calculateLines())
        )
        daysOfWeekTitles.forEachIndexed(::calculateTextOrigin)
    }

    private fun measureDimension(minimumSize: Int, spec: Int, count: Int): Int {
        val specSize = MeasureSpec.getSize(spec)
        return when (MeasureSpec.getMode(spec)) {
            MeasureSpec.EXACTLY -> {
                //parent forces the size, so adjust tileSize
                val newTileSize = (specSize - (count - 1) * tileSpacing) / count
                if (newTileSize < tileSize) {
                    tileSize = newTileSize
                    Log.w(
                        LOG_TAG,
                        "Tile size is too large to fit into required " +
                                "view size, this may be an error"
                    )
                }
                specSize
            }
            MeasureSpec.UNSPECIFIED -> {
                count * tileSize + (count - 1) * tileSpacing
            }
            MeasureSpec.AT_MOST -> {
                val calculatedSize = count * tileSize + (count - 1) * tileSpacing
                if (calculatedSize < minimumSize) {
                    val newTileSize = specSize - (count - 1) * tileSpacing
                    if (newTileSize < tileSize) {
                        tileSize = newTileSize
                    }
                    minimumSize
                } else {
                    calculatedSize
                }
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun onDraw(canvas: Canvas) {
        daysOfWeekTitles.forEachIndexed { index, name ->
            val origin = daysOfWeekTitleTextOrigins[index]
            canvas.drawText(name, origin.x, origin.y, textPaint)
        }
        colorEvaluator?.let {
            val skip = calendar.daysOfWeekBeforeFirstOfMonth()
            for (day in skip until calendar.daysOfMonth() + skip) {
                tilePaint.color = it.colors[day - skip + 1] ?: continue
                // One line is skipped to account for days of week titles
                val line = day / calendar.daysOfWeek() + 1
                val item = day % calendar.daysOfWeek()
                val left = item * (tileSpacing + tileSize)
                val top = line * (tileSpacing + tileSize)
                tileRect.set(left, top, left + tileSize, top + tileSize)
                canvas.drawRect(tileRect, tilePaint)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> true
            MotionEvent.ACTION_UP -> {
                onTileClickListener?.let { listener ->
                    val coords = MotionEvent.PointerCoords()
                        .apply { event.getPointerCoords(0, this) }
                    val x = (coords.x / (tileSpacing + tileSize)).toInt()
                    // One line is skipped to account for days of week titles
                    val y = (coords.y / (tileSpacing + tileSize)).toInt() - 1
                    val position = y * calendar.daysOfWeek() + x
                    val skip = calendar.daysOfWeekBeforeFirstOfMonth()
                    // There may be days of the previous and the next month
                    // on the view and they are not clickable
                    if (position >= skip && position < calendar.daysOfMonth() + skip) {
                        // Days are counted from 1
                        listener.invoke(x, y, position - skip + 1)
                    }
                    true
                } ?: false
            }
            else -> super.onTouchEvent(event)
        }
    }

    /**
     * Load the data into this view. Keys are days of month
     * and values are actual values. Note that following [Calendar] rules,
     * days are counted from 1 and months are counted from 0.
     */
    fun setData(year: Int, month: Int, data: Map<Int, Int>) {
        calendar.set(year, month, 1)
        this.data = data
        colorEvaluator?.evaluate(data)
        requestLayout()
    }

    fun setData(calendar: Calendar, data: Map<Int, Int>) {
        setData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), data)
    }

    private fun calculateTextOrigin(position: Int, text: String) {
        textPaint.getTextBounds(text, 0, text.length, tileRect)
        // Text should be drawn right in the middle of an invisible tile
        val x = (tileSize - tileRect.right) / 2 + tileSize * position + tileSpacing * position
        val y = (tileSize - tileRect.top) / 2 + tileSpacing
        daysOfWeekTitleTextOrigins[position].set(x.toFloat(), y.toFloat())
    }

    private fun calculateLines(): Int {
        val daysToShow = calendar.daysOfWeekBeforeFirstOfMonth() + calendar.daysOfMonth()
        // One line is added to account for days of week titles
        return ceil(daysToShow.toDouble() / calendar.daysOfWeek()).roundToInt() + 1
    }

    @SuppressLint("DefaultLocale")
    private fun weekDaysTitles(): Array<String> {
        val names = DateFormatSymbols.getInstance().shortWeekdays.drop(1)
        val list = mutableListOf<String>()
        for (index in calendar.firstDayOfWeek until calendar.daysOfWeek() + calendar.firstDayOfWeek) {
            list.add(names[(index - 1) % calendar.daysOfWeek()].capitalize())
        }
        return list.toTypedArray()
    }

    companion object {
        private const val LOG_TAG = "MonthlyActivityView"

        private const val DEFAULT_SIZE = 24
        private const val DEFAULT_SPACING = 2
    }
}