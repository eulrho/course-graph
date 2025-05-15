package com.happ.coursegraph.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.happ.coursegraph.R

class BottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.parent as View
        val behavior = BottomSheetBehavior.from(bottomSheet)

        // BottomSheet 초기 상태 설정
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED // 시작 시 축소된 상태
        behavior.peekHeight = 200  // 기본 보이는 높이 (드래그 전)

        // 최대 높이까지 드래그 가능하도록 설정
        behavior.isFitToContents = false
        behavior.halfExpandedRatio = 0.4f // 절반 펼쳐진 상태 비율 (화면의 40%)
        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED // 중간 크기로 시작

        val closeButton: ImageButton = view.findViewById(R.id.btn_close)
        closeButton.setOnClickListener {
            dismiss() // BottomSheet 닫기
        }
    }
}
