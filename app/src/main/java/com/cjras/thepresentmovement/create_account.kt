package com.cjras.thepresentmovement

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.cjras.thepresentmovement.databinding.FragmentAllProjectsBinding
import com.cjras.thepresentmovement.databinding.FragmentCreateAccountBinding
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



        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //initial data population
        //---------------------------------------------------------------------------------------------------------------------------------------------------------

        try {
            GlobalClass.checkUser(this)

            MainScope().launch {
                if (GlobalClass.UpdateDataBase == true) {

                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE

                    withContext(Dispatchers.Default) {

                        var databaseManager = DatabaseManager()

                        databaseManager.updateFromDatabase()
                    }


                }
                UpdateUI()
            }
        } catch (e: Exception) {
            GlobalClass.InformUser(
                getString(R.string.errorText),
                "${e}",
                requireContext()
            )
        }
        //---------------------------------------------------------------------------------------------------------------------------------------------------------



        //if the header is clicked
        binding.llHeader.setOnClickListener()
        {
            fragmentManager?.popBackStackImmediate()
        }

        //if the refresh "button" is clicked
        binding.ivRefresh.setOnClickListener()
        {
            GlobalClass.RefreshFragment(this)
        }

        return view
    }



    //update UI
    private fun UpdateUI()
    {

        //check if the current user is an administrator, otherwise hide certain items
        if (GlobalClass.currentUser.MemberTypeID == 3)
        {
            val filterManager = FilterListFunctions()
            filterManager.populateMemberTypes(binding.spnMemberTypes, requireActivity(), false)
        }
        else
        {
            binding.spnMemberTypes.visibility = View.GONE
            binding.rlSpinnerMember.visibility = View.GONE
            binding.tvRole.visibility = View.GONE
        }


        firebaseAuth = FirebaseAuth.getInstance()

        //if create account button is clicked
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

    }



    private fun RegisterNewUser(
        Name: String,
        Surname: String,
        Email: String,
        Password: String,
        ConfirmPassword: String,
    ) {

        //password result method to check if the password meets strength requirements
        var passwordResult = UserDataClass().validateUserPassword(Password, requireActivity())
        //var successStatus = false

        //check if the password and confirm password are the same
        if (Password == ConfirmPassword) {
            if (passwordResult == "") {

                MainScope().launch() {

                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                        View.VISIBLE

                    withContext(Dispatchers.Default) {

                        //use firebase auth create user with email and password native method, adds user to Firestore
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
                                        MemberTypeID = 1, //change to spinner input
                                        Quote = "",
                                        ContactNumber = "",
                                        CompanyName = "",
                                        LinkedIn = "",
                                        Website = "",
                                        HasImage = false
                                    )


                                    if (GlobalClass.currentUser.MemberTypeID == 3) {

                                        var selectedMemberTypeSpinnerIndex = GlobalClass.MemberTypes.indexOfLast { it.MemberType == binding.spnMemberTypes.selectedItem.toString() }

                                        if (selectedMemberTypeSpinnerIndex != -1)
                                        {
                                            //if the member type exists
                                            val selectedMemberTypeID = GlobalClass.MemberTypes[selectedMemberTypeSpinnerIndex].MemberTypeID
                                            tempUser.MemberTypeID = selectedMemberTypeID
                                        }
                                    }

                                    //add user to Firestore with the entered details
                                    dbManager.addNewUserToFirestore(tempUser)
                                    //successStatus = true

                                    //inform the current user that they have successfully added a new user
                                    Toast.makeText(requireActivity(), getString(R.string.userAdded), Toast.LENGTH_SHORT).show()

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

                                    //GlobalClass.UpdateDataBase = true

                                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

                                    //binding.llHeader.callOnClick()


                                }
                            }
                    }


                }
            } else {
                GlobalClass.InformUser(getString(R.string.invalidPassword), passwordResult, requireActivity())
            }
        } else {
            Toast.makeText(requireActivity(), getString(R.string.passwordNotMatched), Toast.LENGTH_SHORT).show()
        }

    }

}