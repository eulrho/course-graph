package com.happ.coursegraph.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.happ.coursegraph.databinding.FragmentAddSubjectBinding

class AddSubjectFragment : Fragment() {

    private var _binding: FragmentAddSubjectBinding? = null
    private val binding get() = _binding!!

    private val allSubjects = mapOf(
        "1학년" to listOf("미래설계탐색", "미래설계준비"),
        "2학년" to listOf("오픈소스소프트웨어 이해와 실습"),
        "3학년" to listOf("심화코딩", "자료구조"),
        "4학년" to listOf("AI캡스톤디자인", "졸업프로젝트")
    )
    private var currentYear = "1학년"
    private val selectedSubjects = mutableSetOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSubjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_multiple_choice)
        binding.listSubjects.adapter = adapter
        binding.listSubjects.choiceMode = android.widget.ListView.CHOICE_MODE_MULTIPLE

        // 학년 버튼
        binding.btnGrade1.setOnClickListener { updateSubjectList("1학년") }
        binding.btnGrade2.setOnClickListener { updateSubjectList("2학년") }
        binding.btnGrade3.setOnClickListener { updateSubjectList("3학년") }
        binding.btnGrade4.setOnClickListener { updateSubjectList("4학년") }

        binding.btnClose.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .remove(this@AddSubjectFragment)
                .commit()
        }

        // 과목 추가 버튼
        binding.btnAddSubject.setOnClickListener {
            val checkedItems = binding.listSubjects.checkedItemPositions
            val currentSubjects = allSubjects[currentYear] ?: emptyList()

            for (i in 0 until checkedItems.size()) {
                val position = checkedItems.keyAt(i)
                if (checkedItems.valueAt(i)) {
                    selectedSubjects.add(currentSubjects[position])
                }
            }

            Toast.makeText(requireContext(), "추가된 과목: ${selectedSubjects.joinToString()}", Toast.LENGTH_SHORT).show()
            parentFragmentManager.beginTransaction()
                .remove(this@AddSubjectFragment)
                .commit()
        }

        // 초기 과목 리스트
        updateSubjectList("1학년")
    }

    private fun updateSubjectList(year: String) {
        currentYear = year
        val subjects = allSubjects[year] ?: emptyList()
        adapter.clear()
        adapter.addAll(subjects)

        // 체크 상태 반영
        for (i in subjects.indices) {
            binding.listSubjects.setItemChecked(i, selectedSubjects.contains(subjects[i]))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
