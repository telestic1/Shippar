package com.example.shippar.Activities

import android.app.ProgressDialog
import android.content.Intent
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

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var username: EditText
        lateinit var password: EditText
        lateinit var user: String
        lateinit var pass: String
        lateinit var pd: ProgressDialog
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        pd = ProgressDialog(this)
        var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

        val registerUser = findViewById<TextView>(R.id.register)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            user = username.text.toString()
            pass = password.text.toString()
            if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pass)) {
                pd.setMessage("Storm....")
                pd.show()
                mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        pd.dismiss()
                        Utils.intentWithClear(this@Login, Users::class.java)
                    } else {
                        pd.dismiss()
                        Toast.makeText(this@Login, "FAILED" + task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        registerUser.setOnClickListener {
            startActivity(Intent(this@Login, SignInActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    }
