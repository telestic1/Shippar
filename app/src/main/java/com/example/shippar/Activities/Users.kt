package com.example.shippar.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shippar.R
import com.example.shippar.Utils.UserDetails
import com.example.shippar.Utils.Utils
import com.google.ai.client.generativeai.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Users : AppCompatActivity() {
    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var noUsersText: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private lateinit var pd: ProgressDialog
    private lateinit var usersList: ArrayList<String>
    private lateinit var usersAdapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_users)

        // Apply insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val signout = findViewById<Button>(R.id.sign_out)
        pd = ProgressDialog(this)
        pd.setMessage("Loading...")
        pd.show()
        mAuth = FirebaseAuth.getInstance()

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user: FirebaseUser? = firebaseAuth.currentUser
            if (user == null) {
                pd.dismiss()
                Utils.intentWithClear(this@Users, Login::class.java)
            } else {
                UserDetails.userID = user.uid
                // Logging user email (Consider removing for sensitive information)
                // Log.d("userssss", user.email)
            }
        }

        val userRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("/users")
        usersList = ArrayList()
        usersRecyclerView = findViewById(R.id.usersList)
        noUsersText = findViewById(R.id.noUsersText)
        usersAdapter = UsersAdapter(usersList)

        usersRecyclerView.layoutManager = LinearLayoutManager(this)
        usersRecyclerView.adapter = usersAdapter

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList.clear()

                for (child in dataSnapshot.children) {
                    val userData = child.getValue(String::class.java)
                    if (child.value != FirebaseAuth.getInstance().currentUser?.email) {
                        usersList.add(child.value.toString())
                    }
                }

                if (usersList.isEmpty()) {
                    noUsersText.visibility = View.VISIBLE
                    usersRecyclerView.visibility = View.GONE
                } else {
                    noUsersText.visibility = View.GONE
                    usersRecyclerView.visibility = View.VISIBLE
                    usersAdapter.notifyDataSetChanged()
                }

                pd.dismiss()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled event if needed
            }
        })

        usersAdapter.setOnItemClickListener(object : UsersAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val checkChild = usersList[position]
                UserDetails.chatwithEmail = checkChild
                val intent = Intent(this@Users, Chat::class.java)
                intent.putExtra("chatwithEmail", checkChild)
                startActivity(intent)
            }
        })

        signout.setOnClickListener {
            UserDetails.chatwithEmail = ""
            UserDetails.userType = ""
            UserDetails.userEmail = ""
            UserDetails.chatwithID = ""
            UserDetails.userID = ""
            UserDetails.chatRef = null
            FirebaseAuth.getInstance().signOut()
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(mAuthListener)
    }
}