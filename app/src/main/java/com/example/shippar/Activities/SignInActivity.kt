package com.example.shippar.Activities

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.shippar.R
import com.example.shippar.Utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var user: String
    private lateinit var pass: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var pd: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val login = findViewById<TextView>(R.id.login)
        pd = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("/users")

        registerButton.setOnClickListener {
            user = username.text.toString()
            pass = password.text.toString()
            if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pass)) {
                pd.setMessage("Loading..")
                pd.show()
                mAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        pd.dismiss()
                        Toast.makeText(this@SignInActivity, "FAILED! " + task.exception?.message, Toast.LENGTH_SHORT).show()
                    } else {
                        pd.dismiss()
                        val tempUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                        if (tempUser != null) {
                            userRef.child(tempUser.uid).setValue(tempUser.email)
                            Utils.intentWithClear(this@SignInActivity, user::class.java)
                        }
                    }
                }
            }
        }
        login.setOnClickListener {
            Utils.intentWithClear(this@SignInActivity, Login::class.java)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Utils.intentWithClear(this@SignInActivity, Login::class.java)
    }

    }
