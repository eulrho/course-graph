package com.happ.coursegraph.ui.home

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.happ.coursegraph.CourseApplication.Companion.getUserToken
import com.happ.coursegraph.databinding.FragmentHomeBinding
import org.json.JSONArray

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setEvent()
    }

    private fun init() {

    }

    private fun setEvent() {
        observeViewModel()
        val token = getUserToken()

        if (token != null) {
            homeViewModel.getCourseData(token)
        }
        binding.btMajorRequired.setOnClickListener {
            binding.radialGraph.setCategory("전공필수")
        }

        binding.btIntelligentIot.setOnClickListener {
            binding.radialGraph.setCategory("지능형시스템")
        }

        binding.btMicroDegree.setOnClickListener {
            binding.radialGraph.setCategory("마이크로디그리")
        }
    }

    private fun observeViewModel() {
        homeViewModel.courseData.observe(viewLifecycleOwner) { courseList ->
            binding.radialGraph.setCourses(courseList)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}