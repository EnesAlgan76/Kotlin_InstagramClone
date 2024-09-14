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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.data.api.RetrofitInstance
import com.example.kotlininstagramapp.data.api.UserApi
import com.example.kotlininstagramapp.utils.BottomNavHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {
    lateinit var firestore: FirebaseFirestore
    lateinit var adapter: SearchResultsAdapter
    val userService = RetrofitInstance.retrofit.create(UserApi::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)


        firestore = FirebaseFirestore.getInstance()
        val searchBox = view.findViewById<EditText>(R.id.searchBox)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_search)
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        BottomNavHandler.setupBottomNavBar(bottomNavigationView,requireActivity(),findNavController())
        bottomNavigationView.menu.findItem(R.id.menu_item_search).isChecked = true

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SearchResultsAdapter(requireContext())
        recyclerView.adapter = adapter

        val handler = Handler(Looper.getMainLooper())

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacksAndMessages(null)
                val searchText = s.toString().trim()
                if (searchText.isNotEmpty()) {
                    handler.postDelayed({ fetchUsers(searchText) }, 800)
                } else {
                    adapter.clear()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Not used in this example
            }
        })

        return view
    }

    private fun fetchUsers(searchText: String) {
        println("---------- Fetching Users ------")

        CoroutineScope(Dispatchers.IO).launch {
            val response = userService.searchUsersByUsername(searchText).execute()
            if (response.isSuccessful) {
                val userList = response.body()?.data as List<Map<String, String>>
                withContext(Dispatchers.Main) {
                    adapter.setUsers(userList)
                }

            } else {
                Log.e("---------------------", "Response failed")
            }
        }
    }
}
