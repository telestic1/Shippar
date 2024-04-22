package com.example.shippar.Activities

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shippar.R
import com.example.shippar.Utils.UserDetails
import com.example.shippar.Utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Chat : AppCompatActivity() {

    private lateinit var sendButton: ImageView
    private lateinit var messageArea: EditText
    private lateinit var chatRecView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var chatwithEmail: String
    private lateinit var messageRef: DatabaseReference
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chatwithEmail = intent.getStringExtra("chatwithEmail") ?: ""

        sendButton = findViewById(R.id.sendButton)
        messageArea = findViewById(R.id.messageArea)
        chatRecView = findViewById(R.id.chat_recycler_view)
        toolbar = findViewById(R.id.chat_with_toolbar)

        setSupportActionBar(this.toolbar)
        supportActionBar?.title = UserDetails.chatwithEmail
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        chatRecView.setHasFixedSize(true)
        chatRecView.layoutManager = layoutManager

        messageRef = FirebaseDatabase.getInstance().getReference("/messages")
        userRef = FirebaseDatabase.getInstance().getReference("/users")

        sendButton.setOnClickListener {
            val messageText = messageArea.text.toString()
            if (messageText.isNotEmpty()) {
                val map = HashMap<String, String>()
                map["message"] = messageText
                map["user"] = UserDetails.userID
                UserDetails.chatRef?.push()?.setValue(map)
                messageArea.setText("")
                messageArea.hint = "Message"
            }
        }
    }

    private fun setSupportActionBar(toolbar: Toolbar?) {


    }

    override fun onStart() {
        super.onStart()
        Log.d("chatwithemail", "$chatwithEmail,$userRef")
        valueEventListener(userRef, chatwithEmail)
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chatMessage: TextView = itemView.findViewById(R.id.text_chat)
        private val userText: TextView = itemView.findViewById(R.id.user_chat)
        private val linearLayout: LinearLayout = itemView.findViewById(R.id.lin_lay)

        fun setChatMessage(message: String) {
            chatMessage.text = message
        }

        fun setUserText(userName: String, type: Boolean) {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.FILL_PARENT
            )
            if (type) {
                params.gravity = Gravity.END
                userText.text = "You"
                linearLayout.gravity = Gravity.END
                //   linearLayout.setBackgroundResource(R.drawable.bubble_in)
            } else {
                params.gravity = Gravity.START
                userText.text = UserDetails.chatwithEmail
                linearLayout.gravity = Gravity.START
                //   linearLayout.setBackgroundResource(R.drawable.bubble_out)
            }
            linearLayout.layoutParams = params
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Utils.intentWithClear(this, Users::class.java)
    }

    private fun valueEventListener(dbref: DatabaseReference, checkChild: String) {
        val messageRef = FirebaseDatabase.getInstance().getReference("/messages")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (child in dataSnapshot.children) {
                    if (child.value == checkChild) {
                        UserDetails.chatwithID = child.key!!
                        val type1 = "${UserDetails.userID}_${UserDetails.chatwithID}"
                        val type2 = "${UserDetails.chatwithID}_${UserDetails.userID}"
                        messageRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val child1 = dataSnapshot.child(type1)
                                val child2 = dataSnapshot.child(type2)
                                UserDetails.userType = if (child1.exists() || child2.exists()) {
                                    if (child1.exists()) "type1" else "type2"
                                } else {
                                    messageRef.child(type1).setValue("none")
                                    "type1"
                                }
                                UserDetails.chatRef =
                                    FirebaseDatabase.getInstance().getReference("/messages")
                                        .child(UserDetails.userType)
                                setChatAdapter()
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun setChatAdapter() {


    }
}
