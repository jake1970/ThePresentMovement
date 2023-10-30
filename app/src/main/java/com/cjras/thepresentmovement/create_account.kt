package com.cjras.thepresentmovement

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.cjras.thepresentmovement.databinding.FragmentAllProjectsBinding
import com.cjras.thepresentmovement.databinding.FragmentCreateAccountBinding
import com.cjras.thepresentmovement.databinding.FragmentRegisterUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


private var _binding: FragmentCreateAccountBinding? = null
private val binding get() = _binding!!
private lateinit var firebaseAuth: FirebaseAuth

class create_account : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnCreateAccount.setOnClickListener() {

            //boolean to determine if all fields are filled in
            var allFilled = true

            //the container where the input fields are
            val container = binding.rlHeader


            //loop through the inputs
            for (component in container.children) {
                //check that the current component is a text edit and that it doesn't contain a value
                if (component is EditText && component.text.isNullOrEmpty()) {
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

        binding.llHeader.setOnClickListener()
        {
            fragmentManager?.popBackStackImmediate()
        }

        binding.ivRefresh.setOnClickListener()
        {
            GlobalClass.RefreshFragment(this)
        }

        return view
    }

    private fun RegisterNewUser(
        Name: String,
        Surname: String,
        Email: String,
        Password: String,
        ConfirmPassword: String,
    ) {

        var passwordResult = UserDataClass().validateUserPassword(Password, requireActivity())
        //var successStatus = false

        if (Password == ConfirmPassword) {
            if (passwordResult == "") {

                MainScope().launch() {

                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                        View.VISIBLE

                    withContext(Dispatchers.Default) {

                        firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
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
                                        Website = "",
                                        HasImage = false
                                    )
                                    dbManager.addNewUserToFirestore(tempUser)
                                    //successStatus = true

                                    Toast.makeText(requireActivity(), "User Added", Toast.LENGTH_SHORT).show()

                                    GlobalClass.UpdateDataBase = true

                                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                                        View.GONE

                                    binding.llHeader.callOnClick()


                                } else {
                                    Toast.makeText(
                                        requireActivity(),
                                        it.exception?.localizedMessage.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    GlobalClass.UpdateDataBase = true

                                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                                        View.GONE

                                    binding.llHeader.callOnClick()


                                }
                            }
                    }


                }
            } else {
                GlobalClass.InformUser("Invalid Password", passwordResult, requireActivity())
            }
        } else {
            Toast.makeText(requireActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show()
        }

    }

}