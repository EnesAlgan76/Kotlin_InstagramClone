package com.example.kotlininstagramapp.Home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
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
import com.google.firebase.database.*
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
    private lateinit var searchResultsAdapter: SearchResultsAdapter

    private lateinit var searchBox: EditText
    private var usersList: MutableList<User> =mutableListOf()
    lateinit var firestore: FirebaseFirestore


    var conservations: ArrayList<Conversation>  = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_messages,container,false)
        firestore = FirebaseFirestore.getInstance()

        recyclerViewConversations = view.findViewById(R.id.recycler_view_conversations)
        recyclerViewUsers = view.findViewById(R.id.recycler_view_users)
        recyclerViewConversations.layoutManager = LinearLayoutManager(requireContext())
        searchBox = view.findViewById(R.id.searchBox)
        searchResultsAdapter = SearchResultsAdapter(requireContext())

        recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewUsers.adapter = searchResultsAdapter


        val handler = Handler(Looper.getMainLooper())

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacksAndMessages(null)
                val searchText = s.toString().trim()
                if (searchText.isNotEmpty()) {
                    recyclerViewUsers.visibility = View.VISIBLE
                    handler.postDelayed({fetchUsers(searchText)},800)  // Kullanıcının her harf girdiğinde aramak yerine yazmayı bitirince ara.
                } else {
                    searchResultsAdapter.clear()
                    recyclerViewUsers.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Not used in this example
            }
        })



        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    conservations = FirebaseHelper().getConversations()
                }
                conversationsAdapter = ConversationsAdapter(conservations)
                recyclerViewConversations.adapter = conversationsAdapter

            } catch (e: Exception) {
                println("Error fetching posts: ${e.message}")
            }
        }


        return view
    }

    private fun fetchUsers(searchText: String) {
        println("---------- Sorgu Yapılıyor ------")
        firestore.collection("users")
            .whereGreaterThanOrEqualTo("userName", searchText)
            .whereLessThan("userName", searchText + "\uf8ff")
            .orderBy("userName", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let { snapshot ->
                    val users = snapshot.documents.mapNotNull { document ->
                        val userFullName = document.getString("userFullName") ?: ""
                        val userName = document.getString("userName") ?: ""
                        val userId = document.getString("userId") ?: ""
                        val userDetails = document.get("userDetails") as? Map<*, *>
                        val profileImage = userDetails?.get("profilePicture") as? String ?: ""


                        mapOf(
                            "userFullName" to userFullName,
                            "userName" to userName,
                            "userProfileImage" to profileImage,
                            "userId" to userId,
                        )
                    }
                    searchResultsAdapter.setUsers(users)
                }
            }
    }

}