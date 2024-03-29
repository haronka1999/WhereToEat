package com.e.wheretoeat.main.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.e.wheretoeat.main.data.MyDatabase
import com.e.wheretoeat.main.data.user.User
import com.e.wheretoeat.main.data.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class UserViewModel(application: Application) : AndroidViewModel(application) {

    //attributes to share
    var currentUserId: MutableLiveData<Long> = MutableLiveData()
    val readAllData: LiveData<List<User>>

    //helper attributes
    private val repository: UserRepository
    private var id by Delegates.notNull<Long>()

    init {
        val userDao = MyDatabase.getDatabase(application).userDao()
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

    fun getCurrentUserId(currentUserName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCurrentUserId(currentUserName)
        }
    }


    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(user)
        }
    }
}