package com.happ.coursegraph.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.happ.coursegraph.MainActivity
import com.happ.coursegraph.R
import com.happ.coursegraph.data.Course
import com.happ.coursegraph.databinding.FragmentAddSubjectBinding

class AddSubjectFragment : Fragment() {

    // FIXME: 1. 이미 check 되어있는 과목들도 중복해서 반영됨  
    private var _binding: FragmentAddSubjectBinding? = null
    private val binding get() = _binding!!

    private val majorViewModel: MajorStatusViewModel by viewModels({ requireActivity() })

    private val subjectMap = mutableMapOf<String, MutableList<SubjectStatus>>() // 학년별 과목 맵
    private var currentYear = "1학년"

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
        init()
        setEvent()
    }

    private fun init() {
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_multiple_choice)
        binding.listSubjects.adapter = adapter
        binding.listSubjects.choiceMode = android.widget.ListView.CHOICE_MODE_MULTIPLE

        majorViewModel.getSubjectOfGrade("1학년")
    }

    private fun setEvent() {
        observeViewModel()

        binding.btnGrade1.setOnClickListener {
            majorViewModel.getSubjectOfGrade("1학년")
            updateSubjectList("1학년")
        }
        binding.btnGrade2.setOnClickListener {
            majorViewModel.getSubjectOfGrade("2학년")
            updateSubjectList("2학년")
        }
        binding.btnGrade3.setOnClickListener {
            majorViewModel.getSubjectOfGrade("3학년")
            updateSubjectList("3학년")
        }
        binding.btnGrade4.setOnClickListener {
            majorViewModel.getSubjectOfGrade("4학년")
            updateSubjectList("4학년")
        }

        binding.btnClose.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_subject_to_navigation_dashboard)

            (requireActivity() as MainActivity).setVisibilityBottomNav(true)
            (requireActivity() as MainActivity).setVisibilityHideButton(true)
        }

        binding.btnAddSubject.setOnClickListener {
            val checkedItems = binding.listSubjects.checkedItemPositions
            val currentSubjects = subjectMap[currentYear] ?: emptyList()

            val selectedSubjectList = mutableListOf<SubjectStatus>()

            for (i in 0 until currentSubjects.size) {
                val subject = currentSubjects[i]
                val isChecked = checkedItems.get(i, false)

                val status = if (isChecked) "TAKEN" else "NOT_TAKEN"
                selectedSubjectList.add(SubjectStatus(subject.name, currentYear, status))
            }

            // ViewModel로 전송 (서버에도 업로드)
            majorViewModel.postSubjects(selectedSubjectList)

            // 리사이클러뷰에 반영할 데이터도 추가
            val addedCourses = selectedSubjectList
                .filter { it.status == "TAKEN" }
                .map { Course(it.name, "") }

            majorViewModel.addCourses(addedCourses)


            findNavController().navigate(R.id.action_navigation_add_subject_to_navigation_dashboard)

            (requireActivity() as MainActivity).setVisibilityBottomNav(true)
            (requireActivity() as MainActivity).setVisibilityHideButton(true)
        }

    }

    private fun observeViewModel() {
        majorViewModel.subjectStatusList.observe(viewLifecycleOwner) { list ->
            // 학년별로 분류하여 Map에 저장
            subjectMap.clear()
            list.forEach {
                val grade = it.grade
                if (subjectMap[grade] == null) {
                    subjectMap[grade] = mutableListOf()
                }
                subjectMap[grade]?.add(it)
            }

            updateSubjectList(currentYear) // 초기 로딩 시 학년별 과목 표시
        }

        majorViewModel.isChanged.observe(viewLifecycleOwner) { res ->
            if (res == true) {
                parentFragmentManager.beginTransaction()
                    .remove(this@AddSubjectFragment)
                    .commit()
            } else {
                Toast.makeText(
                    requireContext(),
                    "과목 추가 실패",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateSubjectList(year: String) {
        currentYear = year
        val subjects = subjectMap[year] ?: emptyList()

        adapter.clear()
        adapter.addAll(subjects.map { it.name })

        // 체크 상태는 status가 TAKEN이면 체크되도록 설정
        for (i in subjects.indices) {
            val isChecked = subjects[i].status == "TAKEN"
            binding.listSubjects.setItemChecked(i, isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

