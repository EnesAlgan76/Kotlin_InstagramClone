package com.example.kotlininstagramapp.Share.Search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagramapp.Models.User
import com.example.kotlininstagramapp.utils.BottomNavigationHandler
import com.example.kotlininstagramapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SearchActivity : AppCompatActivity() {
    lateinit var firestore: FirebaseFirestore
    lateinit var adapter: SearchResultsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("SearchActivity sınıfına gidildi")
        setContentView(R.layout.activity_search)
        BottomNavigationHandler.setupNavigations(this,findViewById(R.id.bottomNavigationView_search),1)

        firestore = FirebaseFirestore.getInstance()
        val searchBox = findViewById<EditText>(R.id.searchBox)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_search)


        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SearchResultsAdapter(this)
        recyclerView.adapter = adapter

        val handler = Handler(Looper.getMainLooper())

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacksAndMessages(null)
                val searchText = s.toString().trim()
                if (searchText.isNotEmpty()) {
                   handler.postDelayed({fetchUsers(searchText)},800)  // Kullanıcının her harf girdiğinde aramak yerine yazmayı bitirince ara.
                } else {
                    adapter.clear()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Not used in this example
            }
        })
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
                    adapter.setUsers(users)
                }
            }
    }

}