package com.example.marvelallies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfile : Fragment()
{
    //FIREBASE VARIABLES
    private lateinit var _auth: FirebaseAuth
    private lateinit var _dataBase: FirebaseFirestore

    //UI VARIABLES
    private lateinit var _etApiId: EditText
    private lateinit var _btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        /*
         * No realizo lógica adicional en este método,
         * ya que el fragment depende principalmente de los argumentos recibidos
         */
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        _auth = FirebaseAuth.getInstance()
        _dataBase = FirebaseFirestore.getInstance()

        _etApiId = view.findViewById(R.id.etApiId)
        _btnSave = view.findViewById(R.id.btnSave)

        loadCurrentData()

        _btnSave.setOnClickListener { saveChanges() }
    }

    private fun loadCurrentData()
    {
        val user = _auth.currentUser ?: return
        _dataBase.collection("users").document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                val apiId = doc.getLong("apiId")
                if(apiId != null)
                {
                    _etApiId.setText(apiId.toString())
                }
            }
    }

    private fun saveChanges()
    {
        android.util.Log.d("EDIT_PROFILE", "saveChanges() clicked")
        Toast.makeText(requireContext(), "Saving...", Toast.LENGTH_SHORT).show()
        val user = _auth.currentUser ?: return
        val uid = user.uid

        val apiIdText = _etApiId.text.toString()
        if(apiIdText.isBlank())
        {
            _etApiId.error = "Your Rivals UID is required"
            return
        }

        val apiIdNumber = apiIdText.toLongOrNull()
        if (apiIdNumber == null)
        {
            _etApiId.error = "Numbers only"
            return
        }

        _dataBase.collection("users").document(uid)
            .update("apiId", apiIdNumber)


        Toast.makeText(requireContext(), "Changes saved", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }
}