package com.cjras.thepresentmovement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.cjras.thepresentmovement.databinding.FragmentAllProjectsBinding
import com.cjras.thepresentmovement.databinding.FragmentCreateAccountBinding
import com.cjras.thepresentmovement.databinding.FragmentRegisterUserBinding
import com.google.firebase.auth.FirebaseAuth


private var _binding: FragmentCreateAccountBinding? = null
private val binding get() = _binding!!
private lateinit var firebaseAuth: FirebaseAuth
class create_account: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnCreateAccount.setOnClickListener(){

            //boolean to determine if all fields are filled in
            var allFilled = true

            //the container where the input fields are
            val container = binding.rlHeader


            //loop through the inputs
            for (component in container.children)
            {
                //check that the current component is a text edit and that it doesn't contain a value
                if (component is EditText && component.text.isNullOrEmpty())
                {
                    //set the components error text
                    component.error = getString(R.string.missingText)

                    //set the filled status to false
                    allFilled = false
                }
            }
            //if all components are filled in
            if (allFilled == true) {
                RegisterNewUser(
                    binding.etFirstName.text.toString(),
                    binding.etLastName.text.toString(),
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString(),
                    binding.etConfirmPassword.text.toString()
                )
            }
        }

        return view
    }

    fun RegisterNewUser(Name: String, Surname:String, Email:String, Password:String, ConfirmPassword:String){
        if(isValidPassword(Password)){
            if(Password == ConfirmPassword)
            {
                firebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener{
                    if(it.isSuccessful){
                        val userInfo = firebaseAuth.currentUser!!.uid
                        val dbManager = DatabaseManager()
                        val tempUser = UserDataClass(
                            UserID = userInfo,
                            FirstName = Name,
                            LastName = Surname,
                            EmailAddress = Email,
                            MemberTypeID = 1,
                            Quote = "",
                            ContactNumber = "",
                            CompanyName = "",
                            LinkedIn = "",
                            Website = "newWebsite",
                            HasImage = false
                        )
                        dbManager.addNewUserToFirestore(tempUser)
                    }else{
                        Toast.makeText(
                            requireActivity(),
                            it.exception?.localizedMessage.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }else{
                Toast.makeText(requireActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(requireActivity(), "Password too weak", Toast.LENGTH_SHORT).show()
            Toast.makeText(
                requireActivity(),
                "Password requires 8 characters or more, at least 1 number, and at least 1 capital letter",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun isValidPassword(password: String): Boolean {
        if(password.length <= 8){
            return false
        }
        var hasLower = false
        var hasUpper = false
        var hasNumber = false
        for (cha in password){
            if (Character.isLowerCase(cha)){
                hasLower = true
            }
            else if (Character.isUpperCase(cha)){
                hasUpper = true
            }
            else if (Character.isDigit(cha)){
                hasNumber = true
            }

        }
        return hasLower && hasUpper && hasNumber
    }
}