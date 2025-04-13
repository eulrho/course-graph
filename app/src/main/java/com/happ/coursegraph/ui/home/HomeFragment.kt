package com.happ.coursegraph.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.happ.coursegraph.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btMajorRequired.setOnClickListener {
            binding.radialGraph.setCategory("전공필수")
        }

        binding.btEmbeddedIot.setOnClickListener {
            binding.radialGraph.setCategory("임베디드 IoT")
        }

        binding.btIntelligentIot.setOnClickListener {
            binding.radialGraph.setCategory("지능형 IoT")
        }

        binding.btMicroDegree.setOnClickListener {
            binding.radialGraph.setCategory("마이크로디그리")
        }

        binding.btIntelligentSystem.setOnClickListener {
            binding.radialGraph.setCategory("지능형 시스템")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}