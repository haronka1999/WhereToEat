package com.e.wheretoeat.main.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.e.wheretoeat.R
import com.e.wheretoeat.databinding.FragmentRegisterBinding
import com.e.wheretoeat.main.models.User
import com.e.wheretoeat.main.viewmodels.MainViewModel
import com.e.wheretoeat.main.viewmodels.UserViewModel


class RegisterFragment : Fragment() {


    private val mUserViewModel: UserViewModel by activityViewModels()

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var email: String
    private lateinit var userName: String
    private lateinit var password: String
    private var userID: Int = 0
    private var userNames: MutableList<String> = mutableListOf()
    private var emails: MutableList<String> = mutableListOf()
    private lateinit var sharedPref: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_register, container, false
        )

        sharedPref = context?.getSharedPreferences("credentials", Context.MODE_PRIVATE)!!
        binding.saveButton.setOnClickListener {
            userName = binding.userNameEditText.text.toString()
            password = binding.passwordEditText.text.toString()
            if (!isValidForm(userName, password)) {
                return@setOnClickListener
            }

            //add the user details to shared preferences
            val editor = sharedPref.edit()
            editor.clear()
            editor.putString("username", userName)
            editor.putString("password", password)
            editor.apply()

            //add data to database
            insertDatatoDatBase()
            findNavController().navigate(R.id.action_registerFragment2_to_homeFragment)

        }
        return binding.root
    }

    private fun insertDatatoDatBase() {
        val user = User(0, userName, password)
        mUserViewModel.addUser(user)
        Toast.makeText(activity, "Successfully added", Toast.LENGTH_SHORT).show()
    }


    private fun isValidForm(userName: String, password: String): Boolean {
        if (TextUtils.isEmpty(userName)) {
            binding.userNameEditText.error = "UserName is Required"
            return false
        }


        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.error = "Password is Required"
            return false
        }

        if (userName.length >= 10) {
            binding.userNameEditText.error = "User name is too long"
            return false
        }


        if (password.length < 6) {
            binding.passwordEditText.error = "Password must be 6 character long"
            return false
        }

        if (userNames.contains(userName)) {
            binding.userNameEditText.error = "This Username is taken"
            return false
        }

        return true
    }


}
