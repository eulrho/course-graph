package com.happ.coursegraph.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.happ.coursegraph.R
import com.happ.coursegraph.data.Course

class CourseAdapter : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    private val courseList = mutableListOf<Course>()

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvSubjectName)
        val tvGrade: TextView = itemView.findViewById(R.id.tvGrade)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courseList[position]
        holder.tvName.text = course.name
        holder.tvGrade.text = course.grade
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

