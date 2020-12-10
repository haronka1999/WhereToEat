package com.e.wheretoeat.main.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e.wheretoeat.main.api.ApiRepository
import com.e.wheretoeat.main.data.User
import com.e.wheretoeat.main.models.ApiRestaurant
import com.e.wheretoeat.main.models.ApiRestaurantResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainViewModel : ViewModel() {

    //viewModel data
    var users: MutableLiveData<MutableList<User>> = MutableLiveData()
    var favoriteRestaurants: MutableLiveData<MutableList<ApiRestaurant>> = MutableLiveData()
    var apiRestaurantsByCountries: MutableLiveData<MutableList<ApiRestaurant>> = MutableLiveData()
    lateinit var currentApiRestaurant: ApiRestaurant


    //This variable will be used to define which data will be updated:

    //0 ---> username
    //1 ---> address
    //2 ---> phone
    //3 ---> email
    var dataBeEdited = -1

    /*
    The functions below will need for the database queries
     */

    private val apiRepository = ApiRepository()
    fun getAllRestaurants() {
        val result = apiRepository.getAllRestaurants()
        val tempListApiRestaurant: MutableList<ApiRestaurant> = mutableListOf()
        result.enqueue(object : Callback<ApiRestaurantResponse> {
            override fun onResponse(
                call: Call<ApiRestaurantResponse>,
                response: Response<ApiRestaurantResponse>
            ) {
                try {
                    Log.d("Helo", "Onresponse - okay ! ")
                    Log.d("Helo", "${response.body()}")
                    Log.d("Helo", "$response")
                    Log.d("Helo", "${response.body()!!.total_entries}")

                    val restaurantSize = response.body()!!.restaurants.size

                    Log.d("Helo", "size; $restaurantSize")
                    for (i in 0 until restaurantSize) {
                        val apiRestaurant = response.body()!!.restaurants[i]
                        tempListApiRestaurant += apiRestaurant
                    }
                    apiRestaurantsByCountries.value = tempListApiRestaurant
                } catch (e: Exception) {
                    Log.d("Helo", "Onresponse catch: ${e.message}")
                }
            }

            override fun onFailure(call: Call<ApiRestaurantResponse>, t: Throwable) {
                Log.d("Helo", "onfailure - all restaurant: ${t.message}")
            }
        })
    }


    fun getStats() {
        viewModelScope.launch {
            val result = apiRepository.getStats()
            result.enqueue(object : Callback<ApiRestaurant> {
                override fun onFailure(call: Call<ApiRestaurant>, t: Throwable) {
                    Log.d("Helo", "onfailure - one restaurant: ${t.message}")
                }

                override fun onResponse(
                    call: Call<ApiRestaurant>,
                    response: Response<ApiRestaurant>
                ) {
                    Log.d("Helo", "onResponse: ${response.body()}")
                }
            })
        }
    }
}