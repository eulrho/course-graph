package com.happ.coursegraph.ui.dashboard

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.happ.coursegraph.MainActivity
import com.happ.coursegraph.R
import com.happ.coursegraph.data.Course
import com.happ.coursegraph.databinding.FragmentMajorBinding
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

class MajorStatusFragment : Fragment() {

    private var _binding: FragmentMajorBinding? = null
    private val binding get() = _binding!!
    private lateinit var courseAdapter: CourseAdapter
    private lateinit var notTakenAdapter: ArrayAdapter<String>
    private var currentPage = 1

    private var xlsxFileToUpload: File? = null

    private val csvPickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val fileName = getFileNameFromUri(it)
                binding.tvCsvFile.text = fileName

                // 파일을 캐시에 복사만 해두고 변수에 저장
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val tempFile = File(requireContext().cacheDir, fileName)
                inputStream?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                xlsxFileToUpload = tempFile // 파일 저장만 해둠
            }
        }

    private val majorViewmodel: MajorStatusViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMajorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setEvent()
    }

    private fun init() {
        val behavior = BottomSheetBehavior.from(binding.bottomSheetLayout)
        behavior.peekHeight = 200
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        courseAdapter = CourseAdapter()
        notTakenAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)
        binding.lvIncompleteCourses.adapter = notTakenAdapter

    }

    private fun setEvent() {
        observedViewModel()
        majorViewmodel.getGraduation()
        majorViewmodel.getPagedHistoryCourses(1)

        binding.tvAddCourse.setOnClickListener {
            BottomSheetBehavior.from(binding.bottomSheetLayout).state =
                BottomSheetBehavior.STATE_EXPANDED
        }

        binding.tvUploadCsv.setOnClickListener {
            csvPickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        }

        binding.tvQuestion.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_upload_guide)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)

            dialog.findViewById<Button>(R.id.btn_close).setOnClickListener { dialog.dismiss() }
            dialog.findViewById<Button>(R.id.btn_go_site).setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://eis.cbnu.ac.kr/cbnuLogin")
                    )
                )
            }

            dialog.show()
        }

        binding.imDelete.setOnClickListener {
            val fileName = binding.tvCsvFile.text.toString()
            val baseFile = File(
                Environment.getExternalStorageDirectory(),
                "${resources.getString(R.string.app_name)}/$fileName"
            )

            if (baseFile.exists()) {
                baseFile.delete()
                Toast.makeText(requireContext(), "파일이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                binding.tvCsvFile.text = ""
                courseAdapter.clearAll()
                majorViewmodel.updateCourses(emptyList())
            } else {
                Toast.makeText(requireContext(), "파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.imUploadXlsxToServer.setOnClickListener {
            xlsxFileToUpload?.let {
                majorViewmodel.postXlsxUpload(it)
            } ?: run {
                Toast.makeText(requireContext(), "먼저 파일을 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvInput.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_dashboard_to_navigation_add_subject)

            (requireActivity() as MainActivity).setVisibilityBottomNav(false)
            (requireActivity() as MainActivity).setVisibilityHideButton(false)
        }
    }

    private fun observedViewModel() {
        majorViewmodel.majorStatus.observe(viewLifecycleOwner) { updateMajorStatusUI(it) }

        majorViewmodel.courseList.observe(viewLifecycleOwner) {
            binding.reSubjects.layoutManager = LinearLayoutManager(requireContext())
            binding.reSubjects.adapter = courseAdapter
            courseAdapter.submitList(it)
        }

        majorViewmodel.totalPages.observe(viewLifecycleOwner) { setupPagination(it) }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) return it.getString(nameIndex)
            }
        }
        return "알 수 없음.csv"
    }

    private fun setupPagination(totalPages: Int) {
        val paginationLayout = binding.paginationLayout
        paginationLayout.removeAllViews()

        for (i in 1..totalPages) {
            val pageTextView = TextView(requireContext()).apply {
                text = i.toString()
                setPadding(20, 10, 20, 10)
                setTextColor(resources.getColor(R.color.white, null))
                textSize = 13f
                setOnClickListener {
                    currentPage = i
                    majorViewmodel.getPagedHistoryCourses(i)
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(10, 0, 10, 0) }
            }
            paginationLayout.addView(pageTextView)
        }
    }

    private fun updateMajorStatusUI(data: MajorStatusData) {
        val requiredRemaining = maxOf(data.requiredMinCredit - data.totalRequiredCredit, 0)
        val electiveRemaining = maxOf(data.electiveMinCredit - data.totalElectiveCredit, 0)

        val requiredEntries = arrayListOf(
            PieEntry(data.totalRequiredCredit.toFloat(), "이수 학점"),
            PieEntry(requiredRemaining.toFloat(), "남은 학점")
        )

        val electiveEntries = arrayListOf(
            PieEntry(data.totalElectiveCredit.toFloat(), "이수 학점"),
            PieEntry(electiveRemaining.toFloat(), "남은 학점")
        )

        val commonConfig: PieDataSet.() -> Unit = {
            colors = listOf(Color.parseColor("#50A56F"), Color.parseColor("#D2D1D4"))
            valueTextSize = 12f
            valueTextColor = Color.BLACK
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String = "%.0f".format(value)
            }
        }

        binding.chartEssential.apply {
            this.data = PieData(PieDataSet(requiredEntries, "").apply(commonConfig))
            centerText = "전공 필수\n${data.totalRequiredCredit} / ${data.requiredMinCredit}"
            setEntryLabelColor(Color.BLACK)
            setCenterTextSize(12f)
            setCenterTextTypeface(Typeface.DEFAULT_BOLD)
            legend.isEnabled = false
            description.isEnabled = false
            setTouchEnabled(false)
            invalidate()
        }

        binding.chartOptional.apply {
            this.data = PieData(PieDataSet(electiveEntries, "").apply(commonConfig))
            centerText = "전공 선택\n${data.totalElectiveCredit} / ${data.electiveMinCredit}"
            setEntryLabelColor(Color.BLACK)
            setCenterTextSize(12f)
            setCenterTextTypeface(Typeface.DEFAULT_BOLD)
            legend.isEnabled = false
            description.isEnabled = false
            setTouchEnabled(false)
            invalidate()
        }

        binding.imDirectSend.setOnClickListener {
            majorViewmodel.patchHistorySubjects()
        }

        notTakenAdapter.clear()
        notTakenAdapter.addAll(data.notTakenRequiredSubjects)
        notTakenAdapter.notifyDataSetChanged()

        Log.i("##INFO", "미이수 전공 필수 : ${data.notTakenRequiredSubjects.joinToString("\n")}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
