package com.happ.coursegraph.ui.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.happ.coursegraph.R
import com.happ.coursegraph.data.Course

class CourseAdapter : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    private val courseList = mutableListOf<Course>()

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvSubjectName)
        val tvGrade: EditText = itemView.findViewById(R.id.edGrade)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courseList[position]

        holder.tvName.text = course.name

        // 기존 리스너 제거 후 텍스트 설정
        if (holder.tvGrade.tag != null) {
            (holder.tvGrade.tag as? android.text.TextWatcher)?.let {
                holder.tvGrade.removeTextChangedListener(it)
            }
        }

        holder.tvGrade.setText(course.grade)

        // 새로운 TextWatcher 설정
        val watcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                course.grade = s.toString()
                Log.i("##INFO", "grade changed = ${course.grade}")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        }

        holder.tvGrade.addTextChangedListener(watcher)
        holder.tvGrade.tag = watcher // 재사용 시 중복 리스너 제거 위해 tag에 저장
    }


    fun getCurrentList(): List<Course> {
        return courseList
    }

    override fun getItemCount(): Int = courseList.size

    fun submitList(newList: List<Course>) {
        courseList.clear()
        courseList.addAll(newList)
        notifyDataSetChanged()
    }

    fun clearAll() {
        courseList.clear()
        notifyDataSetChanged()
    }
}

