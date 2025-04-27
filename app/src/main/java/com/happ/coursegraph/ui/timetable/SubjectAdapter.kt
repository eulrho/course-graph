package com.happ.coursegraph.ui.timetable

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.happ.coursegraph.R

class SubjectAdapter(
    context: Context,
    private val items: List<ScheduleSubject>,
    private val selectedMap: MutableMap<String, Boolean>,
    private val onCheckedChange: (ScheduleSubject, Boolean) -> Unit
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.list_item_subject, parent, false)

        val subject = items[position]
        val tvName = view.findViewById<TextView>(R.id.tv_subject_name)
        val tvDetail = view.findViewById<TextView>(R.id.tv_subject_detail)
        val checkBox = view.findViewById<CheckBox>(R.id.cb_select)

        tvName.text = subject.name

        val timeText = subject.classroomList.joinToString(" / ") { it.time }
        tvDetail.text = "${subject.professor} / $timeText / ${subject.type}"

        checkBox.setOnCheckedChangeListener(null) // 리스너 초기화
        checkBox.isChecked = selectedMap[subject.name] == true // ✔ 체크 상태 설정

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            selectedMap[subject.name] = isChecked
            subject.status = if (isChecked) "ADD" else "DELETE" // ✔ 상태 설정
            Log.i("##INFO", " subject.status = ${subject.status}")
            onCheckedChange(subject, isChecked)
        }

        return view
    }
}
