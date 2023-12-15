package com.example.kotlininstagramapp.Home

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagramapp.Models.Conversation
import com.example.kotlininstagramapp.Models.User
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.Search.SearchResultsAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessagesFragment : Fragment() {

    private lateinit var recyclerViewConversations: RecyclerView
    private lateinit var recyclerViewUsers: RecyclerView

    private lateinit var conversationsAdapter: ConversationsAdapter
    private lateinit var conversationsSearchResultsAdapter: ConversationsSearchResultsAdapter

    private lateinit var searchBox: EditText
    private val conservations: ArrayList<Conversation> = ArrayList()
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)
        initializeViews(view)
        setupSearchListener()
        fetchConversations()
        return view
    }

    private fun initializeViews(view: View) {
        firestore = FirebaseFirestore.getInstance()

        recyclerViewConversations = view.findViewById(R.id.recycler_view_conversations)
        recyclerViewConversations.layoutManager = LinearLayoutManager(requireContext())
        conversationsAdapter = ConversationsAdapter(conservations)
        recyclerViewConversations.adapter = conversationsAdapter

        recyclerViewUsers = view.findViewById(R.id.recycler_view_users)
        recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())
        conversationsSearchResultsAdapter = ConversationsSearchResultsAdapter(requireContext())
        recyclerViewUsers.adapter = conversationsSearchResultsAdapter

        searchBox = view.findViewById(R.id.searchBox)
    }

    private fun setupSearchListener() {
        val handler = Handler(Looper.getMainLooper())

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacksAndMessages(null)
                val searchText = s.toString().trim()
                if (searchText.isNotEmpty()) {
                    recyclerViewUsers.visibility = View.VISIBLE
                    recyclerViewConversations.visibility = View.INVISIBLE
                    handler.postDelayed({ fetchUsers(searchText) }, 800)
                } else {
                    conversationsSearchResultsAdapter.clear()
                    recyclerViewUsers.visibility = View.GONE
                    recyclerViewConversations.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchConversations() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    conservations.clear()
                    conservations.addAll(FirebaseHelper().getConversations())
                }
                conversationsAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Log.e("MessagesFragment", "Error fetching conversations: ${e.message}")
            }
        }
    }

    private fun fetchUsers(searchText: String) {
        println("---------- Querying Users ------")
        firestore.collection("users")
            .whereGreaterThanOrEqualTo("userName", searchText)
            .whereLessThan("userName", "$searchText\uf8ff")
            .orderBy("userName", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let { snapshot ->
                    val users = mutableListOf<Map<String, String>>()
                    for (document in snapshot.documents) {
                        val userFullName = document.getString("userFullName") ?: ""
                        val userName = document.getString("userName") ?: ""
                        val userId = document.getString("userId") ?: ""
                        val userDetails = document.get("userDetails") as? Map<*, *>
                        val profileImage = userDetails?.get("profilePicture") as? String ?: ""

                        val user = mapOf(
                            "userFullName" to userFullName,
                            "userName" to userName,
                            "userProfileImage" to profileImage,
                            "userId" to userId,
                        )
                        users.add(user)
                    }
                    conversationsSearchResultsAdapter.setUsers(users)
                }
            }
    }

}
