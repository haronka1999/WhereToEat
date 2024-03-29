package com.e.wheretoeat.main.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.e.wheretoeat.R
import com.e.wheretoeat.databinding.FragmentRegisterBinding
import com.e.wheretoeat.main.data.user.User
import com.e.wheretoeat.main.viewmodels.UserViewModel


class RegisterFragment : Fragment() {


    //viewmodels
    private lateinit var mUserViewModel: UserViewModel

    //helpers
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var sharedPref: SharedPreferences

    //attributes for the user
    private lateinit var userName: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var email: String
    private lateinit var imageUri: Uri
    private lateinit var editor: SharedPreferences.Editor

    companion object {
        const val IMAGE_PICK_CODE = 1
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        sharedPref = context?.getSharedPreferences("credentials", Context.MODE_PRIVATE)!!
        editor = sharedPref.edit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)

        binding.chooseImageButton.setOnClickListener {
            pickImageFromGallery()
        }

        binding.saveButton.setOnClickListener {
            userName = binding.userNameEditText.text.toString()
            address = binding.addressEditText.text.toString()
            phone = binding.phoneNumber.text.toString()
            email = binding.emailEditText.text.toString()
            if (!isValidForm(userName, address,phone,email)) {
                return@setOnClickListener
            }

            //add the user details to shared preferences
            editor.clear()
            editor.putString("image", imageUri.toString())
            editor.putString("username", userName)
            editor.putString("address", address)
            editor.putString("phone", phone)
            editor.putString("email", email)
            editor.putString("pictureUrl", imageUri.toString())
            editor.apply()

            //add data to database
            insertUserIntoDataBase()
            findNavController().navigate(R.id.action_registerFragment2_to_homeFragment)
        }
        return binding.root
    }

    private fun isValidForm(userName: String, address: String, phone : String, email : String): Boolean {
        if (TextUtils.isEmpty(userName)) {
            binding.userNameEditText.error = "UserName is Required"
            return false
        }

        if (userName.length >= 12) {
            binding.userNameEditText.error = "User name is too long"
            return false
        }

        if (TextUtils.isEmpty(address)) {
            binding.addressEditText.error = "Address is Required"
            return false
        }
        if (TextUtils.isEmpty(phone)) {
            binding.phoneNumber.error = "Phone is Required"
            return false
        }
        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.error = "Email is Required"
            return false
        }

        if(!isValidEmail(email)){
            binding.emailEditText.error = "Email is not correct"
        }

        if(phone.length >= 10){
            binding.phoneNumber.error = "Phone number is not valid"
        }



        return true
    }

    private fun insertUserIntoDataBase() {
        val user = User(0, userName, imageUri.toString(), address, phone, email)
        mUserViewModel.currentUserId.observe(viewLifecycleOwner, {
            editor.putLong("id", mUserViewModel.currentUserId.value!!)
        })
        mUserViewModel.addUser(user).value
        Toast.makeText(activity, "Successfully added", Toast.LENGTH_SHORT).show()
    }

    private fun pickImageFromGallery() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), IMAGE_PICK_CODE
            )
        } else {
            //Intent to pick image
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            IMAGE_PICK_CODE ->                 // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(intent, IMAGE_PICK_CODE)
                } else {
                    Toast.makeText(activity, "You denied", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            // binding.registerProfileImage.setImageURI(imageUri)
            Glide.with(requireContext())
                .load(imageUri)
                .into(binding.registerProfileImage)
        }
    }


}
