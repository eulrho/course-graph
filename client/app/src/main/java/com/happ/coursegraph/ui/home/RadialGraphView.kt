package com.happ.coursegraph.ui.home

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.*
import kotlin.random.Random

class RadialGraphView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val categoryColors = mutableMapOf<String, Int>()
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
        val status: String, // 추가됨!
        var x: Float? = null,
        var y: Float? = null
    )

    private var selectedCategory: String? = null
    private val courseMap: MutableMap<Int, MutableList<Course>> = mutableMapOf()

    fun setCourses(data: List<Course>) {
        courseMap.clear()

        // 기존 데이터 정리
        categoryColors.clear()

        // 새로운 데이터 세팅
        data.groupBy { it.grade }.forEach { (grade, courses) ->
            courseMap[grade] = courses.toMutableList()

            // 트랙 별로 랜덤 색상 지정
            courses.forEach { course ->
                if (course.category.isNotEmpty() && course.category !in categoryColors) {
                    categoryColors[course.category] = generateRandomColor()
                }
            }
        }
        invalidate()
    }

    private fun generateRandomColor(): Int {
        val rnd = Random(System.currentTimeMillis())
        return Color.argb(136, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)) // 88 = 반투명
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

        // 1~4학년 원 그리기
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

                // --- 노드 색 설정 ---
                paintNode.color = when {
                    course.status == "TAKEN" -> Color.parseColor("#4488FF") // TAKEN은 항상 파란색
                    selectedCategory == null -> Color.LTGRAY                  // 선택 안 했으면 회색
                    course.category.trim() == selectedCategory?.trim() -> categoryColors[course.category.trim()] ?: Color.LTGRAY
                    else -> Color.LTGRAY
                }

                canvas.drawCircle(x, y, 40f, paintNode)

                // --- 텍스트 색 설정 ---
                paintText.color = when {
                    course.status == "TAKEN" -> Color.BLACK   // TAKEN이면 텍스트도 검정
                    selectedCategory != null && course.category.trim() == selectedCategory?.trim() -> Color.BLACK
                    else -> Color.DKGRAY
                }
                paintText.textSize = 18f

                val words = course.name.chunked(6)
                words.take(2).forEachIndexed { i, line ->
                    canvas.drawText(line, x, y + (i * 18f), paintText)
                }
            }
        }
    }


}
