package com.example.marvelallies

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity()
{
    private lateinit var _emailField: EditText
    private lateinit var _passwordField: EditText
    private lateinit var _auth: FirebaseAuth

    private lateinit var _googleSingInClient: GoogleSignInClient

    override fun onStart()
    {
        super.onStart()
        if(_auth.currentUser != null)
        {
            goToMain()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("341760355003-kqopo7bfp4aqqbg09s5ecmjjk0it3bti.apps.googleusercontent.com")
            .requestEmail()
            .build()

        _googleSingInClient = GoogleSignIn.getClient(this, gso)
        val account = GoogleSignIn.getLastSignedInAccount(this)

        account?.let {
            Log.d("Google Login", "${it.displayName} Account is already logged in")
        } ?: run {
            Log.d("Google Login", "No account is logged in")
            findViewById<SignInButton>(R.id.login_btnGoogle).setOnClickListener {SingInWithGoogle()}
        }

        _auth = FirebaseAuth.getInstance()

        _emailField = findViewById(R.id.login_etEmail)
        _passwordField = findViewById(R.id.login_etPassword)

        findViewById<Button>(R.id.login_btnRegister).setOnClickListener {Register()}
        findViewById<Button>(R.id.login_btnLogin).setOnClickListener {Login()}
    }

    private fun Register()
    {
        val email = _emailField.text.toString()
        val password = _passwordField.text.toString()

        if(email.isBlank() || password.isBlank())
        {
            Toast.makeText(this, "Email y contraseña son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        _auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this)
            { task ->
                if (task.isSuccessful)
                {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    goToMain()
                }
                else
                {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                    println("Error: ${task.exception}")
                }
            }
    }

    private fun Login()
    {
        val email = _emailField.text.toString()
        val password = _passwordField.text.toString()

        if(email.isBlank() || password.isBlank())
        {
            Toast.makeText(this, "Email y contraseña son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        _auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this)
            { task ->
                if (task.isSuccessful)
                {
                    val user = _auth.currentUser
                    val userId = user?.uid
                    Toast.makeText(this, "Login successful. UID: $userId", Toast.LENGTH_SHORT).show()

                    goToMain()
                }
                else
                {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                    println("Error: ${task.exception}")
                }
            }
    }

    private fun SingOut()
    {
        _auth.signOut()
        _googleSingInClient.signOut()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
        Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show()
    }

    private fun SingInWithGoogle()
    {
        val singInIntent = _googleSingInClient.signInIntent
        startActivityForResult(singInIntent, 9001)
        Toast.makeText(this, "Login with Google", Toast.LENGTH_SHORT).show()
    }

    private fun goToMain()
    {
        val email = _auth.currentUser?.email ?: ""
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("email", email)

        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 9001)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful)
            {
                val account = task.getResult(ApiException::class.java)

                val idToken = account.idToken
                if (idToken != null)
                {
                    firebaseAuthWithGoogle(idToken)
                }
                else
                {
                    Toast.makeText(this, "No ID token from Google", Toast.LENGTH_SHORT).show()
                    Log.w("Google Login", "idToken was null")
                }
            }
            else
            {
                Log.w("Google Login", "Google sign in failed", task.exception)
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String)
    {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        _auth.signInWithCredential(credential)
            .addOnCompleteListener(this)
            { task ->
                if (task.isSuccessful)
                {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    goToMain()
                }
                else
                {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}