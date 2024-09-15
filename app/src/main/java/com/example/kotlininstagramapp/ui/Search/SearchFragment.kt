package com.example.kotlininstagramapp.ui.Search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.data.api.RetrofitInstance
import com.example.kotlininstagramapp.data.api.UserApi
import com.example.kotlininstagramapp.databinding.FragmentSearchBinding
import com.example.kotlininstagramapp.utils.BottomNavHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchFragment : Fragment() {
    lateinit var firestore: FirebaseFirestore
    lateinit var searchResultAdapter: SearchResultsAdapter
    lateinit var binding: FragmentSearchBinding
    val userService = RetrofitInstance.retrofit.create(UserApi::class.java)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentSearchBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()

        searchResultAdapter= SearchResultsAdapter(requireContext())
        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchResultAdapter
        }



        val handler = Handler(Looper.getMainLooper())

        binding.searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacksAndMessages(null)
                val searchText = s.toString().trim()
                if (searchText.isNotEmpty()) {
                    handler.postDelayed({ fetchUsers(searchText) }, 800)
                } else {
                    searchResultAdapter.clear()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Not used in this example
            }
        })

        return binding.root
    }

    private fun fetchUsers(searchText: String) {
        println("---------- Fetching Users ------")

        CoroutineScope(Dispatchers.IO).launch {
            val response = userService.searchUsersByUsername(searchText).execute()
            if (response.isSuccessful) {
                val userList = response.body()?.data as List<Map<String, String>>
                withContext(Dispatchers.Main) {
                    searchResultAdapter.setUsers(userList)
                }

            } else {
                Log.e("---------------------", "Response failed")
            }
        }
    }
}
