package com.example.marvelallies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class EditProfile : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var etApiId: EditText
    private lateinit var btnSave: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etApiId = view.findViewById(R.id.etApiId)
        btnSave = view.findViewById(R.id.btnSave)

        loadCurrentData()

        btnSave.setOnClickListener { saveChanges() }
    }


    private fun loadCurrentData(){
        val user = auth.currentUser ?: return

        db.collection("users").document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                val apiId = doc.getLong("apiId")
                if(apiId != null){
                    etApiId.setText(apiId.toString())
                }
            }
    }

    private fun saveChanges(){

        android.util.Log.d("EDIT_PROFILE", "saveChanges() clicked")
        Toast.makeText(requireContext(), "Saving...", Toast.LENGTH_SHORT).show()
        val user = auth.currentUser ?: return
        val uid = user.uid

        val apiIdText = etApiId.text.toString()
        if(apiIdText.isBlank()){
            etApiId.error = "Your Rivals UID is required"
            return
        }

        val apiIdNumber = apiIdText.toLongOrNull()
        if (apiIdNumber == null) {
            etApiId.error = "Numbers only"
            return
        }

        db.collection("users").document(uid)
            .update("apiId", apiIdNumber)


        Toast.makeText(requireContext(), "Changes saved", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }
}