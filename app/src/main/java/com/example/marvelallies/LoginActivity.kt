package com.example.marvelallies

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailField = findViewById(R.id.login_etEmail)
        passwordField = findViewById(R.id.login_etPassword)

        auth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.login_btnRegister).setOnClickListener {Register()}
        findViewById<Button>(R.id.login_btnLogin).setOnClickListener {Login()}

    }

    private fun Register(){
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                    println("Error: ${task.exception}")
                }
            }
    }

    private fun Login(){
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser
                    val userId = user?.uid

                    Toast.makeText(this, "Login successful. UID: $userId", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                    println("Error: ${task.exception}")
                }
            }
    }

    private fun SingOut(){
        auth.signOut()
        Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show()
    }
}