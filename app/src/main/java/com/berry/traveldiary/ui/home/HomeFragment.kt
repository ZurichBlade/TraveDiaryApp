package com.berry.traveldiary.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.berry.traveldiary.DiaryEntryActivity
import com.berry.traveldiary.data.MyDatabase
import com.berry.traveldiary.databinding.FragmentHomeBinding
import com.berry.traveldiary.model.DiaryEntries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private var diaryEntriesList: MutableList<DiaryEntries> = mutableListOf()
    private lateinit var myDatabase: MyDatabase
    private var adapter = ListAdapter(diaryEntriesList)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

//        val view = inflater.inflate(R.layout.fragment_home, container, false)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        myDatabase = MyDatabase.getDatabase(requireContext())

        val startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    // Handle the Intent
                    /*refersh the list for new entries*/

                    Toast.makeText(requireContext(), "On result .", Toast.LENGTH_LONG)
                        .show()
                    getDiaryList()
                }
            }


        adapter = ListAdapter(diaryEntriesList)
        val recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        getDiaryList()




        binding.floatingActionButton.setOnClickListener {

            startForResult.launch(Intent(requireContext(), DiaryEntryActivity::class.java))
//            val intent = Intent(requireContext(), DiaryEntryActivity::class.java)
//            startActivity(intent)
        }


        return root
    }

    override fun onResume() {
        super.onResume()

    }

    private fun getDiaryList() {
        CoroutineScope(Dispatchers.IO).launch {
//            diaryEntriesList.clear()
            diaryEntriesList = myDatabase.diaryEntryDao().readAllData()
            activity?.runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}