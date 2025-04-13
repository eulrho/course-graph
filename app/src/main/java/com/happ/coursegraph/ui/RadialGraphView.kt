package com.happ.coursegraph.ui

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
        var x: Float? = null,
        var y: Float? = null
    )

    private val categoryColors = mapOf(
        "전공필수" to Color.BLACK,
        "임베디드 IoT" to Color.parseColor("#8A2BE2"),
        "지능형 IoT" to Color.parseColor("#4682B4"),
        "마이크로디그리" to Color.parseColor("#FFD700"),
        "지능형 시스템" to Color.parseColor("#FF4500")
    )

    private var selectedCategory: String? = null

    private val courseMap: MutableMap<Int, MutableList<Course>> = mutableMapOf(
        1 to mutableListOf(
            Course("무선통신망", "임베디드 IoT"),
            Course("IT소자", "지능형 IoT"),
            Course("IT소자", "지능형 IoT"),
            Course("IT소자", "지능형 IoT"),
            Course("IT소자", "지능형 IoT")
        ),
        2 to mutableListOf(
            Course("디지털 회로설계", "임베디드 IoT"),
            Course("임베디드 시스템", "임베디드 IoT"),
            Course("임베디드 시스템", "임베디드 IoT"),
            Course("임베디드 시스템", "임베디드 IoT"),
            Course("임베디드 시스템", "임베디드 IoT"),
            Course("임베디드 시스템", "임베디드 IoT"),
            Course("임베디드 시스템", "임베디드 IoT"),
            Course("임베디드 시스템", "임베디드 IoT"),
            Course("임베디드 시스템", "임베디드 IoT"),
            Course("임베디드 시스템", "임베디드 IoT"),
            Course("임베디드 시스템", "임베디드 IoT"),
            Course("임베디드 시스템", "임베디드 IoT"),
        ),
        3 to mutableListOf(
            Course("오픈소스", "전공필수"),
            Course("데이터 분석", "지능형 시스템"),
            Course("데이터 분석", "지능형 시스템"),
            Course("데이터 분석", "지능형 시스템")
        ),
        4 to mutableListOf(
            Course("캡스톤디자인", "전공필수"),
            Course("인공지능 응용", "마이크로디그리"),
            Course("인공지능 응용", "마이크로디그리"),
            Course("인공지능 응용", "마이크로디그리"),
            Course("인공지능 응용", "마이크로디그리")
        )
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val maxRadius = min(width, height) * 0.4f
        val radiusStep = maxRadius / 4

        for (i in 1..4) {
            canvas.drawCircle(centerX, centerY, i * radiusStep, paintCircle)
        }

        courseMap.forEach { (year, courses) ->
            val radius = year * radiusStep
            val usedPoints = mutableListOf<PointF>()

            courses.forEach { course ->
                if (course.x == null || course.y == null) {
                    var x: Float
                    var y: Float
                    var attempt = 0
                    val maxAttempts = 200

                    do {
                        val angle = Math.toRadians(Random.nextInt(0, 360).toDouble())
                        x = (centerX + radius * cos(angle)).toFloat()
                        y = (centerY + radius * sin(angle)).toFloat()
                        attempt++
                    } while (
                        usedPoints.any { pt -> hypot(pt.x - x, pt.y - y) < 85f } &&
                        attempt < maxAttempts
                    )

                    usedPoints.add(PointF(x, y))
                    course.x = x
                    course.y = y
                }

                val x = course.x ?: 0f
                val y = course.y ?: 0f
                val isInCategory = course.category.trim() == selectedCategory?.trim()

                paintNode.color = if (isInCategory) {
                    categoryColors[course.category.trim()] ?: Color.LTGRAY
                } else {
                    Color.LTGRAY
                }

                canvas.drawCircle(x, y, 40f, paintNode)

                paintText.color = if (isInCategory) Color.BLACK else Color.DKGRAY
                paintText.textSize = 20f
                canvas.drawText(course.name, x, y + 10f, paintText)
            }
        }
    }

    fun setCategory(category: String) {
        selectedCategory = category.trim()
        invalidate()
    }
}
