package com.happ.coursegraph.ui.dashboard

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.happ.coursegraph.R
import com.happ.coursegraph.data.Course
import com.happ.coursegraph.databinding.FragmentMajorBinding
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader

class MajorStatusFragment : Fragment() {

    private var _binding: FragmentMajorBinding? = null
    private val binding get() = _binding!!
    private var uploadedCsvFile: File? = null
    private lateinit var courseAdapter: CourseAdapter

    private val csvPickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val fileName = getFileNameFromUri(it)
                binding.tvCsvFile.text = fileName

                val parsedCourseList = parseExcel(it) // 엑셀 파싱으로 변경
                binding.reSubjects.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = courseAdapter
                }
                courseAdapter.submitList(parsedCourseList)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMajorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        init()
        setEvent()
    }

    private fun init() {
        setChart()

        val behavior = BottomSheetBehavior.from(binding.bottomSheetLayout)
        behavior.peekHeight = 200 // 이만큼만 보이게
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        courseAdapter = CourseAdapter()
    }

    private fun setEvent() {
        binding.apply {
            tvAddCourse.setOnClickListener {
                val behavior = BottomSheetBehavior.from(binding.bottomSheetLayout)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            tvUploadCsv.setOnClickListener {
                csvPickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            }

            tvQuestion.setOnClickListener {
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_upload_guide)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setCancelable(true)

                val btnClose = dialog.findViewById<Button>(R.id.btn_close)
                val btnGoto = dialog.findViewById<Button>(R.id.btn_go_site)

                btnClose.setOnClickListener {
                    dialog.dismiss()
                }

                btnGoto.setOnClickListener {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://eis.cbnu.ac.kr/cbnuLogin")
                    ) // 예시 링크
                    startActivity(intent)
                }


                dialog.show()
            }

            imDelete.setOnClickListener {
                val fileName = binding.tvCsvFile.text

                val baseFile = File(
                    Environment.getExternalStorageDirectory()
                        .absolutePath + "/" + resources.getString(R.string.app_name) + "/" + fileName
                )

                if (baseFile.exists() == true) {
                    baseFile.delete()
                    Toast.makeText(requireContext(), "파일이 삭제되었습니다.", Toast.LENGTH_SHORT)
                        .show()
                    uploadedCsvFile = null
                    tvCsvFile.setText("")
                    courseAdapter.clearAll()
                } else {
                    Toast.makeText(requireContext(), "파일이 존재하지 않습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            tvInput.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, AddSubjectFragment())
                    .commit()
            }
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var name = "알 수 없음.csv"
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }

    private fun parseCsv(uri: Uri): List<Course> {
        val result = mutableListOf<Course>()
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.useLines { lines ->
            lines.drop(1).forEach { line -> // 첫 줄은 헤더니까 생략
                val tokens = line.split(",")
                if (tokens.size == 2) {
                    val name = tokens[0].trim()
                    val grade = tokens[1].trim()
                    result.add(Course(name, grade))
                }
            }
        }

        return result
    }

    private fun parseExcel(uri: Uri): List<Course> {
        val result = mutableListOf<Course>()
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheetAt(0)

        for (row in sheet.drop(1)) { // 첫 행은 헤더
            val nameCell = row.getCell(0)
            val gradeCell = row.getCell(1)

            val name = nameCell?.stringCellValue?.trim() ?: continue
            val grade = gradeCell?.toString()?.trim() ?: continue

            result.add(Course(name, grade))
        }

        workbook.close()
        return result
    }

    private fun setChart() {

        // major
        binding.apply {
            val recommendCarbohydrates = 43f
            val intakeCarbohydrates = 65f
            // 그래프에 나타낼 데이터
            val entries = ArrayList<PieEntry>()
            entries.add(PieEntry(intakeCarbohydrates, "이수 학점"))
            entries.add(PieEntry(recommendCarbohydrates, "남은 학점"))


            // 그래프 색상(데이터 순서)
            val colors = listOf(
                Color.parseColor("#50A56F"),
                Color.parseColor("#D2D1D4")
            )

            // 데이터, 색상, 글자크기 및 색상 설정
            val dataSet = PieDataSet(entries, "")
            dataSet.apply {
                dataSet.colors = colors
                valueTextSize = 12F
                valueTextColor = Color.BLACK
                dataSet.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return String.format("%.0f", value)
                    }
                }

            }

            // Pie 그래프 생성
            val dataMPchart = PieData(dataSet)
            chartEssential.apply {
                data = dataMPchart
                // 중앙 텍스트를 설정하여 섭취량 표시
                centerText =
                    String.format(
                        "전공 필수 \n%.0f / %.0f",
                        intakeCarbohydrates,
                        recommendCarbohydrates
                    )
                setEntryLabelColor(Color.BLACK)
                setCenterTextSize(12f)
                setCenterTextTypeface(Typeface.DEFAULT_BOLD)

                // 범례와 그래프 설명 비활성화
                legend.isEnabled = false
                description.isEnabled = false
                setTouchEnabled(false)

                // 그래프 업데이트
                invalidate()
            }
        }

        // optional
        binding.apply {
            val recommendCarbohydrates = 43f
            val intakeCarbohydrates = 65f
            // 그래프에 나타낼 데이터
            val entries = ArrayList<PieEntry>()
            entries.add(PieEntry(intakeCarbohydrates, "이수 학점"))
            entries.add(PieEntry(recommendCarbohydrates, "남은 학점"))


            // 그래프 색상(데이터 순서)
            val colors = listOf(
                Color.parseColor("#50A56F"),
                Color.parseColor("#D2D1D4")
            )

            // 데이터, 색상, 글자크기 및 색상 설정
            val dataSet = PieDataSet(entries, "")
            dataSet.apply {
                dataSet.colors = colors
                valueTextSize = 12F
                valueTextColor = Color.BLACK
                dataSet.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return String.format("%.0f", value)
                    }
                }

            }

            // Pie 그래프 생성
            val dataMPchart = PieData(dataSet)
            chartOptional.apply {
                data = dataMPchart
                // 중앙 텍스트를 설정하여 섭취량 표시
                centerText =
                    String.format(
                        "전공 선택 \n%.0f / %.0f",
                        intakeCarbohydrates,
                        recommendCarbohydrates
                    )
                setEntryLabelColor(Color.BLACK)
                setCenterTextSize(12f)
                setCenterTextTypeface(Typeface.DEFAULT_BOLD)

                // 범례와 그래프 설명 비활성화
                legend.isEnabled = false
                description.isEnabled = false
                setTouchEnabled(false)
                // 그래프 업데이트
                invalidate()
            }

        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}