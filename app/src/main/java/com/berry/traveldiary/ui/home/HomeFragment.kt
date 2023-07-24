package com.berry.traveldiary.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.berry.traveldiary.DiaryEntryActivity
import com.berry.traveldiary.R
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


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        myDatabase = MyDatabase.getDatabase(requireContext())

        val startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    // Handle the Intent
                    getDiaryList()
                }
            }



        binding.floatingActionButton.setOnClickListener {
            startForResult.launch(Intent(requireContext(), DiaryEntryActivity::class.java))
        }


        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDiaryList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val search = menu.findItem(R.id.searchItems)
        val searchView = search.actionView as androidx.appcompat.widget.SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.
        SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    getItemsFromDb(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    getItemsFromDb(newText)
                }
                return true
            }

        })
    }

    private fun getItemsFromDb(searchText: String) {
        var searchText = searchText
        searchText = "%$searchText%"

        CoroutineScope(Dispatchers.IO).launch {
            diaryEntriesList = myDatabase.diaryEntryDao().getSearchResults(searchText)
        }.invokeOnCompletion {  Toast.makeText(requireContext(), "search completed>>."+diaryEntriesList, Toast.LENGTH_LONG).show() }


    }


    private fun getDiaryList() {
        val recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        CoroutineScope(Dispatchers.IO).launch {
            diaryEntriesList = myDatabase.diaryEntryDao().readAllData()
        }.invokeOnCompletion {
            val myAdapter = ListAdapter(diaryEntriesList)
            recyclerView.adapter = myAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}