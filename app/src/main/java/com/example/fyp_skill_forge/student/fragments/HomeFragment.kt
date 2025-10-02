package com.example.fyp_skill_forge.student.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fyp_skill_forge.R
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.fyp_skill_forge.databinding.FragmentHomeBinding
import com.example.fyp_skill_forge.student.adapter.recyclerview.ContinueCoursesAdapter
import com.example.fyp_skill_forge.student.adapter.recyclerview.CoursesAdapter
import com.example.fyp_skill_forge.student.adapter.recyclerview.WeProvided
import com.example.fyp_skill_forge.student.model.AllCourses
import com.example.fyp_skill_forge.student.model.Courses
import com.example.fyp_skill_forge.student.utlis.Constant.getDataContinueCourses
import com.example.fyp_skill_forge.student.utlis.Constant.getDataWeProvided
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Firestore-related variables
    private val coursesList = ArrayList<Courses>()
    private lateinit var coursesAdapter: CoursesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using view binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.primary)

        // "All Courses" button click
        binding.allCourses.setOnClickListener {
            val intent = Intent(requireActivity(), AllCourses::class.java)
            startActivity(intent)
        }


        // Set up the image slider
        val imgList = ArrayList<SlideModel>()
        imgList.add(SlideModel((R.drawable.img_3), "Web Development"))
        imgList.add(SlideModel((R.drawable.python), "Python"))
        imgList.add(SlideModel((R.drawable.img_1), "Artificial Intelligence"))
        imgList.add(SlideModel((R.drawable.digital), "Digital Marketing"))
        imgList.add(SlideModel((R.drawable.img_2), "App Development"))
        imgList.add(SlideModel((R.drawable.seo), "SEO"))

        binding.imageSlider.setImageList(imgList, ScaleTypes.FIT)



        // Initialize RecyclerViews
        setUpCoursesRV() // Courses RecyclerView
        setUpWeProvided() // Horizontal RecyclerView for other data
        setUpContinueCoursesRV() // Continue Courses RecyclerView

        // Fetch courses from Firestore
        fetchCoursesFromFirestore()
    }

    private fun setUpCoursesRV() {
        // Show progress bar before fetching data
        binding.customProgressBar.visibility = View.VISIBLE

        coursesAdapter = CoursesAdapter(coursesList, requireContext())
        binding.rv.adapter = coursesAdapter
        binding.rv.layoutManager = GridLayoutManager(requireContext(), 2)

        fetchCoursesFromFirestore()
    }

//    Add the logic of Continue Cources Here

    private fun fetchCoursesFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Courses")
            .get()
            .addOnSuccessListener { result ->
                coursesList.clear()
                for (document in result) {
                    val course = document.toObject(Courses::class.java)
                    coursesList.add(course)
                    Log.d("Firestore Data", "Fetched Course: $course")  // Log each course
                }
                coursesAdapter.notifyDataSetChanged()

                // Hide progress bar when data is loaded
                binding.customProgressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                // Hide progress bar in case of failure
                binding.customProgressBar.visibility = View.GONE
                Log.e("Firestore Error", "Error getting documents: ", exception)
            }
    }


//    private fun setUpCoursesRV() {
//        coursesAdapter = CoursesAdapter(coursesList, requireContext())
//        binding.rv.adapter = coursesAdapter
//        binding.rv.layoutManager = GridLayoutManager(requireContext(), 2)
//        fetchCoursesFromFirestore()
//    }

    private fun setUpWeProvided() {
        val adapter = WeProvided(getDataWeProvided(), requireContext())
        binding.rvWeProvided.adapter = adapter
        binding.rvWeProvided.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setUpContinueCoursesRV() {
        val adapter = ContinueCoursesAdapter(getDataContinueCourses(), requireContext())
        binding.rvContinueCourses.adapter = adapter
        binding.rvContinueCourses.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

//    private fun fetchCoursesFromFirestore() {
//        val db = FirebaseFirestore.getInstance()
//        db.collection("Courses")
//            .get()
//            .addOnSuccessListener { result ->
//                coursesList.clear()
//                for (document in result) {
//                    val course = document.toObject(Courses::class.java)
//                    coursesList.add(course)
//                    Log.d("Firestore Data", "Fetched Course: $course")  // Log each course
//                }
//                coursesAdapter.notifyDataSetChanged()
//            }
//            .addOnFailureListener { exception ->
//                Log.e("Firestore Error", "Error getting documents: ", exception)
//            }
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Nullify the binding reference to avoid memory leaks
    }


}