package com.e.wheretoeat.main.data.user

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.e.wheretoeat.main.data.user.User
import com.e.wheretoeat.main.data.user.UserDatabase
import com.e.wheretoeat.main.data.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class UserViewModel(application: Application) : AndroidViewModel(application) {
    val readAllData: LiveData<List<User>>
    private var sharedPref: SharedPreferences =
        application.getSharedPreferences("credentials", Context.MODE_PRIVATE)!!
    private var editor = sharedPref.edit()
    private val repository: UserRepository
    var id by Delegates.notNull<Long>()

    var currentUserId: MutableLiveData<Long> = MutableLiveData()


    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        readAllData = repository.readAllData
    }

    fun addUser(user: User): LiveData<Long> {
        viewModelScope.launch(Dispatchers.IO) {
            id = repository.addUser(user)
            Log.d("Helo", "id in addUser: $id")
            currentUserId.postValue(id)
        }
        return currentUserId
    }

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(user)
        }
    }
}