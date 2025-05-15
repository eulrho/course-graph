package com.happ.coursegraph.ui.timetable

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.happ.coursegraph.MainActivity
import com.happ.coursegraph.R
import com.happ.coursegraph.databinding.FragmentTimetableBinding
import com.happ.coursegraph.ui.dashboard.AddSubjectFragment

class TimetableFragment : Fragment() {

    private var _binding: FragmentTimetableBinding? = null
    private val binding get() = _binding!!

    private val selectedSubjects = mutableListOf<Subject>()
    private val subjectMap = mutableMapOf<String, Subject>()
    private var currentGrade = "1학년"

    private val timeViewmodel: TimeTableViewModel by activityViewModels()

    val selectedSubjectsMap = mutableMapOf<String, Boolean>() // 선택 상태 추적
    lateinit var adapter: SubjectAdapter

    private val selectedTempSubjects = mutableListOf<Subject>() // 임시 선택된 과목
    private var originalSubjects = emptyList<Subject>() // 기존 서버에서 받은 과목

    private val grades = listOf("1학년", "2학년", "3학년", "4학년")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimetableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setEvent()
    }

    private fun init() {
        val days = listOf("월요일", "화요일", "수요일", "목요일", "금요일")
        val times = listOf(
            "08:00",
            "09:00",
            "10:00",
            "11:00",
            "12:00",
            "13:00",
            "14:00",
            "15:00",
            "16:00",
            "17:00"
        )

        val dayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, days)
        val gradeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, grades)
        val timeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, times)

        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerDay.adapter = dayAdapter
        binding.spinnerStartTime.adapter = timeAdapter
        binding.spinnerEndTime.adapter = timeAdapter
        binding.spGrade.adapter = gradeAdapter

    }

    private fun setEvent() {
        observedViewModel()

        timeViewmodel.apply {

            val isExist = arguments?.getBoolean("isExist", false)
            Log.i("##INFO", "isExist = ${isExist} ")
            if (isExist == null || isExist == false) {
                // 서버에서 시간표 데이터 요청
                getTimeTableSchedule()
            } else {

            }

//             1학년 전체과목 요청
//            getScheduleGrade("1학년")
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_multiple_choice)
        binding.listSubjects.adapter = adapter
        binding.listSubjects.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        binding.listSubjects.setOnItemClickListener { _, _, position, _ ->
            val name = adapter.getItem(position)
            val subject = name?.let { subjectMap[it] }

            subject?.let {
                if (selectedSubjects.contains(subject)) {
                    selectedSubjects.remove(subject)
                } else {
                    selectedSubjects.add(subject)
                }
                applySubjectsToTimetable(selectedSubjects)
            }
        }

        binding.imCreate.setOnClickListener {
            binding.apply {
                layoutSubjectList.visibility = View.VISIBLE
                layoutHeaderOptions.visibility = View.VISIBLE
                layoutHeaderDefault.visibility = View.GONE
            }
            updateSubjects(currentGrade, adapter)


            (requireActivity() as MainActivity).setVisibilityHideButton(false)
            (requireActivity() as MainActivity).setVisibilityBottomNav(false)
        }

        binding.btnCloseSubjectList.setOnClickListener {
            clearTimetable() // 1. 시간표 전체 초기화

            applySubjectsToTimetable(originalSubjects) // 2. 서버로 받은 기존 시간표 다시 그림
            selectedTempSubjects.clear() // 3. 임시 선택된 과목 목록 초기화

            // 4. 체크박스 상태 초기화
            selectedSubjectsMap.clear()
            for (subject in originalSubjects) {
                selectedSubjectsMap[subject.name] = true
            }
            adapter.notifyDataSetChanged()

            binding.apply {
                layoutSubjectList.visibility = View.GONE
                layoutHeaderOptions.visibility = View.GONE
                layoutHeaderDefault.visibility = View.VISIBLE
            }


            (requireActivity() as MainActivity).setVisibilityHideButton(true)
            (requireActivity() as MainActivity).setVisibilityBottomNav(true)
        }



        binding.btnCloseDirectList.setOnClickListener {
            binding.apply {
                layoutSubjectList.visibility = View.GONE
                layoutHeaderOptions.visibility = View.GONE
                layoutHeaderDirectOptions.visibility = View.GONE
                layoutRecommendCourse.visibility = View.GONE
                layoutHeaderDefault.visibility = View.VISIBLE
            }
        }

        binding.btnGrade1.setOnClickListener {
            updateSubjects("1학년", adapter)
            // 1학년 전체과목 요청
            timeViewmodel.getScheduleGrade("1학년")
        }
        binding.btnGrade2.setOnClickListener {
            updateSubjects("2학년", adapter)
            // 2학년 전체과목 요청
            timeViewmodel.getScheduleGrade("2학년")
        }
        binding.btnGrade3.setOnClickListener {
            updateSubjects("3학년", adapter)
            // 3학년 전체과목 요청
            timeViewmodel.getScheduleGrade("3학년")
        }
        binding.btnGrade4.setOnClickListener {
            updateSubjects("4학년", adapter)
            // 4학년 전체과목 요청
            timeViewmodel.getScheduleGrade("4학년")
        }

        binding.btDirectAdd.setOnClickListener {
            binding.apply {
                layoutSubjectList.visibility = View.GONE
                layoutHeaderOptions.visibility = View.GONE
                layoutHeaderDefault.visibility = View.GONE
                layoutRecommendCourse.visibility = View.VISIBLE
                layoutHeaderDirectOptions.visibility = View.VISIBLE
            }
        }

        binding.btComplete.setOnClickListener {
            val subjectName = binding.edInputCourse.text.toString()
            val selectedDay = binding.spinnerDay.selectedItem.toString() // 예: "수요일"
            val startTime = binding.spinnerStartTime.selectedItem.toString()
            val endTime = binding.spinnerEndTime.selectedItem.toString()

            val dayKor = selectedDay.removeSuffix("요일") // "수요일" -> "수"
            val startPeriod = timeToPeriod(startTime)
            val endPeriod = timeToPeriod(endTime)

            // 예: "수 03,04,05"
            val periodStr = (startPeriod until endPeriod)
                .joinToString(",") { "%02d".format(it) }
            val timeStr = "$dayKor $periodStr"

            val generalSchedule = GeneralSchedule(
                name = subjectName,
                timeList = listOf(timeStr),
                status = "ADD"
            )

            timeViewmodel.postScheduleGeneral(listOf(generalSchedule))

            // UI 초기화
            binding.apply {
                layoutSubjectList.visibility = View.GONE
                layoutHeaderOptions.visibility = View.GONE
                layoutHeaderDirectOptions.visibility = View.GONE
                layoutRecommendCourse.visibility = View.GONE
                layoutHeaderDefault.visibility = View.VISIBLE
            }

//            // 업로드 성공 후 UI 재갱신을 위한 observe
//            timeViewmodel.postScheduleResult.observe(viewLifecycleOwner) { success ->
//                if (success == true) {
//                    timeViewmodel.getTimeTableSchedule() // 서버에서 다시 받아오기
//                }
//            }
        }



        binding.btApply.setOnClickListener {
            // ① 실제 상태를 selectedSubjectsMap 기준으로 강제 정정
            timeViewmodel.scheduleGradeSubjects.value?.forEach { subject ->
                val checked = selectedSubjectsMap[subject.name] == true
                subject.status = if (checked) "ADD" else "DELETE"
            }

            // ② 시간표 화면 및 임시 상태 초기화
            originalSubjects = selectedTempSubjects.toList()
            selectedTempSubjects.clear()

            binding.apply {
                layoutSubjectList.visibility = View.GONE
                layoutHeaderOptions.visibility = View.GONE
                layoutHeaderDefault.visibility = View.VISIBLE
            }

            // ③ 서버 전송
            timeViewmodel.postSchedule()
        }

        binding.btnShowSubjects.setOnClickListener {
            (requireActivity() as MainActivity).setVisibilityHideButton(false)
            (requireActivity() as MainActivity).setVisibilityBottomNav(false)

            binding.apply {
                layoutSubjectList.visibility = View.GONE
                layoutHeaderOptions.visibility = View.GONE
                layoutHeaderDefault.visibility = View.GONE
                imCancel.visibility = View.VISIBLE
                btnShowSubjects.visibility = View.GONE
                layoutCreateRecommend.visibility = View.VISIBLE
            }
        }

        binding.imCancel.setOnClickListener {
            (requireActivity() as MainActivity).setVisibilityHideButton(true)
            (requireActivity() as MainActivity).setVisibilityBottomNav(true)

            binding.apply {
                layoutSubjectList.visibility = View.GONE
                layoutHeaderOptions.visibility = View.GONE
                layoutHeaderDefault.visibility = View.VISIBLE
                imCancel.visibility = View.GONE
                btnShowSubjects.visibility = View.VISIBLE
                layoutCreateRecommend.visibility = View.GONE
            }
        }

        binding.btCreate.setOnClickListener {
            val selectedGradePosition = binding.spGrade.selectedItemPosition
            val grade = grades[selectedGradePosition]
            val targetCredit = binding.edScore.text.toString().toInt()

            val bundle = Bundle().apply {
                putString("grade", grade)
                putInt("targetCredit", targetCredit)
            }

            val navController = findNavController()
            navController.navigate(
                R.id.action_navigation_timetalbe_to_navigation_timetable_compare,
                bundle
            )

            binding.imCancel.performClick()
            (requireActivity() as MainActivity).setVisibilityHideButton(false)
            (requireActivity() as MainActivity).setVisibilityBottomNav(false)
        }
    }

    private fun observedViewModel() {
        // 기존 시간표 observe
        timeViewmodel.subjects.observe(viewLifecycleOwner) { subjects ->
            originalSubjects = subjects
            applySubjectsToTimetable(subjects)
        }

        // 업로드 성공 후 UI 재갱신을 위한 observe
        timeViewmodel.postScheduleResult.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                timeViewmodel.getTimeTableSchedule() // 서버에서 다시 받아오기
            }
        }

        // 학년별 과목 리스트 observe
        timeViewmodel.scheduleGradeSubjects.observe(viewLifecycleOwner) { list ->
            // 기존 시간표에 있는 과목이면 자동 체크로 표시
            selectedSubjectsMap.clear()
            for (item in list) {
                val isInOriginal = originalSubjects.any { it.name == item.name }
                if (isInOriginal) {
                    selectedSubjectsMap[item.name] = true
                    item.status = "ADD" // 이 상태도 반영 (서버 업로드용)
                } else {
                    selectedSubjectsMap[item.name] = false
                }
            }

            // 어댑터 생성
            adapter =
                SubjectAdapter(requireContext(), list, selectedSubjectsMap) { subject, isChecked ->
                    val subjectData = subject.toSubject()

                    if (isChecked) {
                        selectedTempSubjects.add(subjectData)
                        applySubjectToTimetableSingle(subjectData)
                    } else {
                        subjectData.timeSlots.forEach {
                            Log.i("##INFO", "time = ${it} ")
                        }
                        selectedTempSubjects.removeIf { temp ->
                            temp.name == subjectData.name &&
                                    temp.timeSlots == subjectData.timeSlots
                        }

                        removeSubjectFromTimetable(subjectData)
                    }
                }

            binding.listSubjects.adapter = adapter
        }

        // 시간표 저장 후 재갱신
        timeViewmodel.userTimeTableUploadResult.observe(viewLifecycleOwner) {
            timeViewmodel.getTimeTableSchedule()
        }
    }


    private fun removeSubjectFromTimetable(subject: Subject) {
        for (slot in subject.timeSlots) {
            val cellId = resources.getIdentifier(
                "cell_${slot.day}_${slot.period}",
                "id",
                requireContext().packageName
            )
            val cell = view?.findViewById<TextView>(cellId)
            Log.i("##INFO", "cellId = ${cellId} ")

            // ✔ 무조건 제거 (임시 상태 반영을 위해)
            cell?.apply {
                // 기존 과목이면 지우지 않도록 조건 넣었던 부분 삭제
                text = ""
                setBackgroundColor(Color.WHITE)
            }
        }
    }

    private fun applySubjectToTimetableSingle(subject: Subject) {
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

    private fun updateSubjects(grade: String, adapter: ArrayAdapter<String>) {
        currentGrade = grade
        val subjectList = emptyList<Subject>() // subjectsByGrade 제거
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

    private fun timeToPeriod(time: String): Int {
        return when (time) {
            "08:00" -> 1
            "09:00" -> 2
            "10:00" -> 3
            "11:00" -> 4
            "12:00" -> 5
            "13:00" -> 6
            "14:00" -> 7
            "15:00" -> 8
            "16:00" -> 9
            "17:00" -> 10
            else -> -1
        }
    }

    private fun convertDayToEng(koreanDay: String): String {
        return when (koreanDay) {
            "월요일" -> "mon"
            "화요일" -> "tue"
            "수요일" -> "wed"
            "목요일" -> "thu"
            "금요일" -> "fri"
            else -> ""
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
