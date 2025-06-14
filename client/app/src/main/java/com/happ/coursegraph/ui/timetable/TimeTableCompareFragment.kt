package com.happ.coursegraph.ui.timetable

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.happ.coursegraph.MainActivity
import com.happ.coursegraph.R
import com.happ.coursegraph.databinding.FragmentTimetableCompareBinding

class TimeTableCompareFragment : Fragment() {
    private var _binding: FragmentTimetableCompareBinding? = null
    private val binding get() = _binding!!

    private var grade: String? = null
    private var targetCredit: Int = 0

    private val timeViewmodel: TimeTableViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimetableCompareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setEvent()
    }

    private fun init() {
        arguments?.let {
            grade = it.getString("grade")
            targetCredit = it.getInt("targetCredit")
        }
        Log.i("##INFO", "grade= ${grade}  // targetCredit = ${targetCredit} ")

        grade?.let { timeViewmodel.postRecommendSchedule(it, targetCredit) }
    }

    private fun setEvent() {
        observedViewModel()

        binding.imCancel.setOnClickListener {
            Log.i("##INFO", "??? ")
            val bundle = Bundle().apply {
                putBoolean("isExist", false)
            }

            findNavController().navigate(
                R.id.action_navigation_timetable_compare_to_navigation_timetalbe,
                bundle
            )

            (requireActivity() as MainActivity).setVisibilityHideButton(true)
            (requireActivity() as MainActivity).setVisibilityBottomNav(true)
        }

        binding.cbOne.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val selectedSchedule = timeViewmodel.recommendSchedule.value?.getOrNull(0)
                selectedSchedule?.let {
                    val subjects = it.schedule.map { recommend ->
                        val timeSlots = recommend.timeList.flatMap { timeStr ->
                            val parts = timeStr.trim().split(" ", limit = 2)
                            if (parts.size < 2) return@flatMap emptyList()
                            val day = convertDayToEng(parts[0])
                            val periods = parts[1].split(",").mapNotNull { it.trim().toIntOrNull() }
                            periods.map { period -> TimeSlot(day, period) }
                        }
                        Subject(recommend.name, timeSlots)
                    }

                    // ViewModel에 반영
                    timeViewmodel.applyRecommendedSubjects(subjects)

                    val bundle = Bundle().apply {
                        putBoolean("isExist", true)
                    }

                    findNavController().navigate(
                        R.id.action_navigation_timetable_compare_to_navigation_timetalbe,
                        bundle
                    )
                    (requireActivity() as MainActivity).setVisibilityHideButton(true)
                    (requireActivity() as MainActivity).setVisibilityBottomNav(true)
                }
            }
        }

        binding.cbTwo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val selectedSchedule = timeViewmodel.recommendSchedule.value?.getOrNull(1)
                selectedSchedule?.let {
                    val subjects = it.schedule.map { recommend ->
                        val timeSlots = recommend.timeList.flatMap { timeStr ->
                            val parts = timeStr.split(" ")
                            if (parts.size < 2) return@flatMap emptyList()
                            val day = convertDayToEng(parts[0])
                            val periods = parts[1].split(",").mapNotNull { it.trim().toIntOrNull() }
                            periods.map { period -> TimeSlot(day, period) }
                        }
                        Subject(recommend.name, timeSlots)
                    }

                    timeViewmodel.applyRecommendedSubjects(subjects)

                    val bundle = Bundle().apply {
                        putBoolean("isExist", true)
                    }

                    findNavController().navigate(
                        R.id.action_navigation_timetable_compare_to_navigation_timetalbe,
                        bundle
                    )
                    (requireActivity() as MainActivity).setVisibilityHideButton(true)
                    (requireActivity() as MainActivity).setVisibilityBottomNav(true)
                }
            }
        }


    }

    private fun observedViewModel() {
        timeViewmodel.recommendSchedule.observe(viewLifecycleOwner) { recommendList ->
            recommendList.getOrNull(0)?.let { result1 ->
                val subjects1 = convertToSubjects(result1.schedule + result1.generalSubjects)
                applyRecommendedSchedule(subjects1, "")
                binding.tvScoreOne.text = "${result1.totalCredit}학점"
            }

            recommendList.getOrNull(1)?.let { result2 ->
                val subjects2 = convertToSubjects(result2.schedule + result2.generalSubjects)
                applyRecommendedSchedule(subjects2, "_two")
                binding.tvScoreTwo.text = "${result2.totalCredit}학점"
            }
        }
    }


    private fun convertToSubjects(schedule: List<RecommendSubject>): List<Subject> {
        return schedule.map { recommend ->
            val timeSlots = recommend.timeList.flatMap { timeStr ->
                val parts = timeStr.trim().split(" ", limit = 2)
                if (parts.size < 2) return@flatMap emptyList()
                val day = convertDayToEng(parts[0])
                val periods = parts[1].split(",").mapNotNull { it.trim().toIntOrNull() }
                periods.map { period -> TimeSlot(day, period) }
            }

            Subject(recommend.name, timeSlots)
        }
    }

    private fun applyRecommendedSchedule(scheduleList: List<Subject>, suffix: String) {
        for (subject in scheduleList) {
            for (slot in subject.timeSlots) {
                val cellId = resources.getIdentifier(
                    "cell_${slot.day}_${slot.period}$suffix", // 예: cell_mon_3 / cell_mon_3_two
                    "id",
                    requireContext().packageName
                )

                val cell = view?.findViewById<View>(cellId) as? TextView
                cell?.apply {
                    text = subject.name
                    setBackgroundColor(android.graphics.Color.parseColor("#B2DFDB")) // 민트색
                } ?: Log.w("##WARN", "Cell not found: cell_${slot.day}_${slot.period}$suffix")
            }
        }
    }

    private fun convertDayToEng(koreanDay: String): String {
        return when (koreanDay) {
            "월" -> "mon"
            "화" -> "tue"
            "수" -> "wed"
            "목" -> "thu"
            "금" -> "fri"
            else -> ""
        }
    }
}