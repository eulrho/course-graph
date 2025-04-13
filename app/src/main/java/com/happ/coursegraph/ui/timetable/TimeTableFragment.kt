package com.happ.coursegraph.ui.timetable


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.happ.coursegraph.databinding.FragmentTimetableBinding

class TimetableFragment : Fragment() {

    private var _binding: FragmentTimetableBinding? = null
    private val binding get() = _binding!!

    private val selectedSubjects = mutableListOf<Subject>()
    private val subjectMap = mutableMapOf<String, Subject>()
    private var currentGrade = "1학년"

    private val subjectsByGrade = mapOf(
        "1학년" to listOf(
            Subject(
                name = "미래설계탐색",
                professor = "김교수",
                timeSlots = listOf(TimeSlot("mon", 1), TimeSlot("mon", 2), TimeSlot("mon", 3))
            ),
            Subject(
                name = "미래설계준비",
                professor = "이교수",
                timeSlots = listOf(TimeSlot("tue", 2), TimeSlot("tue", 3))
            ),
            Subject(
                name = "오픈소스소프트웨어 이해와 실습",
                professor = "박교수",
                timeSlots = listOf(TimeSlot("wed", 1), TimeSlot("wed", 2))
            )
        ),
        "2학년" to emptyList(),
        "3학년" to emptyList(),
        "4학년" to emptyList()
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimetableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_multiple_choice)
        binding.listSubjects.adapter = adapter
        binding.listSubjects.choiceMode = ListView.CHOICE_MODE_MULTIPLE

// 리스트 항목 클릭 시 바로 반영
        binding.listSubjects.setOnItemClickListener { _, _, position, _ ->
            val name = adapter.getItem(position)
            val subject = name?.let { subjectMap[it] }

            subject?.let {
                // 이미 선택돼 있다면 -> 제거
                if (selectedSubjects.contains(subject)) {
                    selectedSubjects.remove(subject)
                } else {
                    selectedSubjects.add(subject)
                }

                // 즉시 반영
                applySubjectsToTimetable(selectedSubjects)
            }
        }


        // + 버튼 → 과목 리스트 보이기
        binding.imCreate.setOnClickListener {
            binding.apply {
                layoutSubjectList.visibility = View.VISIBLE
                layoutHeaderOptions.visibility = View.VISIBLE
                layoutHeaderDefault.visibility = View.GONE
            }
            updateSubjects(currentGrade, adapter)
        }

        // 닫기 버튼
        binding.btnCloseSubjectList.setOnClickListener {
            binding.apply {
                layoutSubjectList.visibility = View.GONE
                layoutHeaderOptions.visibility = View.GONE
                layoutHeaderDefault.visibility = View.VISIBLE
            }
            clearTimetable()
        }

        // 학년 선택 버튼
        binding.btnGrade1.setOnClickListener {
            updateSubjects("1학년", adapter)
        }
        binding.btnGrade2.setOnClickListener {
            updateSubjects("2학년", adapter)
        }
        binding.btnGrade3.setOnClickListener {
            updateSubjects("3학년", adapter)
        }
        binding.btnGrade4.setOnClickListener {
            updateSubjects("4학년", adapter)
        }
        
        binding.btApply.setOnClickListener {
            // TODO: 과목 서버 업로드
            binding.apply {
                layoutSubjectList.visibility = View.GONE
                layoutHeaderOptions.visibility = View.GONE
                layoutHeaderDefault.visibility = View.VISIBLE
            }
        }


    }

    private fun updateSubjects(grade: String, adapter: ArrayAdapter<String>) {
        currentGrade = grade
        val subjectList = subjectsByGrade[grade] ?: emptyList()
        adapter.clear()
        subjectMap.clear()
        for (subject in subjectList) {
            adapter.add(subject.name)
            subjectMap[subject.name] = subject
        }
    }

    private fun applySubjectsToTimetable(subjects: List<Subject>) {
        clearTimetable()
        for (subject in subjects) {
            for (slot in subject.timeSlots) {
                val cellId = resources.getIdentifier(
                    "cell_${slot.day}_${slot.period}",
                    "id",
                    requireContext().packageName
                )
                val cell = view?.findViewById<TextView>(cellId)
                cell?.apply {
                    text = subject.name
                    setBackgroundColor(Color.parseColor("#B2EBF2"))
                }
            }
        }
    }

    private fun clearTimetable() {
        val days = listOf("mon", "tue", "wed", "thu", "fri")
        for (day in days) {
            for (period in 1..9) {
                val cellId = resources.getIdentifier(
                    "cell_${day}_${period}",
                    "id",
                    requireContext().packageName
                )
                val cell = view?.findViewById<TextView>(cellId)
                cell?.apply {

                    text = ""
                    setBackgroundColor(Color.WHITE)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}