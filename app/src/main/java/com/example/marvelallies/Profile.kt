package com.example.marvelallies

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.common.SignInButton
import com.google.android.gms.auth.api.signin.GoogleSignInClient

class Profile : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var layoutApiIdSetup: LinearLayout
    private lateinit var layoutProfileContent: LinearLayout
    private lateinit var etApiId: EditText
    private lateinit var btnSaveApiId: Button

    private lateinit var googleSingInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onResume() {
        super.onResume()
        requireActivity().title = getString(R.string.Profile)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        layoutApiIdSetup = view.findViewById(R.id.layoutApiIdSetup)
        layoutProfileContent = view.findViewById(R.id.layoutProfileContent)
        etApiId = view.findViewById(R.id.etApiId)
        btnSaveApiId = view.findViewById(R.id.btnSaveApiId)

        btnSaveApiId.setOnClickListener { saveApiId()}

        loadUserAndToggleUi()
    }

    private fun loadUserAndToggleUi(){
        val user = auth.currentUser
        if(user != null){

        }

        val uid = user?.uid ?: ""
        val email = user?.email ?: ""

        val docRef = db.collection("users").document(uid)

        docRef.get()
            .addOnSuccessListener { doc ->
                if(!doc.exists()){
                    val newUserData = hashMapOf(
                        "email" to email,
                        "apiId" to null
                    )
                    docRef.set(newUserData)
                        .addOnSuccessListener {
                            showApiSetup()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error saving user data", Toast.LENGTH_SHORT).show()
                            showApiSetup()
                        }
                    return@addOnSuccessListener
                }

                val apiId = doc.getString("apiId")
                if(apiId == null){
                    showApiSetup()
                }else{
                    showFullProfile()
                }
        }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error loading user data", Toast.LENGTH_SHORT).show()
                showApiSetup()
            }
    }

    private fun saveApiId(){
        val user = auth.currentUser ?: run{
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val apidIdText = etApiId.text.toString()
        if(apidIdText.isBlank()){
            etApiId.error = "Your Rivals UID is required"
            return
        }

        val apiIdNumber = apidIdText.toLongOrNull()
        if(apiIdNumber == null){
            etApiId.error = "Your Rivals UID must be a number"
            return
        }

        val uid = user.uid
        val docRef = db.collection("users").document(uid)

        docRef.update("apiId", apiIdNumber)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "UID saved", Toast.LENGTH_SHORT).show()
                showFullProfile()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error saving UID", Toast.LENGTH_SHORT).show()
            }
    }


    private fun showApiSetup() {
        layoutApiIdSetup.visibility = View.VISIBLE
        layoutProfileContent.visibility = View.GONE
    }

    private fun showFullProfile() {
        layoutApiIdSetup.visibility = View.GONE
        layoutProfileContent.visibility = View.VISIBLE
    }


}