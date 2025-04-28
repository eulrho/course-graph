package com.happ.coursegraph.ui.home

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.*
import kotlin.random.Random

class RadialGraphView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paintCircle = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val paintNode = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val paintText = Paint().apply {
        textSize = 30f
        textAlign = Paint.Align.CENTER
        color = Color.BLACK
        isAntiAlias = true
    }

    data class Course(
        val name: String,
        val category: String,
        val grade: Int,
        var x: Float? = null,
        var y: Float? = null
    )

    private val categoryColors = mapOf(
        "전공필수" to Color.parseColor("#888A2BE2"),
        "마이크로디그리" to Color.parseColor("#88FFD700"),
        "지능형시스템" to Color.parseColor("#88FF4500")
    )

    private var selectedCategory: String? = null
    private val courseMap: MutableMap<Int, MutableList<Course>> = mutableMapOf()

    fun setCourses(data: List<Course>) {
        courseMap.clear()
        data.groupBy { it.grade }.forEach { (grade, courses) ->
            courseMap[grade] = courses.toMutableList()
        }
        invalidate()
    }

    fun setCategory(category: String) {
        selectedCategory = category.trim()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val maxRadius = min(width, height) * 0.4f
        val radiusStep = maxRadius / 4

        // 원 그리기 (1학년 ~ 4학년)
        for (i in 1..4) {
            canvas.drawCircle(centerX, centerY, i * radiusStep, paintCircle)
        }

        courseMap.forEach { (year, courses) ->
            val radius = year * radiusStep
            val angleStep = 360.0 / courses.size

            courses.forEachIndexed { index, course ->
                val angle = Math.toRadians(angleStep * index)
                val x = (centerX + radius * cos(angle)).toFloat()
                val y = (centerY + radius * sin(angle)).toFloat()
                course.x = x
                course.y = y

                val isInCategory = course.category.trim() == selectedCategory?.trim()

                paintNode.color = if (isInCategory) {
                    categoryColors[course.category.trim()] ?: Color.LTGRAY
                } else {
                    Color.LTGRAY
                }

                canvas.drawCircle(x, y, 40f, paintNode)

                paintText.color = if (isInCategory) Color.BLACK else Color.DKGRAY
                paintText.textSize = 18f

                val words = course.name.chunked(6)  // 최대 6자씩 자르기
                words.take(2).forEachIndexed { i, line ->
                    canvas.drawText(line, x, y + (i * 18f), paintText)
                }
            }
        }
    }

}
