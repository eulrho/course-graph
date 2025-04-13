package com.happ.coursegraph.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.happ.coursegraph.databinding.BottomsheetAddSubjectBinding

class AddSubjectBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetAddSubjectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetAddSubjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 도움말 클릭 시 - 추후 팝업 연결 가능
        binding.ivHelp.setOnClickListener {
            // 예: Toast 또는 AlertDialog 등으로 구현 가능
        }

        // 예: 파일명 세팅 예시
        binding.tvFileName.text = "예: subjects.csv"

        // 추후 RecyclerView 어댑터 연결 가능
        // binding.rvSubjects.adapter = YourSubjectAdapter(...)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
